package com.gordon.rawe.sharedlibs.bundle.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by gordon on 2016/12/28.
 */

public class BundlePreference {
    public static final String KEY_PREFERENCE_BUNDLE_CONFIGS = "KEY_PREFERENCE_BUNDLE_CONFIGS";

    private BundlePreference() {
    }

    private SharedPreferences sharedPreferences;

    public void init(Context context) {
        sharedPreferences = context.getSharedPreferences(KEY_PREFERENCE_BUNDLE_CONFIGS, 0);
    }

    private static class Holder {
        static BundlePreference instance = new BundlePreference();
    }

    public static BundlePreference getInstance() {
        return Holder.instance;
    }

    private void checkBeforeInit() {
        if (sharedPreferences == null)
            throw new RuntimeException("you should init this SharedPreferences before using it!");
    }

    public void putString(String key, String value) {
        checkBeforeInit();
        sharedPreferences.edit().putString(key, value).apply();
    }

    public void putInt(String key, int value) {
        checkBeforeInit();
        sharedPreferences.edit().putInt(key, value).apply();
    }

    public void putBoolean(String key, boolean value) {
        checkBeforeInit();
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    public String getString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    public int getInt(String key, int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }
}
