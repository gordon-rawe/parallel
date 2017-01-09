package com.gordon.rawe.sharedlibs.bundle.util;

/**
 * Created by yb.wang on 14/12/31.
 */
public class StringUtil {
    private static final String EMPTY = "";

    public static boolean isEmpty(String source) {
        return source == null || source.length() == 0;
    }


    public static boolean equals(String str, String str2) {
        return str != null && str.equals(str2);
    }

    public static String subStringAfter(String source, String prefix) {
        if (isEmpty(source)) return source;
        if (prefix == null) return EMPTY;
        int indexOf = source.indexOf(prefix);
        return indexOf != -1 ? source.substring(indexOf + prefix.length()) : EMPTY;

    }
}
