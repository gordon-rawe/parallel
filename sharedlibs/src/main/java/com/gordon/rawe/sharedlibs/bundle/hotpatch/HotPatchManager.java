package com.gordon.rawe.sharedlibs.bundle.hotpatch;

import android.text.TextUtils;

import com.gordon.rawe.sharedlibs.bundle.framework.configs.Environments;
import com.gordon.rawe.sharedlibs.bundle.log.Logger;
import com.gordon.rawe.sharedlibs.bundle.log.LoggerFactory;
import com.gordon.rawe.sharedlibs.bundle.runtime.RuntimeArgs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by yb.wang on 15/7/30.
 */
public class HotPatchManager {

    private static volatile HotPatchManager instance;
    private static final Logger log;
    private static SortedMap<Integer, HotPatchItem> sortedMap;
    private File patchDir;

    static {
        log = LoggerFactory.getLogcatLogger("HotPatchItem");
    }

    private HotPatchManager() {
        File baseFile = RuntimeArgs.androidApplication.getFilesDir();
        patchDir = new File(baseFile, Environments.HOT_PATCH_DIR);
        sortedMap = new TreeMap<>(new Comparator<Integer>() {
            @Override
            public int compare(Integer lhs, Integer rhs) {
                return rhs.compareTo(lhs);
            }
        });
    }

    public static HotPatchManager getInstance() {
        if (instance == null) {
            synchronized (HotPatchManager.class) {
                if (instance == null) {
                    instance = new HotPatchManager();
                }
            }
        }
        return instance;
    }

    /**
     * 运行补丁
     */
    public void run() {
        try {
            initHotPatches();
            if (!sortedMap.isEmpty()) {
                for (Map.Entry<Integer, HotPatchItem> entry : sortedMap.entrySet()) {
                    HotPatchItem item = entry.getValue();
                    if (item != null && item.isPatchInstalled()) {
                        try {
                            item.optDexFile();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (Throwable e) {
            log.log("Failed to run pacth", Logger.LogLevel.ERROR, e);
        }
    }

    /**
     * 安装补丁
     */
    public boolean installHotPatch(String hotFixFileName, InputStream inputStream) {
        if (TextUtils.isEmpty(hotFixFileName) || inputStream == null) return false;
        boolean ret = true;
        if (!patchDir.exists()) patchDir.mkdirs();
        try {
            int rst_index = hotFixFileName.lastIndexOf("_rst");
            boolean isUninstalled = false;
            if (rst_index >= 0) {
                String fileName = hotFixFileName.substring(0, rst_index);
                isUninstalled = uninstallHotPatch(fileName);
            }
            if (!isUninstalled) {
                int version = 1;
                if (!sortedMap.isEmpty()) {
                    version = sortedMap.firstKey() + 1;
                }

                String storageFile = hotFixFileName + "_" + version;
                HotPatchItem hotPatchItem = new HotPatchItem(new File(patchDir, storageFile), inputStream);
                sortedMap.put(version, hotPatchItem);

                if (hotPatchItem.isPatchInstalled()) {
                    try {
                        hotPatchItem.optHotFixDexFile();
                    } catch (Exception e) {
                        e.printStackTrace();
                        ret = false;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.log("installHotPatch error", Logger.LogLevel.ERROR, e);
            ret = false;
        }
        return ret;
    }

    public void purge() {
        if (patchDir.exists())
            deleteDirectory(patchDir);
    }

    private void deleteDirectory(File file) {
        try {
            File[] listFiles = file.listFiles();
            for (File listFile : listFiles) {
                if (listFile.isDirectory()) {
                    deleteDirectory(listFile);
                } else {
                    listFile.delete();
                }
            }
            file.delete();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private boolean uninstallHotPatch(String hotFixFileName) {
        if (!sortedMap.isEmpty()) {
            int key = 0;
            for (Map.Entry<Integer, HotPatchItem> entry : sortedMap.entrySet()) {
                HotPatchItem item = entry.getValue();
                if (item != null && item.getHotPatchId().toLowerCase().contains(hotFixFileName.toLowerCase())) {
                    item.purge();
                    key = entry.getKey();
                    break;
                }
            }
            if (key > 0) {
                sortedMap.remove(key);
                return true;
            }
        }
        return false;

    }

    private void initHotPatches() {
        if (!patchDir.exists()) {
            patchDir.mkdirs();
        }
        File[] listFiles = patchDir.listFiles();
        if (listFiles != null) {
            for (File file : listFiles) {
                if (file.isDirectory()) {
                    HotPatchItem hotPatchItem = new HotPatchItem(file);
                    try {
                        int separatorIndex = file.getName().lastIndexOf("_");
                        String filter = file.getName().substring(separatorIndex + 1, file.getName().length());
                        int version = Integer.parseInt(filter);
                        sortedMap.put(version, hotPatchItem);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }
}
