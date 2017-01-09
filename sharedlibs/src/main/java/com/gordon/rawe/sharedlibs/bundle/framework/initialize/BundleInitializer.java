package com.gordon.rawe.sharedlibs.bundle.framework.initialize;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import com.gordon.rawe.sharedlibs.bundle.framework.BundleCore;
import com.gordon.rawe.sharedlibs.bundle.framework.BundleException;
import com.gordon.rawe.sharedlibs.bundle.framework.configs.Environments;
import com.gordon.rawe.sharedlibs.bundle.hotpatch.HotPatchManager;
import com.gordon.rawe.sharedlibs.bundle.util.BundlePreference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by gordon on 2016/12/28.
 */

public class BundleInitializer {
    public static void initialize(final Application application) {
        BundlePreference.getInstance().init(application);
        boolean isDexInstalled = true;

        try {
            BundleCore.getInstance().init(application);
            BundleCore.getInstance().configLogger(true, 1);
            String lastBundleKey = BundlePreference.getInstance().getString(Environments.LAST_BUNDLE_KEY, "");
            final String bundleKey = Environments.buildBundleKey(application);
            if (!lastBundleKey.equals(bundleKey)) {
                BundlePreference.getInstance().putBoolean(Environments.BUNDLE_INSTALL_STATUS, true);
                isDexInstalled = false;
                HotPatchManager.getInstance().purge();
            }
            BundleCore.getInstance().startup();
            if (isDexInstalled) {
                HotPatchManager.getInstance().run();
                BundleCore.getInstance().run();
            } else {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ZipFile zipFile = new ZipFile(application.getApplicationInfo().sourceDir);
                            List<String> bundleFiles = getBundleEntryNames(zipFile, Environments.LIB_PATH, Environments.BUNDLE_SUFFIX);
                            if (bundleFiles != null && bundleFiles.size() > 0) {
                                processLibsBundles(zipFile, bundleFiles);
                                BundlePreference.getInstance().putString(Environments.LAST_BUNDLE_KEY, bundleKey);
                            } else {
                                Log.e("Error Bundle", "not found bundle in apk");
                            }
                            try {
                                zipFile.close();
                            } catch (IOException e2) {
                                e2.printStackTrace();
                            }
                            BundleCore.getInstance().run();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private static List<String> getBundleEntryNames(ZipFile zipFile, String str, String str2) {
        List<String> arrayList = new ArrayList<>();
        try {
            Enumeration entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                String name = ((ZipEntry) entries.nextElement()).getName();
                if (name.startsWith(str) && name.endsWith(str2)) {
                    arrayList.add(name);
                }
            }
        } catch (Throwable e) {
            Log.e("getBundleEntryNames", "Exception while get bundles in assets or lib", e);
        }
        return arrayList;
    }

    private static void processLibsBundles(ZipFile zipFile, List<String> list) {

        for (String str : list) {
            processLibsBundle(zipFile, str);
        }
    }

    private static boolean processLibsBundle(ZipFile zipFile, String str) {
        String packageNameFromEntryName = getPackageNameFromEntryName(str);
        Log.e("---x",packageNameFromEntryName);
        if (BundleCore.getInstance().getBundle(packageNameFromEntryName) == null) {
            try {
                BundleCore.getInstance().installBundle(packageNameFromEntryName, zipFile.getInputStream(zipFile.getEntry(str)));
                Log.e("Succeed install", "Succeed to install bundle " + packageNameFromEntryName);
                return true;
            } catch (BundleException | IOException ex) {
                Log.e("Fail install", "Could not install bundle.", ex);
            }
        }
        return false;
    }

    private static String getPackageNameFromEntryName(String entryName) {
        return entryName.substring(entryName.indexOf(Environments.LIB_PATH) + Environments.LIB_PATH.length(), entryName.indexOf(".so")).replace("_", ".");
    }
}
