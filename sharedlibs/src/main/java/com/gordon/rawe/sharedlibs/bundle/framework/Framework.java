package com.gordon.rawe.sharedlibs.bundle.framework;

import android.os.Build;

import com.gordon.rawe.sharedlibs.bundle.framework.configs.Environments;
import com.gordon.rawe.sharedlibs.bundle.log.Logger;
import com.gordon.rawe.sharedlibs.bundle.log.LoggerFactory;
import com.gordon.rawe.sharedlibs.bundle.runtime.RuntimeArgs;
import com.gordon.rawe.sharedlibs.bundle.util.BundlePreference;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by yb.wang on 14/12/31.
 * 框架包含自身SystemBundle
 * 1.管理各个Bundle的启动，更新，卸载
 * 2.提供框架启动Runtime
 */
public final class Framework {
    static final Logger log;

    public static final String SYMBOL_SEMICOLON = ";";

    private static String STORAGE_LOCATION;
    static final Map<String, Bundle> bundles;

    private static long nextBundleID;
    private static Properties properties;

    static {
        log = LoggerFactory.getLogcatLogger("Framework");
        bundles = new ConcurrentHashMap<>();
        nextBundleID = 1;

    }

    private Framework() {
    }

    static void startup() throws BundleException {
        if (properties == null) properties = new Properties();

        log.log("*------------------------------------*", Logger.LogLevel.DBUG);
        log.log(" Bundle framework on " + Build.MODEL + "|" + Build.CPU_ABI + "starting...", Logger.LogLevel.DBUG);
        log.log("*------------------------------------*", Logger.LogLevel.DBUG);

        long currentTimeMillis = System.currentTimeMillis();
        launch();
        boolean bundleInitialized = BundlePreference.getInstance().getBoolean(Environments.BUNDLE_INSTALL_STATUS, false);
        if (bundleInitialized) {
            File file = new File(STORAGE_LOCATION);
            if (file.exists()) {
                log.log("Purging Storage ...", Logger.LogLevel.DBUG);
                deleteDirectory(file);
            }
            file.mkdirs();
            storeProfile();
        } else {
            restoreProfile();
        }
        long endTimeMillis = System.currentTimeMillis() - currentTimeMillis;
        log.log("*------------------------------------*", Logger.LogLevel.DBUG);
        log.log(" Framework " + (bundleInitialized ? "restarted" : "start") + " in " + endTimeMillis + " ms", Logger.LogLevel.DBUG);
        log.log("*------------------------------------*", Logger.LogLevel.DBUG);
    }

    public static List<Bundle> getBundles() {
        List<Bundle> arrayList = new ArrayList<>(bundles.size());
        synchronized (bundles) {
            arrayList.addAll(bundles.values());
        }
        return arrayList;
    }

    public static Bundle getBundle(String str) {
        return bundles.get(str);
    }

    public static Bundle getBundle(long id) {
        synchronized (bundles) {
            for (Bundle bundle : bundles.values()) {
                if (bundle.getBundleId() == id) {
                    return bundle;
                }
            }
            return null;
        }
    }

    private static void launch() {
        STORAGE_LOCATION = RuntimeArgs.androidApplication.getExternalCacheDir().getPath()+File.separator+"storage";
    }

    public static String getProperty(String str, String defaultValue) {
        return properties == null ? defaultValue : (String) properties.get(str);
    }

    private static void storeProfile() {
        BundleImpl[] bundleImplArr = getBundles().toArray(new BundleImpl[bundles.size()]);
        for (BundleImpl bundleImpl : bundleImplArr) {
            bundleImpl.updateMetadata();
        }
        storeMetadata();
    }

    private static void storeMetadata() {
        try {
            DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(new File(STORAGE_LOCATION, "meta")));
            dataOutputStream.writeLong(nextBundleID);
            dataOutputStream.flush();
            dataOutputStream.close();
        } catch (Throwable e) {
            log.log("Could not save meta data.", Logger.LogLevel.ERROR, e);
        }
    }

    private static int restoreProfile() {
        try {
            log.log("Restoring profile", Logger.LogLevel.DBUG);
            File file = new File(STORAGE_LOCATION, "meta");
            if (file.exists()) {
                DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));
                nextBundleID = dataInputStream.readLong();
                dataInputStream.close();
                File file2 = new File(STORAGE_LOCATION);
                File[] listFiles = file2.listFiles();
                int i = 0;
                while (i < listFiles.length) {
                    if (listFiles[i].isDirectory() && new File(listFiles[i], "meta").exists()) {
                        try {
                            String location = new BundleImpl(listFiles[i]).location;
                            log.log("RESTORED BUNDLE " + location, Logger.LogLevel.DBUG);
                        } catch (Exception e) {
                            log.log(e.getMessage(), Logger.LogLevel.ERROR, e.getCause());
                        }
                    }
                    i++;
                }
                return 1;
            }
            log.log("Profile not found, performing clean start ...", Logger.LogLevel.DBUG);
            return -1;
        } catch (Exception e2) {
            e2.printStackTrace();
            return 0;
        }
    }


    public static void deleteDirectory(File file) {
        if (file != null) {
            File[] listFiles = file.listFiles();
            for (File listFile : listFiles) {
                if (listFile.isDirectory()) {
                    deleteDirectory(listFile);
                } else {
                    listFile.delete();
                }
            }
            file.delete();
        }
    }


    static BundleImpl installNewBundle(String location, InputStream inputStream) throws BundleException {
        BundleImpl bundleImpl = (BundleImpl) getBundle(location);
        if (bundleImpl != null) {
            return bundleImpl;
        }
        long j = nextBundleID;
        nextBundleID++;
        bundleImpl = new BundleImpl(new File(STORAGE_LOCATION, String.valueOf(j)), location, j, inputStream);
        storeMetadata();
        return bundleImpl;
    }
}
