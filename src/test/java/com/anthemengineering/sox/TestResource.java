package com.anthemengineering.sox;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

import static com.anthemengineering.sox.TestUtils.toByteArray;

public class TestResource {
    private final byte[] resource;

    public TestResource(String resourceName) {
        this.resource = toByteArray(getResourceStream(resourceName));
    }

    public ByteBuffer asByteBuffer() {
        return ByteBuffer.allocateDirect(resource.length).put(resource);
    }

    public byte[] asByteArray() {
        return Arrays.copyOf(resource, resource.length);
    }

    public int size() {
        return resource.length;
    }

    private static InputStream getResourceStream(String resourceName) {
        InputStream result =  TestUtils.class.getResourceAsStream(resourceName);
        if (result == null) {
            throw new IllegalStateException("Resource '" + resourceName + "' does not exist.");
        }

        return result;
    }

}
