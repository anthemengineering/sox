package com.anthemengineering.sox;

import com.anthemengineering.sox.jna.sox_effect_t;
import com.anthemengineering.sox.jna.sox_effects_chain_t;
import com.anthemengineering.sox.jna.sox_format_t;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public final class SoxEffectsChain implements Closeable {
    private final Sox sox;

    private final sox_format_t source;
    private final sox_format_t destination;
    private final sox_effects_chain_t chain;

    private SoxEffectsChain(Path source, Path destination, boolean overwriteDestination) {
        sox = new Sox();

        this.source = sox.openRead(source.toAbsolutePath().toString());
        this.destination = sox.openWrite(destination.toAbsolutePath().toString(), this.source.signal, overwriteDestination);

        this.chain = sox.createEffectsChain(this.source.encoding, this.destination.encoding);
    }

    public static Builder builder() {
        return new Builder();
    }

    public void flowEffects() {
        sox.flowEffects(chain);
    }

    @Override
    public void close() throws IOException {
        sox.deleteEffectsChain(chain);
        sox.close(source);
        sox.close(destination);
    }

    private void addInputEffect() {
        sox_effect_t inputEffect = sox.createInputEffect(source);
        sox.addEffect(chain, inputEffect, source.signal, source.signal);
    }

    private void addOutputEffect() {
        sox_effect_t outputEffect = sox.createOutputEffect(destination);
        sox.addEffect(chain, outputEffect, source.signal, source.signal);
    }

    private void addEffect(String name, String... options) {
        sox_effect_t effect = sox.createEffect(name, options);
        sox.addEffect(chain, effect, source.signal, source.signal);
    }

    public static class Builder {
        private Path source;
        private Path destination;
        private boolean overwriteDestination;
        private List<Effect> effects = new ArrayList<>();

        private Builder() {
            // no op
        }

        private static class Effect {
            private final String name;
            private final String[] options;

            private Effect(String name, String[] options) {
                this.name = name;
                this.options = options;
            }

            private Effect(String name) {
                this(name, new String[]{});
            }
        }

        public Builder source(Path source) {
            this.source = source;
            return this;
        }

        public Builder source(String source) {
            return source(Paths.get(source));
        }

        public Builder destination(Path destination) {
            this.destination = destination;
            return this;
        }

        public Builder destination(String destination) {
            return this.destination(Paths.get(destination));
        }

        public Builder overwriteDestination(boolean overwriteDestination) {
            this.overwriteDestination = overwriteDestination;
            return this;
        }

        public Builder effect(String name, String... options) {
            effects.add(new Effect(name, options));
            return this;
        }

        public Builder effect(String name) {
            effects.add(new Effect(name));
            return this;
        }

        public SoxEffectsChain build() {
            SoxEffectsChain soxEffectsChain = new SoxEffectsChain(source, destination, overwriteDestination);

            soxEffectsChain.addInputEffect();
            for (Effect effect : effects) {
                soxEffectsChain.addEffect(effect.name, effect.options);
            }
            soxEffectsChain.addOutputEffect();

            return soxEffectsChain;
        }
    }

}
