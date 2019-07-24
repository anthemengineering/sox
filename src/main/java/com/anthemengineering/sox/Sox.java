/*
 *  Copyright 2019 Anthem Engineering LLC.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

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
        assertSuccess(SoxLibrary.INSTANCE.sox_close(format), "Could not close %1$s: %2$d", format);
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

        assertSuccess(
                SoxLibrary.INSTANCE.sox_effect_options(effect, options != null ? options.length : 0, options),
                "Could not set effects options on effect with name %1$s: %2$d",
                name);

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

        assertSuccess(
                SoxLibrary.INSTANCE.sox_effect_options(inputEffect, 1, new Pointer[] { input.getPointer() }),
                "Could not set effects options on effect with input %1$s: %2$d",
                input);

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

        assertSuccess(
                SoxLibrary.INSTANCE.sox_effect_options(outputEffect, 1, new Pointer[] { output.getPointer() }),
                "Could not create output effect with output %1$s: %2$d",
                output);

        return outputEffect;
    }

    public static sox_effects_chain_t addEffect(sox_effects_chain_t chain, sox_effect_t effp, sox_signalinfo_t in, sox_signalinfo_t out) {
        assertSuccess(
                SoxLibrary.INSTANCE.sox_add_effect(chain, effp, in, out),
                "Could not add effect to chain (%1$s, %2$s, %3$s, %4$s): %5$d",
                chain,
                effp,
                in,
                out);

        return chain;
    }

    public static void flowEffects(sox_effects_chain_t chain, SoxLibrary.sox_flow_effects_callback callback, Pointer client_data) {
        assertSuccess(
                SoxLibrary.INSTANCE.sox_flow_effects(chain, null, null),
                "Could not flow effects (%1$s): %2$d",
                chain);
    }

    public static void flowEffects(sox_effects_chain_t chain) {
        flowEffects(chain, null, null);
    }

    private static void assertSuccess(int result, String formatMsg, Object ... args) {
        if (result != SOX_SUCCESS) {
            Object[] fmtArgs = new Object[args.length + 1];
            System.arraycopy(args, 0, fmtArgs, 0, args.length);
            fmtArgs[fmtArgs.length - 1] = result;

            throw new SoxException(String.format(formatMsg, fmtArgs));
        }
    }
}
