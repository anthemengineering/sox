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

package com.anthemengineering.sox.effects;

import com.anthemengineering.sox.effects.utils.OptList;

/**
 * flanger [delay depth regen width speed shape phase interp]
 * <p>
 * Apply a flanging effect to the audio. See [3] for a detailed description of flanging.
 * <p>
 * All parameters are optional (right to left).
 *
 * Documentation is from: http://sox.sourceforge.net/sox.html on 2019-07-21.
 */
public class Flanger implements SoxEffect {
    @Override
    public String getName() {
        return "flanger";
    }

    private static String[] ARG_ORDER = new String[]{
            "delay",
            "depth",
            "regen",
            "width",
            "speed",
            "shape",
            "phase",
            "interp"
    };

    private String delay;
    private String depth;
    private String regen;
    private String width;
    private String speed;
    private String shape;
    private String phase;
    private String interp;

    public Flanger delay(String delay) {
        this.delay = delay;
        return this;
    }

    public Flanger depth(String depth) {
        this.depth = depth;
        return this;
    }

    public Flanger regen(String regen) {
        this.regen = regen;
        return this;
    }

    public Flanger width(String width) {
        this.width = width;
        return this;
    }

    public Flanger speed(String speed) {
        this.speed = speed;
        return this;
    }

    public Flanger shape(String shape) {
        this.shape = shape;
        return this;
    }

    public Flanger phase(String phase) {
        this.phase = phase;
        return this;
    }

    public Flanger interp(String interp) {
        this.interp = interp;
        return this;
    }

    @Override
    public String[] getOptionsList() {
        OptList optList = new OptList();
        optList.addOrderArguments(
                ARG_ORDER,
                delay,
                depth,
                regen,
                width,
                speed,
                shape,
                phase,
                interp);

        return optList.toStringArray();
    }
}
