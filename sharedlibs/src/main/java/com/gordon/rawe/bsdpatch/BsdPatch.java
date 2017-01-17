package com.gordon.rawe.bsdpatch;

/**
 * Created by gordon on 17/01/2017.
 */

public class BsdPatch {
    static {
        System.loadLibrary("bsdpatch");
    }

    public static native int bsdpatch(String oldPath, String newPath, String patchPath);
}
