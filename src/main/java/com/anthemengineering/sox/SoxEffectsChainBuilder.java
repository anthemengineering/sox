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

package com.anthemengineering.sox;

import com.anthemengineering.sox.effects.SoxEffect;
import com.anthemengineering.sox.format.SoxSink;
import com.anthemengineering.sox.format.SoxSource;

import java.util.ArrayList;
import java.util.List;

public class SoxEffectsChainBuilder {
    private SoxSource source;
    private SoxSink sink;
    private final List<Effect> effects = new ArrayList<>();

    public static SoxEffectsChainBuilder of() {
        return new SoxEffectsChainBuilder();
    }

    protected SoxEffectsChainBuilder() {
        // no op
    }

    public static class Effect {
        public final String name;
        public final String[] options;

        private Effect(String name, String[] options) {
            this.name = name;
            this.options = options;
        }

        private Effect(String name) {
            this(name, new String[]{});
        }
    }

    public SoxEffectsChainBuilder source(SoxSource source) {
        this.source = source;

        return this;
    }

    public SoxEffectsChainBuilder sink(SoxSink sink) {
        this.sink = sink;

        return this;
    }

    public SoxEffectsChainBuilder effect(SoxEffect newEffect) {
        return effect(newEffect.getName(), newEffect.getOptionsList());
    }

    public SoxEffectsChainBuilder effect(String name, String... options) {
        effects.add(new Effect(name, options));
        return this;
    }

    public SoxEffectsChainBuilder effect(String name) {
        effects.add(new Effect(name));
        return this;
    }

    public SoxSource getSource() {
        return source;
    }

    public SoxSink getSink() {
        return sink;
    }

    public List<Effect> getEffects() {
        return effects;
    }
}
