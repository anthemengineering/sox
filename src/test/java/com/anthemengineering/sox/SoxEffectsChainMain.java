package com.anthemengineering.sox;

import com.anthemengineering.sox.effects.Flanger;
import com.anthemengineering.sox.effects.Highpass;

public class SoxEffectsChainMain {
    public static void main(String[] args) {
        SoxEffectsChain.builder()
                       .source("src/test/resources/ascending-fifths.wav")
                       .destination("target/output.wav")
                       .overwriteDestination(true)
                       .effect(new Highpass().frequency("1000"))
                       .effect(new Flanger())
                       .build()
                       .flowEffects()
                       .close();
    }
}
