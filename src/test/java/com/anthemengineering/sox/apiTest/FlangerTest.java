package com.anthemengineering.sox.apiTest;

import com.anthemengineering.sox.effects.Flanger;
import org.junit.Test;

public class FlangerTest {
    @Test
    public void shouldConstruct() {
        Flanger flanger = new Flanger()
                .delay("1")
                .depth("1")
                .regen("1")
                .width("1")
                .speed("1")
                .shape("1")
                .phase("1")
                .interp("1");
    }
}
