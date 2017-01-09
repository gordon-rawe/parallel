package com.gordon.rawe.sharedlibs.bundle.runtime;

import android.app.Application;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.gordon.rawe.sharedlibs.bundle.framework.Bundle;
import com.gordon.rawe.sharedlibs.bundle.framework.BundleImpl;
import com.gordon.rawe.sharedlibs.bundle.framework.Framework;
import com.gordon.rawe.sharedlibs.bundle.hack.AndroidHack;
import com.gordon.rawe.sharedlibs.bundle.hack.SysHacks;
import com.gordon.rawe.sharedlibs.bundle.log.Logger;
import com.gordon.rawe.sharedlibs.bundle.log.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by yb.wang on 15/1/5.
 * 挂载载系统资源中，处理框架资源加载
 */
public class DelegateResources extends Resources {
    static final Logger log;
    public static final int RETRIAL_TIME = 3;

    static {
        log = LoggerFactory.getLogcatLogger("DelegateResources");
    }

    public DelegateResources(AssetManager assets, Resources resources) {
        super(assets, resources.getDisplayMetrics(), resources.getConfiguration());
    }

    public static void newDelegateResources(Application application, Resources resources) throws Exception {
        List<Bundle> bundles = Framework.getBundles();
        if (bundles != null && !bundles.isEmpty()) {
            Resources delegateResources;
            List<String> arrayList = new ArrayList<>();
            arrayList.addAll(getSystemAssetPaths(application));
            for (Bundle bundle : bundles) {
                arrayList.add(((BundleImpl) bundle).getArchive().getArchiveFile().getAbsolutePath());
            }
            AssetManager assetManager = AssetManager.class.newInstance();
            /*添加出错重试三次机制*/
            for (String str : arrayList) {
                int count = RETRIAL_TIME;
                while (count > 0) {
                    if ((int) SysHacks.AssetManager_addAssetPath.invoke(assetManager, str) == 0)
                        break;
                    count--;
                    if (count == 0) log.log("add assets path failed...", Logger.LogLevel.DBUG);
                }
            }
            //处理小米UI资源
            if (resources == null || !resources.getClass().getName().equals("android.content.res.MiuiResources")) {
                delegateResources = new DelegateResources(assetManager, resources);
            } else {
                Constructor declaredConstructor = Class.forName("android.content.res.MiuiResources").getDeclaredConstructor(AssetManager.class, DisplayMetrics.class, Configuration.class);
                declaredConstructor.setAccessible(true);
                delegateResources = (Resources) declaredConstructor.newInstance(assetManager, resources.getDisplayMetrics(), resources.getConfiguration());
            }
            RuntimeArgs.delegateResources = delegateResources;
            AndroidHack.injectResources(application, delegateResources);
            StringBuilder stringBuffer = new StringBuilder();
            stringBuffer.append("newDelegateResources [");
            for (int i = 0; i < arrayList.size(); i++) {
                if (i > 0) {
                    stringBuffer.append(",");
                }
                stringBuffer.append(arrayList.get(i));
            }
            stringBuffer.append("]");
            log.log(stringBuffer.toString(), Logger.LogLevel.DBUG);
        }
    }

    public static List<String> getSystemAssetPaths(Application application) {
        List<String> retValue = new ArrayList<>();
        retValue.add(application.getApplicationInfo().sourceDir);
        AssetManager assetManager = application.getResources().getAssets();
        try {
            Method declaredMethod = assetManager.getClass().getDeclaredMethod("getStringBlockCount");
            int count = (int) declaredMethod.invoke(assetManager);
            for (int i = 0; i < count; i++) {
                String cookieName = (String) assetManager.getClass().getMethod("getCookieName", new Class[]{Integer.TYPE}).invoke(assetManager, i + 1);
                if (!TextUtils.isEmpty(cookieName)) retValue.add(cookieName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retValue;
    }
}
