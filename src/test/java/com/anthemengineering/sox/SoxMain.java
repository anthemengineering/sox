package com.anthemengineering.sox;

import com.anthemengineering.sox.jna.sox_effect_t;
import com.anthemengineering.sox.jna.sox_effects_chain_t;
import com.anthemengineering.sox.jna.sox_format_t;

public class SoxMain {

    private static final String SOURCE_PATH = "src/test/resources/ascending-fifths.wav";
    private static final String DESTINATION_PATH = "target/output.wav";

    public static void main(String[] args) {
        System.out.println("Initialized Sox");

        sox_format_t source = Sox.openRead(SOURCE_PATH);
        System.out.println("Opened " + SOURCE_PATH + ": " + source);

        sox_format_t destination = Sox.openWrite(DESTINATION_PATH, source.signal, true);
        System.out.println("Opened " + DESTINATION_PATH + ": " + destination);

        sox_effects_chain_t chain = Sox.createEffectsChain(source.encoding, destination.encoding);
        System.out.println("Created effects chain: " + chain);

        sox_effect_t inputEffect = Sox.createInputEffect(source);
        Sox.addEffect(chain, inputEffect, source.signal, source.signal);
        System.out.println("Added input effect to chain");

        sox_effect_t highpassEffect = Sox.createEffect("highpass", "1000");
        Sox.addEffect(chain, highpassEffect, source.signal, source.signal);
        System.out.println("Added highpass effect to chain");

        sox_effect_t flangerEffect = Sox.createEffect("flanger");
        Sox.addEffect(chain, flangerEffect, source.signal, source.signal);
        System.out.println("Added flanger effect to chain");

        sox_effect_t outputEffect = Sox.createOutputEffect(destination);
        Sox.addEffect(chain, outputEffect, source.signal, source.signal);
        System.out.println("Added output effect to chain");

        Sox.flowEffects(chain);
        System.out.println("Flowed chain");

        Sox.deleteEffectsChain(chain);
        Sox.close(source);
        Sox.close(destination);
        System.out.println("Cleaned up");
    }
}
