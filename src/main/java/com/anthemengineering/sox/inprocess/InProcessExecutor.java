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

package com.anthemengineering.sox.inprocess;

import com.anthemengineering.sox.SoxEffectsChainBuilder;
import com.anthemengineering.sox.SoxEffectsChainExecutor;
import com.anthemengineering.sox.format.SoxSink;
import com.anthemengineering.sox.format.SoxSource;
import com.anthemengineering.sox.jna.sox_effect_t;
import com.anthemengineering.sox.jna.sox_effects_chain_t;
import com.anthemengineering.sox.jna.sox_format_t;
import com.anthemengineering.sox.utils.SoxException;

import java.io.Closeable;
import java.util.concurrent.*;

import static com.anthemengineering.sox.utils.ValidationUtil.nonNull;

public class InProcessExecutor implements SoxEffectsChainExecutor {
    private static final ExecutorService threadPool = Executors.newWorkStealingPool();

    @Override
    public CompletableFuture<Void> execute(SoxEffectsChainBuilder soxEffectsChain) {
        return CompletableFuture.supplyAsync(() -> executeAsync(soxEffectsChain), threadPool);
    }

    public void executeNow(SoxEffectsChainBuilder soxEffectsChain) {
        try {
            new InProcessExecutor().execute(soxEffectsChain).get(5, TimeUnit.SECONDS);
        } catch (InterruptedException|ExecutionException|TimeoutException e) {
            if (e.getCause() instanceof SoxException) {
                throw (SoxException) e.getCause();
            }

            throw new SoxException("Error executing", e);
        }
    }

    private Void executeAsync(SoxEffectsChainBuilder soxEffectsChain) {
        try  (SoxFormatClosable source = new SoxFormatClosable(soxEffectsChain.getSource());
              SoxFormatClosable sink = new SoxFormatClosable(source, soxEffectsChain.getSink());
              SoxChainClosable chain = new SoxChainClosable(source, sink)){

            // add input effect
            sox_effect_t inputEffect = Sox.createInputEffect(source.format);
            Sox.addEffect(chain.chain, inputEffect, source.format.signal, source.format.signal);

            // add all effects
            for (SoxEffectsChainBuilder.Effect effectDesc : soxEffectsChain.getEffects()) {
                sox_effect_t effect = Sox.createEffect(effectDesc.name, effectDesc.options);
                Sox.addEffect(chain.chain, effect, source.format.signal, source.format.signal);
            }

            // Add output effect
            sox_effect_t outputEffect = Sox.createOutputEffect(sink.format);
            Sox.addEffect(chain.chain, outputEffect, source.format.signal, source.format.signal);

            Sox.flowEffects(chain.chain);

            return null;
        }
    }

    private static class SoxFormatClosable implements Closeable {
        private sox_format_t format;

        private SoxFormatClosable(SoxSource source) {
            this(nonNull(source, "Source is required to be specified")
                    .create());
        }

        private SoxFormatClosable(SoxFormatClosable source, SoxSink sink) {
            this(nonNull(sink, "Sink is required to be specified")
                    .create(source.format));
        }

        private SoxFormatClosable(sox_format_t format) {
            this.format = format;
        }

        @Override
        public void close() {
            Sox.close(format);
        }
    }

    private static class SoxChainClosable implements Closeable {
        private sox_effects_chain_t chain;

        private SoxChainClosable(sox_effects_chain_t chain) {
            this.chain = chain;
        }

        private SoxChainClosable(SoxFormatClosable source, SoxFormatClosable sink) {
            this(Sox.createEffectsChain(source.format.encoding, sink.format.encoding));
        }

        @Override
        public void close() {
            Sox.deleteEffectsChain(chain);
        }
    }
}
