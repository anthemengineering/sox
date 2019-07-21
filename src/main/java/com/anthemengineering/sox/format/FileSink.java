package com.anthemengineering.sox.format;

import com.anthemengineering.sox.Sox;
import com.anthemengineering.sox.jna.sox_format_t;
import com.anthemengineering.sox.jna.sox_signalinfo_t;

import java.nio.file.Path;
import java.nio.file.Paths;

import static com.anthemengineering.sox.ValidationUtil.nonNull;

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
    public sox_format_t create(sox_signalinfo_t sourceSignal) {
        return Sox.openWrite(
                nonNull(path, "Sink path is required to be specified.").toAbsolutePath().toString(),
                sourceSignal,
                overwrite);
    }
}
