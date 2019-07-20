package com.anthemengineering.sox;

import com.anthemengineering.sox.jna.*;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

import static com.anthemengineering.sox.jna.SoxLibrary.sox_error_t.SOX_SUCCESS;

public class SoxMain {

    public static final String SOURCE_PATH = "src/test/resources/ascending-fifths.wav";
    public static final String DESTINATION_PATH = "output.wav";

    public static void main(String[] args) {
        assert SoxLibrary.INSTANCE.sox_init() == SOX_SUCCESS;
        System.out.println("Initialized sox");

        sox_format_t source = SoxLibrary.INSTANCE.sox_open_read(SOURCE_PATH, null, null, null);
        assert source != null;
        System.out.println("Opened " + SOURCE_PATH + ": " + source);

        sox_format_t destination = SoxLibrary.INSTANCE.sox_open_write(DESTINATION_PATH, source.signal, null, null, null, new SoxLibrary.sox_open_write_overwrite_permitted_callback() {
            @Override
            public int apply(Pointer filename) {
                // allow overwriting
                return 1;
            }
        });
        assert destination != null;
        System.out.println("Opened " + DESTINATION_PATH + ": " + destination);

        sox_effects_chain_t chain = SoxLibrary.INSTANCE.sox_create_effects_chain(source.encoding, destination.encoding);
        System.out.println("Created effects chain: " + chain);

        sox_effect_handler_t inputEffectHandler = SoxLibrary.INSTANCE.sox_find_effect("input");
        sox_effect_t inputEffect = SoxLibrary.INSTANCE.sox_create_effect(inputEffectHandler);
        assert SoxLibrary.INSTANCE.sox_effect_options(inputEffect, 1, new Pointer[] { source.getPointer() }) == SOX_SUCCESS;
        System.out.println("Created input effect: " + inputEffect);

        SoxLibrary.INSTANCE.sox_add_effect(chain, inputEffect, source.signal, source.signal);
        System.out.println("Added input effect to chain");

        sox_effect_handler_t flangerEffectHandler = SoxLibrary.INSTANCE.sox_find_effect("flanger");
        sox_effect_t flangerEffect = SoxLibrary.INSTANCE.sox_create_effect(flangerEffectHandler);
        assert SoxLibrary.INSTANCE.sox_effect_options(flangerEffect, 0, (String[]) null) == SOX_SUCCESS;
        System.out.println("Created flanger effect: " + flangerEffect);

        SoxLibrary.INSTANCE.sox_add_effect(chain, flangerEffect, source.signal, source.signal);
        System.out.println("Added flanger effect to chain");

        sox_effect_handler_t outputEffectHandler = SoxLibrary.INSTANCE.sox_find_effect("output");
        sox_effect_t outputEffect = SoxLibrary.INSTANCE.sox_create_effect(outputEffectHandler);
        assert SoxLibrary.INSTANCE.sox_effect_options(outputEffect, 1, new Pointer[] { destination.getPointer() }) == SOX_SUCCESS;
        System.out.println("Created output effect: " + outputEffect);

        SoxLibrary.INSTANCE.sox_add_effect(chain, outputEffect, source.signal, source.signal);
        System.out.println("Added output effect to chain");

        SoxLibrary.INSTANCE.sox_flow_effects(chain, null, null);
        System.out.println("Flowed chain");
    }
}
