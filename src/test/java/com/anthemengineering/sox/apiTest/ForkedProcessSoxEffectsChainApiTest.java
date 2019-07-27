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

package com.anthemengineering.sox.apiTest;

import com.anthemengineering.sox.SoxEffectsChainBuilder;
import com.anthemengineering.sox.TestResource;
import com.anthemengineering.sox.effects.Flanger;
import com.anthemengineering.sox.effects.HighpassFilter;
import com.anthemengineering.sox.format.*;
import com.anthemengineering.sox.forkedprocess.ForkedProcessExecutor;
import com.anthemengineering.sox.forkedprocess.SoxProcessException;
import com.anthemengineering.sox.utils.SoxException;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

// TODO: Perform real tests
public class ForkedProcessSoxEffectsChainApiTest {
    private static final TestResource ascendingFifths = new TestResource("/ascending-fifths.wav");
    private static ForkedProcessExecutor executor;

    @BeforeClass
    public static void setup() throws IOException {
        Files.createDirectories(Paths.get("target/test-output"));
        executor = new ForkedProcessExecutor(4, TimeUnit.SECONDS);
    }

    @Test
    public void shouldThrowWithoutArguments() {
        try {
            // TODO better error handling for obvious errors into the command line.
            // right now sox is just running
            executor.executeNow(SoxEffectsChainBuilder.of());

            failBecauseExceptionWasNotThrown(SoxException.class);
        } catch (SoxException e) {
            assertThat(e).hasMessage("Source is required to be specified.");
        }
    }

    @Test
    public void shouldTimeOutIfProcessTakesTooLong() {
        try {
            // TODO better error handling for obvious errors into the command line.
            // right now sox is just running
            ForkedProcessExecutor localExecutor = new ForkedProcessExecutor(100, TimeUnit.MILLISECONDS);
            localExecutor.executeNow(SoxEffectsChainBuilder.of()
                    .source(() -> null)
                    .sink(source -> null));

            failBecauseExceptionWasNotThrown(SoxException.class);
        } catch (SoxProcessException e) {
            assertThat(e).hasMessageMatching("Sox exited with exit code -2147483648: Sox pid=\\d+ process timeout\\:\\s*");
            assertThat(e.getCommandLine()).isEqualTo("\"sox\" \"-\" \"-t\" \"wav\" \"-\"");
        }
    }

    @Test
    public void shouldAllowReadAndWriteFiles() throws IOException {
        Path outPath = testPath("shouldAllowReadAndWriteFiles.wav");
        deleteSafe(outPath);

        executor.executeNow(SoxEffectsChainBuilder.of()
                .source(new FileSource().path("src/test/resources/ascending-fifths.wav"))
                .sink(new FileSink().path(outPath.toAbsolutePath()).allowOverwrite())
                .effect(new HighpassFilter().frequency("1000"))
                .effect(new Flanger()));

        // TODO: better test
        assertThat(Files.size(outPath)).isEqualTo(1071148);
    }

    @Test
    public void shouldAllowByteBufferToBeUsedAsSource() throws IOException {

        Path path = testPath("shouldAllowByteBufferToBeUsedAsSource.wav");
        deleteSafe(path);

        try {
            executor.executeNow(SoxEffectsChainBuilder.of()
                    .source(new ByteBufferSinkSource().buffer(ascendingFifths.asByteArray()))
                    .sink(new FileSink().path(path).allowOverwrite())
                    .effect(new HighpassFilter().frequency("1000"))
                    .effect(new Flanger()));

            byte[] buffer = Files.readAllBytes(path);

            assertThat(buffer)
                    .startsWith("RIFF".getBytes(StandardCharsets.US_ASCII));
        } catch (SoxProcessException e) {
            System.out.println(e.getCommandLine());

            throw e;
        }
    }

    @Test
    public void shouldAllowByteBufferToBeUsedAsSink() {
        java.nio.ByteBuffer buffer = java.nio.ByteBuffer.allocateDirect(ascendingFifths.size());
        testPath("shouldAllowByteBufferToBeUsedAsSink.wav");

        try {
            executor.executeNow(SoxEffectsChainBuilder.of()
                    .source(new FileSource().path("src/test/resources/ascending-fifths.wav"))
                    .sink(new ByteBufferSinkSource().buffer(buffer, ascendingFifths.size()))
                    .effect(new HighpassFilter().frequency("1000"))
                    .effect(new Flanger()));

            byte[] bufferBytes = new byte[ascendingFifths.size()];
            buffer.rewind();
            buffer.get(bufferBytes);

            assertThat(bufferBytes)
                    .startsWith("RIFF".getBytes(StandardCharsets.US_ASCII));
        } catch (SoxProcessException e) {
            System.out.println(e.getCommandLine());
            throw e;
        }
    }

    private void deleteSafe(Path p) {
        try {
            Files.deleteIfExists(p);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Path testPath(String p) {
        return Paths.get("target/test-output/", p);
    }
}
