package com.gordon.rawe.utils;

/**
 * Created by gordon on 2017/1/2.
 */

public class Helper {
    public static boolean isInvalid(CharSequence object) {
        return object == null || object.length() == 0;
    }
}
