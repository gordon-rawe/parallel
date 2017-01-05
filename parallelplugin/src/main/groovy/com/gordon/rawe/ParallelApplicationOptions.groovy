package com.gordon.rawe

import com.gordon.rawe.utils.Helper
import org.gradle.api.Project

public class ParallelApplicationOptions {

    public static final String optionsName = "applicationOptions"

    /** mandatory */
    public String packageName

    /** optional */
    public String keystore
    public String keyAlias
    public String keyPassword
    public String storePassword

    public void initOptions(Project project) {
        ParallelSharedOptions.reference.applicationPackageName = packageName
        ParallelSharedOptions.reference.applicationBuildDir = "$project.buildDir/generated/source/r/release/" + packageName.replace('.', '/') + "/R.java"
        ParallelSharedOptions.reference.releaseApkFileName = Helper.isInvalid(ParallelSharedOptions.reference.releaseApkFileName) ? "$project.name-release.apk" : ParallelSharedOptions.reference.releaseApkFileName
        ParallelSharedOptions.reference.releaseApkFilePath = Helper.isInvalid(ParallelSharedOptions.reference.releaseApkFilePath) ? "$project.buildDir/outputs/apk/$ParallelSharedOptions.reference.releaseApkFileName" : ParallelSharedOptions.reference.releaseApkFilePath
    }
}