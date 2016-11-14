package com.lakeel.device.management.core;

public final class StringUtils {

    private StringUtils() {
    }

    public static boolean isEmpty(String str) {
        if (str == null || str.length() == 0) {
            return true;
        } else {
            return false;
        }
    }
}
