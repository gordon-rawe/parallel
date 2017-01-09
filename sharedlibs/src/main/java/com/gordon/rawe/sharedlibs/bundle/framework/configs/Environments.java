package com.gordon.rawe.sharedlibs.bundle.framework.configs;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by gordon on 2016/12/28.
 */


public class Environments {
    private Environments() {
    }

    public static final String LAST_BUNDLE_KEY = "LAST_BUNDLE_KEY";
    public static final String LIB_PATH = "assets/baseres/";
    public static final String BUNDLE_SUFFIX = ".so";
    public static final String BUNDLE_INSTALL_STATUS = "BUNDLE_INSTALL_STATUS";
    public static final String HOT_PATCH_DIR = "hot_patch";
    public static final String KEY_WELCOME_PAGE = "ctrip.android.bundle.welcome";
    public static final String WELCOME_PAGE = "ctrip.android.view.home.CtripSplashActivity";
    public static final String LOCATION_DIR = "storage";

    public static String buildBundleKey(Context context) throws PackageManager.NameNotFoundException {
        PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
        return String.valueOf(packageInfo.versionCode) + "_" + packageInfo.versionName;
    }
}
