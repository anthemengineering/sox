package com.anthemengineering.sox;

public final class ValidationUtil {
    private ValidationUtil() {}

    static <T> T nonNull(T obj, String msg) {
        if (obj == null) {
            throw new SoxException(msg);
        }

        return obj;
    }

    public static String notNullOrEmpty(String str, String msg) {
        if (str == null || str.trim().isEmpty()) {
            throw new SoxException(msg);
        }

        return str;
    }
}
