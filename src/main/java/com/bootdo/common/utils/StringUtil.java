package com.bootdo.common.utils;

public class StringUtil {

    public static boolean isEmpty(String str) {
        if (str == null) {
            return true;
        } else {
            return str.trim().equals("");
        }
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static String trimSufffix(String toTrim, String trimStr) {
        while(toTrim.endsWith(trimStr)) {
            toTrim = toTrim.substring(0, toTrim.length() - trimStr.length());
        }

        return toTrim;
    }
}
