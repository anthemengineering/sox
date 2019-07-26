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

import com.anthemengineering.sox.effects.Flanger;
import com.anthemengineering.sox.effects.HighpassFilter;
import com.anthemengineering.sox.format.FileSink;
import com.anthemengineering.sox.format.FileSource;
import com.anthemengineering.sox.inprocess.InProcessExecutor;

public class SoxEffectsChainMain {
    public static void main(String[] args) {
        InProcessExecutor.executeNow(SoxEffectsChainBuilder.of()
                .source(new FileSource().path("src/test/resources/ascending-fifths.wav"))
                .sink(new FileSink().path("target/output.wav").allowOverwrite())
                .effect(new HighpassFilter().frequency("1000"))
                .effect(new Flanger()));
    }
}
