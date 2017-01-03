package com.gordon.rawe

import com.gordon.rawe.utils.Helper
import org.gradle.api.Project

public class ParallelLibraryOptions {
    public static final String optionsName = "libraryOptions"

    public String packageName;
    public String moduleProguardRulesFilePath;

    //nullAble
    public String libsDirPath;
    public String sourceDirPath;
    public String assetsDirPath;
    public String resourceDirPath;
    public String androidManifestFilePath;

    public String parentModuleName;
    public String soName;

    public
    static void initLibraryExtensionAfterEvaluate(ParallelLibraryOptions libraryExtension, Project libraryProject) {
        libraryExtension.libsDirPath = Helper.isInvalid(libraryExtension.libsDirPath) ? "$libraryProject.projectDir/libs" : libraryExtension.libsDirPath
        libraryExtension.sourceDirPath = Helper.isInvalid(libraryExtension.sourceDirPath) ? "$libraryProject.projectDir/src/main/java" : libraryExtension.sourceDirPath
        libraryExtension.resourceDirPath = Helper.isInvalid(libraryExtension.resourceDirPath) ? "$libraryProject.projectDir/src/main/res" : libraryExtension.resourceDirPath
        libraryExtension.assetsDirPath = Helper.isInvalid(libraryExtension.assetsDirPath) ? "$libraryProject.projectDir/src/main/assets" : libraryExtension.assetsDirPath
        libraryExtension.androidManifestFilePath = Helper.isInvalid(libraryExtension.androidManifestFilePath) ? "$libraryProject.projectDir/src/main/AndroidManifest.xml" : libraryExtension.androidManifestFilePath
        libraryExtension.soName = libraryExtension.packageName.replace('.', '_')

        println("$libraryProject.path apply dynamicLibrary initLibraryExtensionAfterEvaluate:\n")
    }
}