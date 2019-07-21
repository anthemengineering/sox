package com.anthemengineering.sox.format;

import com.anthemengineering.sox.jna.sox_format_t;
import com.anthemengineering.sox.jna.sox_signalinfo_t;

public interface SoxSink {
    sox_format_t create(sox_signalinfo_t signal);
}
