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

package com.anthemengineering.sox.forkedprocess;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class ByteBufferedOutputStream extends OutputStream {
    private static final String NO_MORE_SPACE_IN_BUFFER = "No more space in buffer.";
    private final ByteBuffer wrapped;

    public ByteBufferedOutputStream(ByteBuffer bytes) {
        this.wrapped = bytes;
    }

    @Override
    public void write(int b) throws IOException {
        if (wrapped.remaining() == 0) {
            throw new IOException(NO_MORE_SPACE_IN_BUFFER);
        }
        wrapped.put((byte)b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if (wrapped.remaining() < len) {
            throw new IOException(NO_MORE_SPACE_IN_BUFFER);
        }
        wrapped.put(b, off, len);
    }
}