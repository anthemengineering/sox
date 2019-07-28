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

public class FileSink implements SoxSink {
    private Path path;
    private boolean overwrite;

    public FileSink path(Path path) {
        this.path = path;

        return this;
    }

    public FileSink path(String path) {
        return path(Paths.get(path));
    }

    public FileSink allowOverwrite() {
        this.overwrite = true;

        return this;
    }

    @Override
    public sox_format_t create(sox_format_t sourceFormat) {
        return Sox.openWrite(
                nonNull(path, "Sink path is required to be specified.").toAbsolutePath().toString(),
                sourceFormat.signal,
                overwrite);
    }

    @Override
    public Path getPath() {
        return path.toAbsolutePath();
    }
}
