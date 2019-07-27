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

package com.anthemengineering.sox.format;

import com.anthemengineering.sox.inprocess.Sox;
import com.anthemengineering.sox.jna.sox_format_t;

import java.nio.file.Path;
import java.nio.file.Paths;

import static com.anthemengineering.sox.utils.ValidationUtil.nonNull;

public class FileSource implements SoxSource {
    private Path path;

    public FileSource path(Path path) {
        this.path = path;

        return this;
    }

    public FileSource path(String path) {
        return path(Paths.get(path));
    }

    @Override
    public sox_format_t create() {
        return Sox.openRead(nonNull(path.toAbsolutePath().toString(), "Source is required to be specified"));
    }

    @Override
    public Path getPath() {
        return path;
    }
}
