/*
 *  Copyright 2019 Anthem Engineering LLC.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.anthemengineering.sox.intraprocess;

import com.anthemengineering.sox.SoxEffectsChainBuilder;
import com.anthemengineering.sox.SoxEffectsChainExecutor;
import com.anthemengineering.sox.format.ByteBufferSinkSource;
import com.anthemengineering.sox.utils.SoxException;
import com.zaxxer.nuprocess.NuAbstractProcessHandler;
import com.zaxxer.nuprocess.NuProcess;
import com.zaxxer.nuprocess.NuProcessBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

import static com.anthemengineering.sox.intraprocess.SoxProcessTimeoutException.NU_TIMEOUT;
import static com.anthemengineering.sox.utils.ValidationUtil.nonNull;

public class IntraProcessExecutor implements SoxEffectsChainExecutor {
    private static final ScheduledExecutorService cleanupService = Executors.newSingleThreadScheduledExecutor();

    private TimeUnit maxProcessTimeoutUnit;
    private long maxProcessTimeoutTime;

    public IntraProcessExecutor() {
        this(10, TimeUnit.MINUTES);
    }

    public IntraProcessExecutor(long maxProcessTimeoutTime, TimeUnit maxProcessTimeoutUnit) {
        this.maxProcessTimeoutTime = maxProcessTimeoutTime;
        this.maxProcessTimeoutUnit = maxProcessTimeoutUnit;
    }

    public void executeNow(SoxEffectsChainBuilder soxEffectsChain) {
        try {
            execute(soxEffectsChain).get();
        } catch (InterruptedException e) {
            throw new SoxException("Error executing", e);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof SoxProcessTimeoutException) {
                SoxProcessTimeoutException toe = (SoxProcessTimeoutException) e.getCause();
                throw new SoxProcessTimeoutException(toe);
            } else if (e.getCause() instanceof SoxProcessException) {
                throw new SoxProcessException((SoxProcessException) e.getCause());
            }

            throw new SoxException("Error executing", e);
        }
    }

    @Override
    public CompletableFuture<Void> execute(SoxEffectsChainBuilder soxEffectsChain) {
        List<String> commandLine = buildCommandLine(soxEffectsChain);
        NuProcessBuilder processBuilder = new NuProcessBuilder(commandLine);

        // Setup event handling from the sox process (as the pipe fills up we need to read from it)
        ProcessHandler processHandler = new ProcessHandler(getSinkAudioStream(soxEffectsChain), commandLine);

        // assign the listener
        processBuilder.setProcessListener(processHandler);

        // vfork + execute
        NuProcess process = processBuilder.start();

        // this is outbound audio, may be null if we passed it in as a commandline argument
        ByteBuffer sourceAudioBuffer = getSourceAudioBuffer(soxEffectsChain);

        // file based this will be null otherwise we have it all in memory.
        if (sourceAudioBuffer != null) {
            // note this is asynchronous, and nuProcess takes care of pipe buffering for us,
            // we just can't modify the ByteBuffer until its done.
            // Also note, NuProcess can use directBuffer for more efficient operations
            process.writeStdin(sourceAudioBuffer);

            // close stdin after we are done writing
            // false means to write till write is finished, this call is still
            // asynchronous
            process.closeStdin(false);
        }

        return processHandler.result;
    }

    private ByteBuffer getSourceAudioBuffer(SoxEffectsChainBuilder soxEffectsChain) {
        if (soxEffectsChain.getSource() instanceof ByteBufferSinkSource) {
            return ((ByteBufferSinkSource) soxEffectsChain.getSource()).getBuffer();
        }

        // file based is passed in as arguments
        return null;
    }

    private OutputStream getSinkAudioStream(SoxEffectsChainBuilder soxEffectsChain) {
        if (soxEffectsChain.getSink() instanceof ByteBufferSinkSource) {
            return new ByteBufferedOutputStream(((ByteBufferSinkSource) soxEffectsChain.getSink()).getBuffer());
        }

        // file based is passed in as arguments
        return null;
    }

    private List<String> buildCommandLine(SoxEffectsChainBuilder soxEffectsChain) {
        List<String> commandLine = new ArrayList<>(estimateSize(soxEffectsChain));
        commandLine.add(getBinaryName());

        addSource(commandLine, soxEffectsChain);
        addSink(commandLine, soxEffectsChain);
        addEffects(commandLine, soxEffectsChain);

        return commandLine;
    }

    private void addSource(List<String> commandLine, SoxEffectsChainBuilder soxEffectsChain) {
        nonNull(soxEffectsChain.getSource(), "Source is required to be specified.");
        if (soxEffectsChain.getSource().getPath() != null) {
            commandLine.add(asString(soxEffectsChain.getSource().getPath()));
        } else {
            commandLine.add("-");
        }
    }

    private void addSink(List<String> commandLine, SoxEffectsChainBuilder soxEffectsChain) {
        nonNull(soxEffectsChain.getSink(), "Sink is required to be specified.");
        if (soxEffectsChain.getSink().getPath() != null) {
            commandLine.add(asString(soxEffectsChain.getSink().getPath()));
        } else {
            commandLine.addAll(Arrays.asList("-t", "wav", "-"));
        }
    }

    private void addEffects(List<String> commandLine, SoxEffectsChainBuilder soxEffectsChain) {
        for (SoxEffectsChainBuilder.Effect effect : soxEffectsChain.getEffects()) {
            commandLine.add(effect.name);
            commandLine.addAll(Arrays.asList(effect.options));
        }
    }

    private String asString(Path p) {
        return p.toAbsolutePath().toString();
    }

    private int estimateSize(SoxEffectsChainBuilder soxEffectsChain) {
        // assume 3 arguments per effect plus 'binary name', 'input, 'output'
        return soxEffectsChain.getEffects().size() * 3 + 3;
    }

    private String getBinaryName() {
        return "sox";
    }

    private class ProcessHandler extends NuAbstractProcessHandler {
        // may be null if the output was passed in to sox as a filename
        private final OutputStream sinkAudioStream;
        CompletableFuture<Void> result;
        List<String> commandLine;
        ByteArrayOutputStream errorStream = new ByteArrayOutputStream(256);
        NuProcess nuProcess;
        byte[] bufferBuffer = new byte[1024];
        byte[] errorBuffer = new byte[256];

        private ProcessHandler(
                OutputStream sinkAudioStream,
                List<String> commandLine) {
            this.sinkAudioStream = sinkAudioStream;
            this.commandLine = commandLine;
            this.result = new CompletableFuture<>();
        }

        @Override
        public void onStart(NuProcess nuProcess) {
            super.onStart(nuProcess);
            this.nuProcess = nuProcess;
            // we don't want the process to live for ever, so kill it after a reasonable time.
            cleanupService.schedule(this::close, maxProcessTimeoutTime, maxProcessTimeoutUnit);
        }

        @Override
        public void onExit(int exitStatus) {
            if (exitStatus != 0) {
                // generic error handler
                result.completeExceptionally(new SoxProcessException(exitStatus, getErrorString(), commandLine));
            } else {
                result.complete(null);
            }

            super.onExit(exitStatus);
        }

        @Override
        public void onStdout(ByteBuffer buffer, boolean closed) {
            if (sinkAudioStream != null) {
                writeToStream(buffer, bufferBuffer, sinkAudioStream);
            }
            // this does clean up of the buffer
            super.onStdout(buffer, closed);
        }

        private String getErrorString() {
            return errorStream.toString();
        }

        @Override
        public void onStderr(ByteBuffer buffer, boolean closed) {
            writeToStream(buffer, errorBuffer, errorStream);
            super.onStderr(buffer, closed);
        }

        private void close() {
            if (nuProcess.isRunning()) {
                // the process hasn't ended, we don't want a zombie, so force end it.
                int pid = nuProcess.getPID();
                result.completeExceptionally(new SoxProcessTimeoutException(
                        pid,
                        getErrorString(),
                        commandLine));

                try {
                    // note destroy call does not block and the process may live a short time while the OS kills
                    // the process.  And first lets try to gently kill the process, then do a 'force' kill.
                    nuProcess.destroy(false);
                    if (nuProcess.waitFor(500, TimeUnit.MILLISECONDS) == NU_TIMEOUT && nuProcess.isRunning()) {
                        // after the timeout, if its not destroyed, kill it.
                        nuProcess.destroy(true);
                    }
                } catch (Exception e) {
                    // log error
                    System.err.println("Unable to destroy process " + pid);
                }
            }
        }

        private void writeToStream(ByteBuffer buffer, byte[] innerBuffer, OutputStream out) {
            while (buffer.remaining() > 0) {
                int toRead = Math.min(buffer.remaining(), innerBuffer.length);
                buffer.get(innerBuffer, 0, toRead);
                try {
                    out.write(innerBuffer, 0, toRead);
                } catch (IOException e) {
                    // TODO better error
                    throw new SoxException("Unknown error", e);
                }
            }
        }
    }
}
