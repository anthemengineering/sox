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
