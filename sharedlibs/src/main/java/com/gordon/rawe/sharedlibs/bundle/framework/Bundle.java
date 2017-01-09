package com.gordon.rawe.sharedlibs.bundle.framework;

import java.io.InputStream;

public interface Bundle {


    long getBundleId();


    String getLocation();


    int getState();


    void update(InputStream inputStream) throws BundleException;
}
