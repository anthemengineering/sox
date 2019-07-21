package com.anthemengineering.sox.format;

import com.anthemengineering.sox.Sox;
import com.anthemengineering.sox.jna.size_t;
import com.anthemengineering.sox.jna.sox_format_t;
import com.anthemengineering.sox.jna.sox_signalinfo_t;
import com.sun.jna.Native;

import java.nio.ByteBuffer;

public class InMemory implements SoxSource, SoxSink {
    private ByteBuffer buffer;
    private long bufferSize;

    public InMemory buffer(byte[] buffer) {
        this.buffer = ByteBuffer.allocateDirect(buffer.length);
        this.buffer.put(buffer);

        return this;
    }

    public InMemory buffer(ByteBuffer buffer, long bufferSize) {
        if (!buffer.isDirect()) {
            return buffer(buffer.array());
        } else {
            this.buffer = buffer;
            this.bufferSize = bufferSize;
        }

        return this;
    }

    @Override
    public sox_format_t create() {
        return Sox.openRead(Native.getDirectBufferPointer(buffer), new size_t(bufferSize));
    }


    @Override
    public sox_format_t create(sox_signalinfo_t signal) {
        return Sox.openWrite(Native.getDirectBufferPointer(buffer), new size_t(bufferSize), signal);
    }
}
