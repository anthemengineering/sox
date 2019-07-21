package com.anthemengineering.sox;

import com.anthemengineering.sox.effects.Flanger;
import com.anthemengineering.sox.effects.HighpassFilter;
import com.anthemengineering.sox.format.FileSink;
import com.anthemengineering.sox.format.FileSource;

public class SoxEffectsChainMain {
    public static void main(String[] args) {
        SoxEffectsChain.builder()
                .source(new FileSource().path("src/test/resources/ascending-fifths.wav"))
                .sink(new FileSink().path("target/output.wav").allowOverwrite())
                .effect(new HighpassFilter().frequency("1000"))
                .effect(new Flanger())
                .build()
                .flowEffects()
                .close();
    }
}
