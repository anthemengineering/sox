package com.anthemengineering.sox;

import java.util.Collection;

public final class ValidationUtil {
    private ValidationUtil() {}

    public static <T> T nonNull(T obj, String msg) {
        if (obj == null) {
            throw new SoxException(msg);
        }

        return obj;
    }

    public static long positiveNumber(long num, String msg) {
        if (num <= 0) {
            throw new SoxException(msg);
        }

        return num;
    }

    public static String notNullOrEmpty(String str, String msg) {
        if (isNullOrEmpty(str)) {
            throw new SoxException(msg);
        }

        return str;
    }

    public static <T extends Collection<?>> T notNullOrEmpty(T col, String msg) {
        if (col == null || col.isEmpty()) {
            throw new SoxException(msg);
        }

        return col;
    }

    public static String nullOrEmpty(String str, String msg) {
        if (!isNullOrEmpty(str)) {
            throw new SoxException(msg);
        }

        return str;
    }

    private static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
