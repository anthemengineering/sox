package com.anthemengineering.sox.apiTest;

import com.anthemengineering.sox.effects.Highpass;
import com.anthemengineering.sox.effects.Lowpass;
import com.anthemengineering.sox.effects.utils.Pass;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PassEffectTest {
    @Test
    public void shouldBuildHighPass() {
        Pass effect = new Highpass()
                .pole(Pass.Pole.SINGLE_POLE)
                .pole(Pass.Pole.DOUBLE_POLE)
                .width("123")
                .frequency("1000");

        assertThat(effect.getName()).isEqualTo("highpass");
        assertThat(effect.getOptionsList())
                .containsExactly("-2", "1000", "123");
    }

    @Test
    public void shouldBuildLowPass() {
        Pass effect = new Lowpass()
                .pole(Pass.Pole.DOUBLE_POLE)
                .pole(Pass.Pole.SINGLE_POLE)
                .width("123")
                .frequency("1000");

        assertThat(effect.getName()).isEqualTo("lowpass");
        assertThat(effect.getOptionsList())
                .containsExactly("-1", "1000", "123");
    }
}
