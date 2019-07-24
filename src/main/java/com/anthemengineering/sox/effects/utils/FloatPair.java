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

package com.anthemengineering.sox.effects.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class FloatPair implements Comparable<FloatPair> {
    // DecimalFormat is not thread safe, so have to create a new instance each time
    private final DecimalFormat format = createFormat();

    private final Float first;
    private final Float second;

    public FloatPair(float first) {
        this.first = first;
        this.second = null;
    }

    public FloatPair(float first, float second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public String toString() {
        if (second != null) {
            return format.format(first) + "," + format.format(second);
        } else {
            return format.format(first);
        }
    }

    @Override
    public int compareTo(FloatPair o) {
        return first.compareTo(o.first);
    }

    public static DecimalFormat createFormat() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setInfinity("inf");
        return new DecimalFormat("0.###", symbols);
    }
}
