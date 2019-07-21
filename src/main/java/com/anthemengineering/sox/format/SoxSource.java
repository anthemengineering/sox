package com.anthemengineering.sox.format;

import com.anthemengineering.sox.jna.sox_format_t;

public interface SoxSource {
    sox_format_t create();
}
