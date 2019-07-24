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
