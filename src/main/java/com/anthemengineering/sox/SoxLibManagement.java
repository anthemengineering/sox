package com.anthemengineering.sox;

import com.anthemengineering.sox.jna.SoxLibrary;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.anthemengineering.sox.jna.SoxLibrary.sox_error_t.SOX_SUCCESS;

public final class SoxLibManagement {
    private static final AtomicBoolean initialized = new AtomicBoolean(false);

    private SoxLibManagement() {}

    public static void initialize() {
        if (!initialized.getAndSet(true)) {
            // only initialize if we are the first
            if (SoxLibrary.INSTANCE.sox_init() != SOX_SUCCESS) {
                throw new SoxException("Could not initialize Sox");
            }
        }
    }

    public static void reset() {
        if (SoxLibrary.INSTANCE.sox_quit() != SOX_SUCCESS) {
            throw new SoxException("Could not quit Sox");
        }

        initialized.set(false);
    }
}
