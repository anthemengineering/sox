package com.anthemengineering.sox.format;

import com.anthemengineering.sox.jna.sox_format_t;


public interface SoxSink {
    sox_format_t create(sox_format_t signal);
}
