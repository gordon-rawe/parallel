package com.gordon.rawe

import com.gordon.rawe.TaskNames.TaskNames
import com.gordon.rawe.utils.Helper
import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.api.Project
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.Copy

public class ParallelSharedOptions {

    public static ParallelSharedOptions reference;

    public ParallelSharedOptions() {
        reference = this;
    }

    public static final String optionsName = "parallelOptions"

    public static class Default {
        public static final int DEFAULT_TARGET_SDK_VERSION = 22;
        public static final String DEFAULT_BUILD_TOOLS_VERSION = "25.0.2";
        public static final String DEFAULT_SUPPORT_LIB_VERSION = "25.1.0";
        public static final String DEFAULT_JAVA_COMPILE_VERSION = "1.7";
        public static final String DEFAULT_BUILD_OUTPUT_NAME = "build-output";
        public static final String DEFAULT_BUILD_OUTPUT_PREFIX = "parallel";
        public static final String DEFAULT_BASE_APK_SUFFIX = "-base-release.apk";
    }

    /** mandatory */
    public String moduleConfigFilePath;
    public String applicationPackageName;
    public String applicationBuildDir;

    /** optional */
    public String enabled = true;
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

    public void initBasicOptions(Project project) {

        /**assign values*/
        buildOutputName = Helper.isInvalid(buildOutputName) ? Default.DEFAULT_BUILD_OUTPUT_NAME : buildOutputName
        buildOutputPrefix = Helper.isInvalid(buildOutputPrefix) ? Default.DEFAULT_BUILD_OUTPUT_PREFIX : buildOutputPrefix
        buildOutputPath = Helper.isInvalid(buildOutputPath) ? "$project.rootDir/$buildOutputName" : buildOutputPath
        buildOutputBaseApkFilePath = Helper.isInvalid(buildOutputBaseApkFilePath) ? "$buildOutputPath/" + Default.DEFAULT_BASE_APK_SUFFIX : buildOutputBaseApkFilePath
        sdkDir = Helper.isInvalid(sdkDir) ? getSDKDirFromProject(project) : sdkDir

        //config sdk start
        //String aaptDir = ParallelSharedOptions.class.getResource("/aapt").path
        String outJarFolder = getJarFolder()
        File aaptOutDir = new File("$outJarFolder/aapt");
        if (!aaptOutDir.exists())
            aaptOutDir.mkdirs()
        project.logger.info("$project.path apply aaptDir:$aaptOutDir")
        if (Os.isFamily(Os.FAMILY_WINDOWS)) {
            aapt = "$aaptOutDir.path/aapt_win.exe"
            dex = "$sdkDir/build-tools/$buildToolsVersion/dx.bat"
        } else if (Os.isFamily(Os.FAMILY_MAC)) {
            aapt = "$aaptOutDir.path/aapt_mac"
            dex = "$sdkDir/build-tools/$buildToolsVersion/dx"
        } else if (Os.isFamily(Os.FAMILY_UNIX)) {
            aapt = "$aaptOutDir.path/aapt_linux"
            dex = "$sdkDir/build-tools/$buildToolsVersion/dx"
        }
        androidJar = "$sdkDir/platforms/android-$targetSdkVersion/android.jar"
        if (targetSdkVersion >= 23)
            apacheJar = "$sdkDir/platforms/android-$targetSdkVersion/optional/org.apache.http.legacy.jar";

        File aaptFile = project.file(aapt)
        if (!aaptFile.exists()) {
            File aaptOutZip = new File("$outJarFolder/aapt.zip");
            if (!aaptOutZip.exists())
                exportResource("/aapt.zip")
            FileTree aaptZipFileTree = project.zipTree(aaptOutZip.path)
            if (aaptZipFileTree != null) {
                Copy copy = project.tasks.create(TaskNames.COPY_AAPT, Copy.class);
                copy.setFileMode(0755)
                copy.from(aaptZipFileTree.files)
                copy.into(aaptOutDir)
                copy.execute()
            }
        }
        File dexFile = project.file(dex)
        dexFile.setExecutable(true)
        aaptFile.setExecutable(true)
        println reference
        //config sdk end
    }

    /**
     * Export a resource embedded into a Jar file to the local file path.
     *
     * @param resourceName ie.: "/SmartLibrary.dll"
     * @return The path to the exported resource
     * @throws Exception
     */
    public static void exportResource(String resourceName) throws Exception {
        InputStream stream = null;
        OutputStream resStreamOut = null;
        String jarFolder = getJarFolder();
        try {
            stream = ParallelSharedOptions.class.getResourceAsStream(resourceName);
            //note that each / is a directory down in the "jar tree" been the jar the root of the tree
            if (stream == null)
                throw new Exception("Cannot get resource \"" + resourceName + "\" from Jar file.");
            int readBytes;
            byte[] buffer = new byte[4096];
            resStreamOut = new FileOutputStream(jarFolder + resourceName);
            while ((readBytes = stream.read(buffer)) > 0)
                resStreamOut.write(buffer, 0, readBytes);
        } catch (Exception ex) {
            throw ex;
        } finally {
            stream.close();
            resStreamOut.close();
        }
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

    @Override
    public String toString() {
        return "    ParallelSharedOptions {" +
                "\n         ****[must not be null]****" +
                "\n         enabled='" + enabled + '\'' +
                "\n         moduleConfigFilePath='" + moduleConfigFilePath + '\'' +
                "\n         targetSdkVersion='" + targetSdkVersion + '\'' +
                "\n         buildToolsVersion='" + buildToolsVersion + '\'' +
                "\n         supportLibraryVersion='" + supportLibraryVersion + '\'' +
                "\n         javaCompileVersion='" + javaCompileVersion + '\'' +
                "\n         sdkDir='" + sdkDir + '\'' +
                "\n         buildOutputName='" + buildOutputName + '\'' +
                "\n         buildOutputPath='" + buildOutputPath + '\'' +
                "\n         buildOutputBaseApkFilePath='" + buildOutputBaseApkFilePath + '\'' +

                "\n         ****[       sdk      ]****" +
                "\n         aapt='" + aapt + '\'' +
                "\n         dex='" + dex + '\'' +
                "\n         androidJar='" + androidJar + '\'' +
                "\n         apacheJar='" + apacheJar + '\'' +
                "\n         applicationPackageName='" + applicationPackageName + '\'' +
                "\n         applicationBuildDir='" + applicationBuildDir + '\'' +
                "\n         ****[      extra     ]****" +
                '\n    }';
    }
}