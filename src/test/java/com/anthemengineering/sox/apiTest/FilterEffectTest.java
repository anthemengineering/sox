package com.anthemengineering.sox.apiTest;

import com.anthemengineering.sox.effects.HighpassFilter;
import com.anthemengineering.sox.effects.LowpassFilter;
import com.anthemengineering.sox.effects.utils.Filter;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FilterEffectTest {
    @Test
    public void shouldBuildHighPass() {
        Filter effect = new HighpassFilter()
                .pole(Filter.Pole.SINGLE_POLE)
                .pole(Filter.Pole.DOUBLE_POLE)
                .width("123")
                .frequency("1000");

        assertThat(effect.getName()).isEqualTo("highpass");
        assertThat(effect.getOptionsList())
                .containsExactly("-2", "1000", "123");
    }

    @Test
    public void shouldBuildLowPass() {
        Filter effect = new LowpassFilter()
                .pole(Filter.Pole.DOUBLE_POLE)
                .pole(Filter.Pole.SINGLE_POLE)
                .width("123")
                .frequency("1000");

        assertThat(effect.getName()).isEqualTo("lowpass");
        assertThat(effect.getOptionsList())
                .containsExactly("-1", "1000", "123");
    }
}
