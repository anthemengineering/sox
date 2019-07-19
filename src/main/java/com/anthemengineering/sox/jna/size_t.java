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
