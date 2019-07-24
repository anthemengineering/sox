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

import com.anthemengineering.sox.SoxException;
import com.anthemengineering.sox.effects.Compand;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

public class CompandTest {
    @Test
    public void basic() {
        assertThat(new Compand()
                .addAttackDecay(0.3f, 0.002f)
                .addTransferFunction(0.001f)
                .getOptionsList())
                .containsExactly("0.3,0.002", "0.001");

    }

    @Test
    public void throwIfNoOptionsSpecified() {
        try {
            new Compand().getOptionsList();
            fail("Should throw");
        } catch (SoxException e) {
            assertThat(e).hasMessage("Must specify at least one attack/delay pair");
        }
    }

    @Test
    public void throwOnMissingTransferFunction() {
        try {
            new Compand()
                    .addAttackDecay(0.3f, 0.002f)
                    .getOptionsList();
            fail("Should throw");
        } catch (SoxException e) {
            assertThat(e).hasMessage("Must at least specify a single in-db");
        }
    }

    @Test
    public void shouldBuildTransferFunctionsProperly() {
        assertThat(new Compand()
                .addAttackDecay(1f, 1f)
                .addTransferFunction(0.001f)
                .addTransferFunction(0.004f, 0.005f)
                .addTransferFunction(0.002f, 0.003f)
                .getOptionsList())
                .containsExactly(
                        "1,1",
                        "0.001,0.002,0.003,0.004,0.005");
    }

    @Test
    public void shouldBuildTransferFunctionsProperlyWithKnee() {
        assertThat(new Compand()
                .addAttackDecay(1f, 1f)
                .addTransferFunction(0.001f)
                .addTransferFunction(0.004f, 0.005f)
                .addTransferFunction(0.002f, 0.003f)
                .softKneeDb(0.01f)
                .getOptionsList())
                .containsExactly(
                        "1,1",
                        "0.01:0.001,0.002,0.003,0.004,0.005");
    }

    @Test
    public void shouldBuildUpGainOptionalGainArgumentsPositional() {
        try {
            new Compand()
                    .addAttackDecay(0.3f, 0.002f)
                    .addTransferFunction(0.001f)
                    .initialVolumeDb(0.1f) // should make throw since no gain
                    .getOptionsList();
        } catch (SoxException e) {
            assertThat(e).hasMessage("Option 'initial-volume-dB' requires the previous option 'gain' to be set.");
        }

        try {
            new Compand()
                    .addAttackDecay(0.3f, 0.002f)
                    .addTransferFunction(0.001f)
                    .gainDb(0.1f)
                    .delaySeconds(0.2f) // should make throw since no initial
                    .getOptionsList();
        } catch (SoxException e) {
            assertThat(e).hasMessage("Option 'delay' requires the previous option 'initial-volume-dB' to be set.");
        }
    }


    @Test
    public void shouldBuildAllowGain() {
        assertThat(new Compand()
                .addAttackDecay(1f, 1f)
                .addTransferFunction(1f)
                .gainDb(0.1f)
                .getOptionsList())
        .containsExactly("1,1", "1", "0.1");

        assertThat(new Compand()
                .addAttackDecay(1f, 1f)
                .addTransferFunction(1f)
                .gainDb(0.1f)
                .initialVolumeDb(0.2f)
                .getOptionsList())
                .containsExactly("1,1", "1", "0.1", "0.2");

        assertThat(new Compand()
                .addAttackDecay(1f, 1f)
                .addTransferFunction(1f)
                .gainDb(0.1f)
                .initialVolumeDb(0.2f)
                .delaySeconds(0.3f)
                .getOptionsList())
                .containsExactly("1,1", "1", "0.1", "0.2", "0.3");
    }

    @Test
    public void shouldAllowExampleInDocumentation() {
        // .1,.1 −45.1,−45,−inf,0,−inf 45 −90 .1
        assertThat(new Compand()
                .addAttackDecay(.1f, .1f)
                .addTransferFunction(-45.1f)
                .addTransferFunction(-45, Float.NEGATIVE_INFINITY)
                .addTransferFunction(0, Float.NEGATIVE_INFINITY)
                .gainDb(45)
                .initialVolumeDb(-90)
                .delaySeconds(0.1f)
                .getOptionsList())
                .containsExactly("0.1,0.1",
                        "-45.1,-45,-inf,0,-inf",
                        "45",
                        "-90",
                        "0.1");

        // 0.3,1 6:−70,−60,−20 −5 −90 0.2
        assertThat(new Compand()
                .addAttackDecay(.3f, 1f)
                .softKneeDb(6)
                .addTransferFunction(-70)
                .addTransferFunction(-60, -20)
                .gainDb(-5)
                .initialVolumeDb(-90)
                .delaySeconds(0.2f)
                .getOptionsList())
                .containsExactly("0.3,1",
                        "6:-70,-60,-20",
                        "-5",
                        "-90",
                        "0.2");
    }
}
