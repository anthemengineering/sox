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

package com.anthemengineering.sox.jna;

import com.sun.jna.IntegerType;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

public class size_t extends IntegerType {
    public size_t() { this(0); }
    public size_t(long value) { super(Native.SIZE_T_SIZE, value); }

    public static class ByReference extends com.sun.jna.ptr.ByReference {
        public ByReference() {
            this(new size_t());
        }

        public ByReference(size_t value) {
            super(Native.SIZE_T_SIZE);
            setValue(value);
        }

        public void setValue(size_t value) {
            Pointer p = getPointer();
            if (Native.SIZE_T_SIZE == 8) {
                p.setLong(0, value.longValue());
            } else {
                p.setInt(0, value.intValue());
            }
        }

        public size_t getValue() {
            Pointer p = getPointer();
            return new size_t(Native.SIZE_T_SIZE == 8 ? p.getLong(0) : p.getInt(0));
        }
    }
}
