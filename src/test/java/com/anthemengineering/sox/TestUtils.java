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
