package com.anthemengineering.sox;

import com.anthemengineering.sox.effects.SoxEffect;
import com.anthemengineering.sox.format.SoxSink;
import com.anthemengineering.sox.format.SoxSource;
import com.anthemengineering.sox.jna.sox_effect_t;
import com.anthemengineering.sox.jna.sox_effects_chain_t;
import com.anthemengineering.sox.jna.sox_format_t;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

import static com.anthemengineering.sox.ValidationUtil.nonNull;

public final class SoxEffectsChain implements Closeable {
    private final sox_format_t source;
    private final sox_format_t destination;

    private final sox_effects_chain_t chain;

    SoxEffectsChain(SoxSource source, SoxSink sink) {
        this.source = source.create();
        this.destination = sink.create(this.source);

        this.chain = Sox.createEffectsChain(this.source.encoding, this.destination.encoding);
    }

    public static Builder builder() {
        return new Builder();
    }

    public SoxEffectsChain flowEffects() {
        Sox.flowEffects(chain);
        return this;
    }

    @Override
    public void close() {
        Sox.deleteEffectsChain(chain);
        Sox.close(source);
        Sox.close(destination);
    }

    private void addInputEffect() {
        sox_effect_t inputEffect = Sox.createInputEffect(source);
        Sox.addEffect(chain, inputEffect, source.signal, source.signal);
    }

    private void addOutputEffect() {
        sox_effect_t outputEffect = Sox.createOutputEffect(destination);
        Sox.addEffect(chain, outputEffect, source.signal, source.signal);
    }

    private void addEffect(String name, String... options) {
        sox_effect_t effect = Sox.createEffect(name, options);
        Sox.addEffect(chain, effect, source.signal, source.signal);
    }

    public static class Builder {
        private SoxSource source;
        private SoxSink sink;
        private final List<Effect> effects = new ArrayList<>();

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

        public Builder source(SoxSource source) {
            this.source = source;

            return this;
        }

        public Builder sink(SoxSink sink) {
            this.sink = sink;

            return this;
        }

        public Builder effect(SoxEffect newEffect) {
            return effect(newEffect.getName(), newEffect.getOptionsList());
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
            SoxEffectsChain soxEffectsChain = new SoxEffectsChain(
                    nonNull(source, "Source is required to be specified"),
                    nonNull(sink, "Sink is required to be specified"));

            soxEffectsChain.addInputEffect();
            for (Effect effect : effects) {
                soxEffectsChain.addEffect(effect.name, effect.options);
            }
            soxEffectsChain.addOutputEffect();

            return soxEffectsChain;
        }
    }
}
