package com.gordon.rawe

import com.gordon.rawe.utils.Helper
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.Copy

public class ParallelSharedOptions {

    public static final String pluginName = "parallelOptions"

    public static class Default {
        public static final int DEFAULT_TARGET_SDK_VERSION = 22;
        public static final String DEFAULT_BUILD_TOOLS_VERSION = "25.0.2";
        public static final String DEFAULT_SUPPORT_LIB_VERSION = "25.1.0";
        public static final String DEFAULT_JAVA_COMPILE_VERSION = "1.7";
        public static final String DEFAULT_BUILD_OUTPUT_NAME = "build-output";
        public static final String DEFAULT_BUILD_OUTPUT_PREFIX = "parallel";
        public static final String DEFAULT_BASE_APK_SUFFIX = "-base-release.apk";
    }

    public String moduleConfigFilePath;

    public int targetSdkVersion = Default.DEFAULT_TARGET_SDK_VERSION;//23(Android 6.0) 以下 不需要权限申请
    public String buildToolsVersion = Default.DEFAULT_BUILD_TOOLS_VERSION;
    public String supportLibraryVersion = Default.DEFAULT_SUPPORT_LIB_VERSION;
    public String javaCompileVersion = Default.DEFAULT_JAVA_COMPILE_VERSION;
    public String sdkDir;

    public String buildOutputName = Default.DEFAULT_BUILD_OUTPUT_NAME;
    public String buildOutputPrefix = Default.DEFAULT_BUILD_OUTPUT_PREFIX;
    public String buildOutputPath;
    public String buildOutputBaseApkFilePath;

    public String aapt;
    public String dex;
    public String androidJar;
    public String apacheJar;

    //inner
    public ConfigurableFileCollection classpath

    public void initBasicOptions(Project project) {
        ParallelSharedOptions options = project.extensions.create(pluginName, ParallelSharedOptions.class)
        /**assign values*/
        buildOutputName = Helper.isInvalid(options.buildOutputName) ? Default.DEFAULT_BUILD_OUTPUT_NAME : options.buildOutputName
        buildOutputPrefix = Helper.isInvalid(options.buildOutputPrefix) ? Default.DEFAULT_BUILD_OUTPUT_PREFIX : options.buildOutputPrefix
        buildOutputPath = Helper.isInvalid(options.buildOutputPath) ? "$project.rootDir/$options.buildOutputName" : options.buildOutputPath
        buildOutputBaseApkFilePath = Helper.isInvalid(options.buildOutputBaseApkFilePath) ? "$buildOutputPath/" + Default.DEFAULT_BASE_APK_SUFFIX : options.buildOutputBaseApkFilePath
        sdkDir = Helper.isInvalid(options.sdkDir) ? getSDKDirFromProject(project) : options.sdkDir

        //config sdk start
        //String aaptDir = MApplicationExtension.class.getResource("/aapt").path
        String outJarFolder = getJarFolder()
        File aaptOutDir = new File("$outJarFolder/aapt");
        if (!aaptOutDir.exists())
            aaptOutDir.mkdirs()
        applicationProject.logger.info("$applicationProject.path:apply dynamicApplication:aaptDir:$aaptOutDir")
        if (Os.isFamily(Os.FAMILY_WINDOWS)) {
            applicationExtension.aapt = "$aaptOutDir.path/aapt_win.exe"
            applicationExtension.dex = "$applicationExtension.sdkDir/build-tools/$applicationExtension.buildToolsVersion/dx.bat"
        } else if (Os.isFamily(Os.FAMILY_MAC)) {
            applicationExtension.aapt = "$aaptOutDir.path/aapt_mac"
            applicationExtension.dex = "$applicationExtension.sdkDir/build-tools/$applicationExtension.buildToolsVersion/dx"
        } else if (Os.isFamily(Os.FAMILY_UNIX)) {
            applicationExtension.aapt = "$aaptOutDir.path/aapt_linux"
            applicationExtension.dex = "$applicationExtension.sdkDir/build-tools/$applicationExtension.buildToolsVersion/dx"
        }
        applicationExtension.androidJar = "$applicationExtension.sdkDir/platforms/android-$applicationExtension.targetSdkVersion/android.jar"
        if (applicationExtension.targetSdkVersion >= 23)
            applicationExtension.apacheJar = "$applicationExtension.sdkDir/platforms/android-$applicationExtension.targetSdkVersion/optional/org.apache.http.legacy.jar";

        File aaptFile = applicationProject.file(applicationExtension.aapt)
        if (!aaptFile.exists()) {
            File aaptOutZip = new File("$outJarFolder/aapt.zip");
            if (!aaptOutZip.exists())
                exportResource("/aapt.zip")
            applicationProject.logger.info("$applicationProject.path:apply dynamicApplication:aaptOutZip.exists=" + aaptOutZip.exists() + " ,aaptOutZip.path=$aaptOutZip.path")
            FileTree aaptZipFileTree = applicationProject.zipTree(aaptOutZip.path)
            applicationProject.logger.info("$applicationProject.path:apply dynamicApplication:aaptZipFileTree=" + aaptZipFileTree.toList().toListString())
            if (aaptZipFileTree != null) {
                Copy copy = applicationProject.tasks.create("copyAapt", Copy.class);
                copy.setFileMode(0755)
                copy.from(aaptZipFileTree.files)
                copy.into(aaptOutDir)
                copy.execute()
            }
        }
        File dexFile = applicationProject.file(applicationExtension.dex)
        dexFile.setExecutable(true)
        aaptFile.setExecutable(true)
        applicationProject.logger.info("$applicationProject.path:apply dynamicApplication:dexFile.exists=" + dexFile.exists() + " ,dexFile.canExecute=" + dexFile.canExecute() + " ,dexFile.path=$dexFile.path")
        applicationProject.logger.info("$applicationProject.path:apply dynamicApplication:aaptFile.exists=" + aaptFile.exists() + " ,aaptFile.canExecute=" + aaptFile.canExecute() + " ,aaptFile.path=$aaptFile.path")
        //config sdk end
        applicationExtension.applicationProject = applicationProject
        instance = applicationExtension
        instance.applicationProject = applicationProject
        println("$applicationProject.path:apply dynamicApplication:initApplicationExtensionAfterEvaluate:\n" + instance.toString())
    }

    public static String getJarFolder() {
        return new File(ParallelApplicationOptions.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile().getPath().replace('\\', '/');
    }

    public static Properties getLocalProperties(Project project) {
        Properties properties = new Properties()
        properties.load(project.rootProject.file('local.properties').newDataInputStream())
        return properties
    }


    public static String getSDKDirFromProject(Project project) {
        String sdkDir = System.getenv("ANDROID_HOME")
        if (sdkDir == null)
            sdkDir = getLocalProperties(project).getProperty('sdk.dir')
        return sdkDir
    }
}