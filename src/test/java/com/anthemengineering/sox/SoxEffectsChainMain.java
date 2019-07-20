package com.anthemengineering.sox;

public class SoxEffectsChainMain {
    public static void main(String[] args) {
        SoxEffectsChain.builder()
                       .source("src/test/resources/ascending-fifths.wav")
                       .destination("output.wav")
                       .overwriteDestination(true)
                       .effect("highpass", "1000")
                       .effect("flanger")
                       .build()
                       .flowEffects();
    }
}
