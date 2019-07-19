package com.anthemengineering.sox;

import com.anthemengineering.sox.jna.SoxLibrary;
import com.anthemengineering.sox.jna.sox_effects_chain_t;
import com.anthemengineering.sox.jna.sox_format_t;
import com.sun.jna.Pointer;

public class SoxMain {

    public static final String SOURCE_PATH = "src/test/resources/ascending-fifths.wav";
    public static final String DESTINATION_PATH = "output.wav";

    public static void main(String[] args) {
        SoxLibrary.INSTANCE.sox_init();
        System.out.println("Initialized sox");

        sox_format_t source = SoxLibrary.INSTANCE.sox_open_read(SOURCE_PATH, null, null, null);
        System.out.println("Opened " + SOURCE_PATH + ": " + source);

        sox_format_t destination = SoxLibrary.INSTANCE.sox_open_write(DESTINATION_PATH, source.signal, null, null, null, new SoxLibrary.sox_open_write_overwrite_permitted_callback() {
            @Override
            public int apply(Pointer filename) {
                // allow overwriting
                return 1;
            }
        });
        System.out.println("Opened " + DESTINATION_PATH + ": " + destination);

        sox_effects_chain_t chain = SoxLibrary.INSTANCE.sox_create_effects_chain(source.encoding, destination.encoding);
        System.out.println("Created effects chain: " + chain);
    }
}
