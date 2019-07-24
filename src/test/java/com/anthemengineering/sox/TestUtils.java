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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

final class TestUtils {
    private static final int EOF = -1;
    private TestUtils() {}

    static byte[] toByteArray(final InputStream input) {
        try (final ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            copy(input, output);
            return output.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void copy(InputStream input, ByteArrayOutputStream output) {
        byte[] buffer = new byte[4096];
        int n;

        try {
            while (EOF != (n = input.read(buffer))) {
                output.write(buffer, 0, n);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
