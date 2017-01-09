package com.gordon.rawe.sharedlibs;

import android.app.Application;
import android.content.Context;

import com.gordon.rawe.sharedlibs.bundle.framework.initialize.BundleInitializer;

/**
 * Created by gordon on 2016/12/30.
 */

public class BundleApplication extends Application {

    @Override
    public void onCreate() {
        BundleInitializer.initialize(this);
    }
}
