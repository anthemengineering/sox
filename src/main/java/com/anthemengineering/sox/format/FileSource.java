package com.anthemengineering.sox.format;

import com.anthemengineering.sox.Sox;
import com.anthemengineering.sox.jna.sox_format_t;

import java.nio.file.Path;
import java.nio.file.Paths;

import static com.anthemengineering.sox.ValidationUtil.nonNull;

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
}
