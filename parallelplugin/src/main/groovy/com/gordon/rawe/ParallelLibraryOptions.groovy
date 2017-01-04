package com.gordon.rawe

import com.gordon.rawe.utils.Helper
import org.gradle.api.Project

public class ParallelLibraryOptions {
    public static final String optionsName = "libraryOptions"

    /** mandatory */
    public String packageName;
    public String moduleProguardRulesFilePath;

    /** optional */
    public String libsDirPath;
    public String sourceDirPath;
    public String assetsDirPath;
    public String resourceDirPath;
    public String androidManifestFilePath;

    public String parentModuleName;
    public String soName;

    public void initOptionsAfterEvaluate(Project libraryProject) {
        libsDirPath = Helper.isInvalid(libsDirPath) ? "$libraryProject.projectDir/libs" : libsDirPath
        sourceDirPath = Helper.isInvalid(sourceDirPath) ? "$libraryProject.projectDir/src/main/java" : sourceDirPath
        resourceDirPath = Helper.isInvalid(resourceDirPath) ? "$libraryProject.projectDir/src/main/res" : resourceDirPath
        assetsDirPath = Helper.isInvalid(assetsDirPath) ? "$libraryProject.projectDir/src/main/assets" : assetsDirPath
        androidManifestFilePath = Helper.isInvalid(androidManifestFilePath) ? "$libraryProject.projectDir/src/main/AndroidManifest.xml" : androidManifestFilePath
        soName = packageName.replace('.', '_')
        println this
    }

    @Override
    String toString() {
        return "packageName\t$packageName\n" +
                "moduleProguardRulesFilePath\t$moduleProguardRulesFilePath\n" +
                "libsDirPath\t$libsDirPath\n" +
                "sourceDirPath\t$sourceDirPath\n" +
                "assetsDirPath\t$assetsDirPath\n" +
                "resourceDirPath\t$resourceDirPath\n" +
                "androidManifestFilePath\t$androidManifestFilePath\n" +
                "assetsDirPath\t$parentModuleName\n" +
                "resourceDirPath\t$soName\n"
    }
}