package com.gordon.rawe.sharedlibs.bundle.framework;

public class BundleException extends Exception {

    public BundleException(String str, Throwable th) {
        super(str);
    }

    public BundleException(String str) {
        super(str);
    }
}
