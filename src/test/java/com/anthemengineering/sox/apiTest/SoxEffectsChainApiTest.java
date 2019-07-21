package com.anthemengineering.sox.apiTest;

import com.anthemengineering.sox.SoxEffectsChain;
import com.anthemengineering.sox.SoxException;
import com.anthemengineering.sox.effects.HighpassFilter;
import com.anthemengineering.sox.effects.utils.Filter;
import org.junit.Test;

import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

// TODO: Perform real tests
public class SoxEffectsChainApiTest {
    @Test
    public void shouldMakeBuilder() {
        SoxEffectsChain.Builder builder = SoxEffectsChain.builder()
                .source(Paths.get("something"))
                .source("anotherPass")
                .destination(Paths.get("something"))
                .destination("anotherPass")
                .effect("highpass")
                .effect("highpass", "1000")
                .effect(new HighpassFilter()
                        .frequency("1000")
                        .pole(Filter.Pole.DOUBLE_POLE)
                        .width("10"))
                .overwriteDestination(true);

        assertThat(builder)
                .describedAs("The builder should be returned, ready to have its chain built.")
                .isNotNull();
    }

    @Test
    public void shouldThrowWithoutArguments() {
        try {
            SoxEffectsChain.builder().build().flowEffects();

            failBecauseExceptionWasNotThrown(SoxException.class);
        } catch (SoxException e) {
            assertThat(e).hasMessage("Source is required to be specified");
        }
    }

}
