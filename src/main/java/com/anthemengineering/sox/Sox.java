package com.anthemengineering.sox;

import com.anthemengineering.sox.jna.*;
import com.sun.jna.Pointer;

import static com.anthemengineering.sox.jna.SoxLibrary.sox_error_t.SOX_SUCCESS;

public final class Sox {
    static {
        SoxLibManagement.initialize(); // ensures initialized
    }

    private Sox() {

    }

    public static sox_format_t openRead(String path, sox_signalinfo_t signal, sox_encodinginfo_t encoding, String filetype) {
        sox_format_t f = SoxLibrary.INSTANCE.sox_open_read(path, signal, encoding, filetype);

        if (f == null) {
            throw new SoxException("Could not open " + path + " for read");
        }

        return f;
    }

    public static sox_format_t openRead(String path) {
        return openRead(path, null, null, null);
    }

    public static sox_format_t openWrite(String path, sox_signalinfo_t signal, sox_encodinginfo_t encoding, String filetype, sox_oob_t oob, boolean overwrite) {
        final boolean o = overwrite;
        sox_format_t f = SoxLibrary.INSTANCE.sox_open_write(path, signal, encoding, filetype, oob, new SoxLibrary.sox_open_write_overwrite_permitted_callback() {
            @Override
            public int apply(Pointer filename) {
                return o ? 1 : 0;
            }
        });

        if (f == null) {
            throw new SoxException("Could not open " + path + " for write");
        }

        return f;
    }

    public static sox_format_t openWrite(String path, sox_signalinfo_t signal, boolean overwrite) {
        return openWrite(path, signal, null, null, null, overwrite);
    }

    public static sox_format_t openRead(Pointer source, size_t sourceBufferSize, sox_signalinfo_t signal, sox_encodinginfo_t encoding, String filetype) {
        sox_format_t f = SoxLibrary.INSTANCE.sox_open_mem_read(source, sourceBufferSize, signal, encoding, filetype);

        if (f == null) {
            throw new SoxException("Could not open source memory for read");
        }

        return f;
    }

    public static sox_format_t openRead(Pointer source, size_t sourceBufferSize) {
        return openRead(source, sourceBufferSize, null, null, null);
    }

    public static sox_format_t openWrite(Pointer dest, size_t destBufferSize, sox_signalinfo_t signal, sox_encodinginfo_t encoding, String filetype, sox_oob_t oob) {
        sox_format_t f = SoxLibrary.INSTANCE.sox_open_mem_write(dest, destBufferSize, signal, encoding, filetype, oob);

        if (f == null) {
            throw new SoxException("Could not open source memory for write");
        }

        return f;
    }

    public static sox_format_t openWrite(Pointer dest, size_t destBufferSize, sox_signalinfo_t signal) {
        return openWrite(dest, destBufferSize, signal, null, null, null);
    }

    public static void close(sox_format_t format) {
        if (SoxLibrary.INSTANCE.sox_close(format) != SOX_SUCCESS) {
            throw new SoxException("Could not close " + format);
        }
    }

    public static sox_effects_chain_t createEffectsChain(sox_encodinginfo_t inEncoding, sox_encodinginfo_t outEncoding) {
        sox_effects_chain_t chain = SoxLibrary.INSTANCE.sox_create_effects_chain(inEncoding, outEncoding);

        if (chain == null) {
            throw new SoxException("Could not create effects chain");
        }

        return chain;
    }

    public static void deleteEffectsChain(sox_effects_chain_t chain) {
        SoxLibrary.INSTANCE.sox_delete_effects_chain(chain);
    }

    public static sox_effect_t createEffect(String name, String... options) {
        sox_effect_handler_t handler = SoxLibrary.INSTANCE.sox_find_effect(name);

        if (handler == null) {
            throw new SoxException("Could not find effects handler with name " + name);
        }

        sox_effect_t effect = SoxLibrary.INSTANCE.sox_create_effect(handler);

        if (effect == null) {
            throw new SoxException("Could not create effects handler for effect with name " + name);
        }

        if (SoxLibrary.INSTANCE.sox_effect_options(effect, options != null ? options.length : 0, options) != SOX_SUCCESS) {
            throw new SoxException("Could not set effects options on effect with name " + name);
        }

        return effect;
    }

    public static sox_effect_t createEffect(String name) {
        return createEffect(name, new String[]{});
    }

    public static sox_effect_t createInputEffect(sox_format_t input) {
        sox_effect_handler_t handler = SoxLibrary.INSTANCE.sox_find_effect("input");

        if (handler == null) {
            throw new SoxException("Could not find effects handler with name input");
        }

        sox_effect_t inputEffect = SoxLibrary.INSTANCE.sox_create_effect(handler);

        if (SoxLibrary.INSTANCE.sox_effect_options(inputEffect, 1, new Pointer[] { input.getPointer() }) != SOX_SUCCESS) {
            throw new SoxException("Could not create input effect");
        }

        return inputEffect;
    }

    public static sox_effect_t createOutputEffect(sox_format_t output) {
        sox_effect_handler_t handler = SoxLibrary.INSTANCE.sox_find_effect("output");

        if (handler == null) {
            throw new SoxException("Could not find effects handler with name output");
        }

        sox_effect_t outputEffect = SoxLibrary.INSTANCE.sox_create_effect(handler);

        if (SoxLibrary.INSTANCE.sox_effect_options(outputEffect, 1, new Pointer[] { output.getPointer() }) != SOX_SUCCESS) {
            throw new SoxException("Could not create output effect");
        }

        return outputEffect;
    }

    public static sox_effects_chain_t addEffect(sox_effects_chain_t chain, sox_effect_t effp, sox_signalinfo_t in, sox_signalinfo_t out) {
        if (SoxLibrary.INSTANCE.sox_add_effect(chain, effp, in, out) != SOX_SUCCESS) {
            throw new SoxException("Could not add effect to chain");
        }

        return chain;
    }

    public static void flowEffects(sox_effects_chain_t chain, SoxLibrary.sox_flow_effects_callback callback, Pointer client_data) {
        if (SoxLibrary.INSTANCE.sox_flow_effects(chain, null, null) != SOX_SUCCESS) {
            throw new SoxException("Could not flow effects");
        }
    }

    public static void flowEffects(sox_effects_chain_t chain) {
        flowEffects(chain, null, null);
    }
}
