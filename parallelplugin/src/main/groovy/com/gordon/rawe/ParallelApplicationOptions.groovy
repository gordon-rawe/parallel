package com.gordon.rawe

import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection

public class ParallelApplicationOptions {

    public static final String optionsName = "applicationOptions"

    /** mandatory */
    public String packageName

    /** optional */
    public String keystore
    public String keyAlias
    public String keyPassword
    public String storePassword

    //inner
    public ConfigurableFileCollection classpath

    public void initOptions(Project project) {
        ParallelSharedOptions.reference.applicationPackageName = packageName
        ParallelSharedOptions.reference.applicationBuildDir = "$project.buildDir/generated/source/r/release/" + packageName.replace('.', '/') + "/R.java"
//        applicationExtension.releaseBaseApkName = MTextUtil.isEmpty(applicationExtension.releaseBaseApkName) ? "$applicationProject.name-release.apk" : applicationExtension.releaseBaseApkName
//        applicationExtension.releaseBaseApkPath = MTextUtil.isEmpty(applicationExtension.releaseBaseApkPath) ? "$applicationProject.buildDir/outputs/apk/$applicationExtension.releaseBaseApkName" : applicationExtension.releaseBaseApkPath
//        applicationExtension.buildOutputName = MTextUtil.isEmpty(applicationExtension.buildOutputName) ? "build-output" : applicationExtension.buildOutputName
//        applicationExtension.buildOutputPrefix = MTextUtil.isEmpty(applicationExtension.buildOutputPrefix) ? "MDynamic" : applicationExtension.buildOutputPrefix
//        applicationExtension.buildOutputPath = MTextUtil.isEmpty(applicationExtension.buildOutputPath) ? "$applicationProject.rootDir/$applicationExtension.buildOutputName" : applicationExtension.buildOutputPath
//        applicationExtension.buildOutputBaseApkFilePath = MTextUtil.isEmpty(applicationExtension.buildOutputBaseApkFilePath) ? "$applicationExtension.buildOutputPath/$applicationExtension.buildOutputPrefix-base-release.apk" : applicationExtension.buildOutputBaseApkFilePath
//        applicationExtension.sdkDir = MTextUtil.isEmpty(applicationExtension.sdkDir) ? getSDKDirFromProject(applicationProject) : applicationExtension.sdkDir
//
//        //config sdk start
//        //String aaptDir = MApplicationExtension.class.getResource("/aapt").path
//        String outJarFolder = getJarFolder()
//        File aaptOutDir = new File("$outJarFolder/aapt");
//        if (!aaptOutDir.exists())
//            aaptOutDir.mkdirs()
//        applicationProject.logger.info("$applicationProject.path:apply dynamicApplication:aaptDir:$aaptOutDir")
//        if (Os.isFamily(Os.FAMILY_WINDOWS)) {
//            applicationExtension.aapt = "$aaptOutDir.path/aapt_win.exe"
//            applicationExtension.dex = "$applicationExtension.sdkDir/build-tools/$applicationExtension.buildToolsVersion/dx.bat"
//        } else if (Os.isFamily(Os.FAMILY_MAC)) {
//            applicationExtension.aapt = "$aaptOutDir.path/aapt_mac"
//            applicationExtension.dex = "$applicationExtension.sdkDir/build-tools/$applicationExtension.buildToolsVersion/dx"
//        } else if (Os.isFamily(Os.FAMILY_UNIX)) {
//            applicationExtension.aapt = "$aaptOutDir.path/aapt_linux"
//            applicationExtension.dex = "$applicationExtension.sdkDir/build-tools/$applicationExtension.buildToolsVersion/dx"
//        }
//        applicationExtension.androidJar = "$applicationExtension.sdkDir/platforms/android-$applicationExtension.targetSdkVersion/android.jar"
//        if (applicationExtension.targetSdkVersion >= 23)
//            applicationExtension.apacheJar = "$applicationExtension.sdkDir/platforms/android-$applicationExtension.targetSdkVersion/optional/org.apache.http.legacy.jar";
//
//        File aaptFile = applicationProject.file(applicationExtension.aapt)
//        if (!aaptFile.exists()) {
//            File aaptOutZip = new File("$outJarFolder/aapt.zip");
//            if (!aaptOutZip.exists())
//                exportResource("/aapt.zip")
//            applicationProject.logger.info("$applicationProject.path:apply dynamicApplication:aaptOutZip.exists=" + aaptOutZip.exists() + " ,aaptOutZip.path=$aaptOutZip.path")
//            FileTree aaptZipFileTree = applicationProject.zipTree(aaptOutZip.path)
//            applicationProject.logger.info("$applicationProject.path:apply dynamicApplication:aaptZipFileTree=" + aaptZipFileTree.toList().toListString())
//            if (aaptZipFileTree != null) {
//                Copy copy = applicationProject.tasks.create("copyAapt", Copy.class);
//                copy.setFileMode(0755)
//                copy.from(aaptZipFileTree.files)
//                copy.into(aaptOutDir)
//                copy.execute()
//            }
//        }
//        File dexFile = applicationProject.file(applicationExtension.dex)
//        dexFile.setExecutable(true)
//        aaptFile.setExecutable(true)
//        applicationProject.logger.info("$applicationProject.path:apply dynamicApplication:dexFile.exists=" + dexFile.exists() + " ,dexFile.canExecute=" + dexFile.canExecute() + " ,dexFile.path=$dexFile.path")
//        applicationProject.logger.info("$applicationProject.path:apply dynamicApplication:aaptFile.exists=" + aaptFile.exists() + " ,aaptFile.canExecute=" + aaptFile.canExecute() + " ,aaptFile.path=$aaptFile.path")
//        //config sdk end
//        applicationExtension.applicationProject = applicationProject
//        instance = applicationExtension
//        instance.applicationProject = applicationProject
//        println("$applicationProject.path:apply dynamicApplication:initApplicationExtensionAfterEvaluate:\n" + instance.toString())
    }
}