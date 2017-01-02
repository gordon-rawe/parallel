package com.gordon.rawe

import org.gradle.api.Plugin
import org.gradle.api.Project

public class ParallelLibraryPlugin implements Plugin<Project> {
//    public static String optionsName = "parallelOptions"
//    private Project applicationProject = null;
//
    private ParallelLibraryOptions libraryExtension;
//    private ConfigurableFileTree libJars = null;
//
//    private Project parentProject = null
//    private String parentPackageName = null
//    private String parentModuleJar = null
//    private String parentRFile = null
//    private ParallelLibraryPlugin parentLibraryExtension = null

    @Override
    public void apply(Project project) {

        project.afterEvaluate {
            project.task("show") << {
                println project.buildDir
                println project.project.buildDir
                println project.path
            }
        }

//
//        libraryExtension = project.extensions.create(optionsName, ParallelLibraryOptions.class)
//
//        project.afterEvaluate {
//            try {
//                println("$project.path:apply dynamicLibrary:config start")
//
//                boolean isConditionOK = true
//                if (MApplicationExtension.instance == null) {
//                    println("$project.path:apply dynamicLibrary:MApplicationExtension.instance==null return")
//                    isConditionOK = false
//                } else {
//                    if (!MApplicationExtension.instance.solidMode) {
//                        println("$project.path:apply dynamicLibrary:solidMode==false return")
//                        isConditionOK = false
//                    }/*else {
//                        project.evaluationDependsOn(MApplicationExtension.instance.applicationProject.path)
//                    }*/
//                }
//
//                if (libraryExtension == null) {
//                    println("$project.path:apply dynamicLibrary:libraryExtension==null return")
//                    isConditionOK = false
//                }
//                /*findApplicationProject()
//                if (applicationProject == null) {
//                    println("$project.path:apply dynamicLibrary:applicationProject==null return")
//                    return
//                } else {
//                    applicationProject.getBuildscript().configurations.each {
//                        println("$project.path:apply dynamicLibrary:applicationBuildscript:$it.name")
//                    }
//                    project.evaluationDependsOn(applicationProject.path)
//                    println("$project.path:apply dynamicLibrary:evaluationDependsOn($applicationProject.path)")
//                }*/
//
//                if (MTextUtil.isEmpty(libraryExtension.packageName)) {
//                    println("$project.path:apply dynamicLibrary:packageName==null return")
//                    isConditionOK = false
//                }
//                if (isConditionOK) {
//                    MLibraryExtension.initLibraryExtensionAfterEvaluate(libraryExtension, project)
//                    configureParentModule(project);
//                    configureLibs(project);
//                    configureInitTask(project);
//                    configureAaptReleaseTask(project);
//                    configureCompileReleaseTask(project);
//                    configureCopySOOutputsTask(project);
//                    configureObfuscateReleaseTask(project);
//                    configureDexReleaseTask(project);
//                    configureBundleReleaseTask();
//                }
//                println("$project.path:apply dynamicLibrary:config end")
//            } catch (Exception e) {
//                e.printStackTrace()
//            }
//        }
    }
//
//    private void findApplicationProject() {
//        if (applicationProject != null) {
//            println("$project.path:apply dynamicLibrary:findApplicationProject:" + (applicationProject == null ? "null" : applicationProject.path))
//            return
//        }
//        for (Project subProject : project.rootProject.getSubprojects()) {
//            println("$project.path:apply dynamicLibrary:subProject:$subProject.name")
//            if (subProject.getConfigurations().findByName("apk") != null) {
//                applicationProject = subProject;
//                println("$project.path:apply dynamicLibrary:findApplicationProject:$subProject.name")
//                break
//            }
//        }
//        println("$project.path:apply dynamicLibrary:findApplicationProject:" + (applicationProject == null ? "null" : applicationProject.path))
//    }
//
//    private void configureParentModule(Project project) {
//        println "$project.path:configureParentModule:parentModuleName==$libraryExtension.parentModuleName"
//        if (!MTextUtil.isEmpty(libraryExtension.parentModuleName)) {
//            parentProject = project.project(libraryExtension.parentModuleName)
//            project.evaluationDependsOn(parentProject.path)
//
//            if (parentProject != null) {
//                parentLibraryExtension = parentProject.extensions.findByName(optionsName)
//                if (parentLibraryExtension != null) {
//                    parentPackageName = parentLibraryExtension.packageName
//                    if (!MTextUtil.isEmpty(parentPackageName))
//                        parentRFile = "$parentProject.buildDir/gen/r/" + parentPackageName.replace('.', '/') + "/R.java"
//                }
//                parentModuleJar = "$parentProject.buildDir/intermediates/classes-obfuscated/classes-obfuscated.jar"
//            }
//        }
//        println "$project.path:configureParentModule:parentModuleExtension:" + (parentLibraryExtension == null ? "null" : "\n" + parentLibraryExtension.toString())
//        println "$project.path:configureParentModule:parentModuleProjectName==" + (parentProject == null ? "null" : parentProject.name)
//        println "$project.path:configureParentModule:parentModuleProjectPath==" + (parentProject == null ? "null" : parentProject.path)
//        println "$project.path:configureParentModule:parentPackageName==$parentPackageName"
//        println "$project.path:configureParentModule:parentModuleJar==$parentModuleJar"
//        println "$project.path:configureParentModule:parentRFile==$parentRFile"
//    }
//
//    private void configureLibs(Project project) {
//        libJars = project.fileTree(libraryExtension.libsDirPath).include('*.jar')
//        if (libJars != null && !libJars.isEmpty()) {
//            project.logger.info("$project.path:------------------------------------------------------------------------------------")
//            libJars.each {
//                project.logger.info("$project.path:configureLibs:libJars:" + it.path)
//            }
//            project.logger.info("$project.path:------------------------------------------------------------------------------------")
//        }
//    }
//
//    private void configureInitTask(Project project) {
//        project.logger.info("$project.path:configureInitTask:start")
//        Task dynamicInitDirsTask = project.tasks.create("dynamicInitDirs", Task.class);
//
//        dynamicInitDirsTask.outputs.dir "$project.buildDir/gen/r"
//        dynamicInitDirsTask.outputs.dir "$project.buildDir/intermediates"
//        dynamicInitDirsTask.outputs.dir "$project.buildDir/intermediates/classes"
//        dynamicInitDirsTask.outputs.dir "$project.buildDir/intermediates/classes-obfuscated"
//        dynamicInitDirsTask.outputs.dir "$project.buildDir/intermediates/res"
//        dynamicInitDirsTask.outputs.dir "$project.buildDir/intermediates/dex"
//
//        dynamicInitDirsTask.outputs.dir MApplicationExtension.instance.buildOutputPath
//        dynamicInitDirsTask.outputs.dir "$MApplicationExtension.instance.buildOutputPath/remoteApk"
//        dynamicInitDirsTask.outputs.dir "$MApplicationExtension.instance.buildOutputPath/jni/armeabi/"
//        dynamicInitDirsTask.outputs.dir "$MApplicationExtension.instance.buildOutputPath/jni/x86/"
//        project.logger.info("$project.path:dynamicInitDirsTask:buildOutputPath:$MApplicationExtension.instance.buildOutputPath")
//        dynamicInitDirsTask.doFirst {
//            project.logger.info("$project.path:dynamicInitDirsTask:doFirst")
//        }
//        dynamicInitDirsTask.doLast {
//            project.logger.info("$project.path:dynamicInitDirsTask:doLast start")
//
//            new File(MApplicationExtension.instance.buildOutputPath).mkdirs()
//            new File(MApplicationExtension.instance.buildOutputPath, 'remoteApk').mkdirs()
//            new File(MApplicationExtension.instance.buildOutputPath, "jni/armeabi/").mkdirs()
//            new File(MApplicationExtension.instance.buildOutputPath, "jni/x86/").mkdirs()
//
//            project.buildDir.mkdirs()
//            new File(project.buildDir, 'gen/r').mkdirs()
//            new File(project.buildDir, 'intermediates').mkdirs()
//            new File(project.buildDir, 'intermediates/classes').mkdirs()
//            new File(project.buildDir, 'intermediates/classes-obfuscated').mkdirs()
//            new File(project.buildDir, 'intermediates/res').mkdirs()
//            new File(project.buildDir, 'intermediates/dex').mkdirs()
//
//            project.logger.info("$project.path:dynamicInitDirsTask:doLast end")
//        }
//        Set<Task> dependTasks = project.rootProject.getTasksByName("dynamicAssembleRelease", true)
//
//        if (parentProject != null)
//            dynamicInitDirsTask.dependsOn(dependTasks, "$parentProject.path:dynamicBundleRelease")
//        else
//            dynamicInitDirsTask.dependsOn(dependTasks)
//
//        dynamicInitDirsTask.getDependsOn().each {
//            println "$project.path:dynamicInitDirs:findDependency: " + it.toString()
//        }
//        project.logger.info("$project.path:configureInitTask:end")
//    }
//
//    private Zip configureBundleReleaseTask(Project project) {
//        project.logger.info("$project.path:configureBundleReleaseTask:start")
//        Zip zip = project.tasks.create("dynamicBundleRelease", Zip)
//
//        zip.inputs.file "$project.buildDir/intermediates/dex/${project.name}_dex.zip"
//        zip.inputs.file "$project.buildDir/intermediates/res/resources.zip"
//        zip.outputs.file "$MApplicationExtension.instance.buildOutputPath/${libraryExtension.soName}.so"
//
//        zip.archiveName = "${libraryExtension.soName}.so"
//        zip.destinationDir = project.file(MApplicationExtension.instance.buildOutputPath)
//        zip.duplicatesStrategy = "fail"
//        zip.from project.zipTree("$project.buildDir/intermediates/dex/${project.name}_dex.zip")
//        zip.from project.zipTree("$project.buildDir/intermediates/res/resources.zip")
//        zip.doLast {
//            assert new File("$MApplicationExtension.instance.buildOutputPath/${libraryExtension.soName}.so").exists()
//        }
//        zip.dependsOn "dynamicDexRelease"
//        project.logger.info("$project.path:configureBundleReleaseTask:end")
//        return zip
//    }
//
//    private void configureDexReleaseTask(Project project) {
//        project.logger.info("$project.path:configureDexReleaseTask:start")
//        Exec exec = project.tasks.create("dynamicDexRelease", Exec)
//
//        exec.inputs.file "$project.buildDir/intermediates/classes-obfuscated/classes-obfuscated.jar"
//        exec.outputs.file "$project.buildDir/intermediates/dex/${project.name}_dex.zip"
//
//        exec.workingDir project.buildDir
//        exec.executable MApplicationExtension.instance.dex
//
//        def argv = []
//        argv << '--dex'
//        argv << '--debug'
//        argv << "--output=$project.buildDir/intermediates/dex/${project.name}_dex.zip"
//        argv << "$project.buildDir/intermediates/classes-obfuscated/classes-obfuscated.jar"
//        exec.args = argv
//        exec.dependsOn "dynamicObfuscateRelease"
//        project.logger.info("$project.path:configureDexReleaseTask:end")
//    }
//
//    private void configureObfuscateReleaseTask(Project project) {
//        project.logger.info("$project.path:configureObfuscateReleaseTask:start")
//        ProGuardTask proGuard = project.tasks.create("dynamicObfuscateRelease", ProGuardTask)
//
//        proGuard.inputs.file MApplicationExtension.instance.androidJar
//        proGuard.inputs.file "$project.buildDir/intermediates/res/resources.zip"
//        proGuard.inputs.file "$project.buildDir/intermediates/res/aapt-rules.txt"
//        proGuard.inputs.file libraryExtension.moduleProguardRulesFilePath
//        proGuard.inputs.file "$MApplicationExtension.instance.applicationProject.buildDir/intermediates/classes-proguard/release/classes.jar"
//        proGuard.inputs.files project.fileTree(libraryExtension.libsDirPath).include('*.jar')
//        proGuard.inputs.files project.fileTree("$project.buildDir/gen/r").include('**/*.java')
//        proGuard.inputs.dir "$project.buildDir/intermediates/classes"
//        proGuard.outputs.file "$project.buildDir/intermediates/classes-obfuscated/classes-obfuscated.jar"
//        proGuard.outputs.file "$MApplicationExtension.instance.buildOutputPath/$libraryExtension.soName-mapping.txt"
//        if (parentModuleJar != null)
//            proGuard.inputs.file parentModuleJar
//
//        proGuard.injars("$project.buildDir/intermediates/classes")
//        proGuard.injars(project.fileTree(libraryExtension.libsDirPath).include('*.jar'))
//        proGuard.outjars("$project.buildDir/intermediates/classes-obfuscated/classes-obfuscated.jar")
//
//        proGuard.configuration(libraryExtension.moduleProguardRulesFilePath)
//        proGuard.configuration("$project.buildDir/intermediates/res/aapt-rules.txt")
//
//        proGuard.libraryjars(getClasspath())
////        proGuard.libraryjars(MApplicationExtension.instance.androidJar)
////        proGuard.libraryjars(project.fileTree("$MApplicationExtension.instance.sdkDir/extras/android/support/v7/").include('**/**/*.jar'))
////        proGuard.libraryjars(project.fileTree("$MApplicationExtension.instance.buildDir/intermediates/exploded-aar/").include('**/**/**/**/*.jar'))
////        proGuard.libraryjars("$MApplicationExtension.instance.buildDir/intermediates/classes-proguard/release/classes.jar")
//        proGuard.dependsOn "dynamicCopySOOutputs"
//        project.logger.info("$project.path:configureObfuscateReleaseTask:end")
//    }
//
//    private void configureCopySOOutputsTask(Project project) {
//        project.logger.info("$project.path:configureCopySOOutputsTask: start")
//        Copy copy = project.tasks.create("dynamicCopySOOutputs", Copy.class);
//        String subPath = project.buildDir.getParent()
//        copy.inputs.dir "$subPath/libs/armeabi/"
//        copy.inputs.dir "$subPath/libs/x86/"
//        copy.outputs.dir "$MApplicationExtension.instance.buildOutputPath/jni/armeabi/"
//        copy.outputs.dir "$MApplicationExtension.instance.buildOutputPath/jni/x86/"
//        copy.setDescription("复制 so fils 到 $MApplicationExtension.instance.buildOutputPath")
//        copy.from("$subPath/libs/armeabi/").into("$MApplicationExtension.instance.buildOutputPath/jni/armeabi/")
//        copy.from("$subPath/libs/x86/").into("$MApplicationExtension.instance.buildOutputPath/jni/x86/")
//        copy.dependsOn "dynamicCompileRelease"
//        project.logger.info("$project.path:configureCopySOOutputsTask: end")
//    }
//
//    private ConfigurableFileCollection getClasspath() {
//        assert MApplicationExtension.instance.classpath != null && !MApplicationExtension.instance.classpath.isEmpty(), "base-classpath is null or empty!"
//        ConfigurableFileCollection fileCollection = project.files(MApplicationExtension.instance.classpath)
//        if (libJars != null && !libJars.isEmpty())
//            fileCollection = project.files(MApplicationExtension.instance.classpath, libJars)
//        if (!MTextUtil.isEmpty(parentModuleJar))
//            fileCollection = project.files(MApplicationExtension.instance.classpath, libJars, parentModuleJar)
//        return fileCollection
//    }
//
//    private void configureCompileReleaseTask(Project project) {
//        try {
//            project.logger.info("$project.path:configureCompileReleaseTask:start")
//            JavaCompile javaCompile = project.tasks.create("dynamicCompileRelease", JavaCompile.class)
//            javaCompile.inputs.files project.fileTree(libraryExtension.libsDirPath).include('*.jar')
//            javaCompile.inputs.files project.fileTree(libraryExtension.sourceDirPath).include('**/*.java')
//            javaCompile.inputs.files project.fileTree("$project.buildDir/gen/r").include('**/*.java')
//            javaCompile.inputs.file MApplicationExtension.instance.androidJar
//            javaCompile.inputs.file "$MApplicationExtension.instance.applicationProject.buildDir/intermediates/classes-proguard/release/classes.jar"
//            javaCompile.inputs.file "$project.buildDir/intermediates/res/resources.zip"
//            javaCompile.inputs.file "$project.buildDir/intermediates/res/aapt-rules.txt"
//            javaCompile.inputs.dir "$project.buildDir/gen/r"
//            javaCompile.outputs.dir "$project.buildDir/intermediates/classes"
//
//            if (!MTextUtil.isEmpty(parentModuleJar))
//                javaCompile.inputs.file parentModuleJar
//
//            javaCompile.setClasspath(getClasspath())
//
//            //必须设置，否则报错
//            //https://docs.gradle.org/current/dsl/org.gradle.api.tasks.compile.JavaCompile.html Properties of JavaCompile
//            //The character encoding to be used when reading source files. Defaults to null, in which case the platform default encoding will be used.
//            javaCompile.options.encoding = 'UTF-8'
//            javaCompile.setSourceCompatibility(MApplicationExtension.instance.javaCompileVersion)
//            javaCompile.setTargetCompatibility(MApplicationExtension.instance.javaCompileVersion)
//            javaCompile.setDependencyCacheDir(project.file("$project.buildDir/dependency-cache"))
//            javaCompile.setDestinationDir(project.file("$project.buildDir/intermediates/classes"))
//            javaCompile.source(
//                    project.fileTree(libraryExtension.sourceDirPath).include('**/*.java'),
//                    project.fileTree("$project.buildDir/gen/r").include('**/*.java')
//            )
//            javaCompile.dependsOn "dynamicAaptRelease"
//        } catch (Exception e) {
//            e.printStackTrace()
//        }
//        project.logger.info("$project.path:configureCompileReleaseTask:end")
//    }
//
//    private void configureAaptReleaseTask(Project project) {
//        project.logger.info("$project.path:configureDynamicAaptReleaseTask:start")
//        Exec dynamicAaptRelease = project.tasks.create("dynamicAaptRelease", Exec.class)
//        dynamicAaptRelease.inputs.file MApplicationExtension.instance.androidJar
//        dynamicAaptRelease.inputs.file MApplicationExtension.instance.buildOutputBaseApkFilePath
//        dynamicAaptRelease.inputs.file libraryExtension.androidManifestFilePath
//        dynamicAaptRelease.inputs.dir libraryExtension.resourceDirPath
//        dynamicAaptRelease.inputs.dir libraryExtension.assetsDirPath
//
//        //noinspection GroovyUnusedAssignment
//        def packageName = MApplicationExtension.instance.packageName
//        def inputRFile = "$MApplicationExtension.instance.applicationProject.buildDir/generated/source/r/release/" + packageName.replace('.', '/') + "/R.java"
//        //def appPackageName = MApplicationPlugin.appPackageName
//        if (parentProject != null) {
//            inputRFile = parentRFile
//            packageName = "${parentPackageName}-$packageName"
//        }
//
//        println "$project.path:dynamicAaptRelease:packageName==$packageName"
//
//        dynamicAaptRelease.inputs.file inputRFile
//        dynamicAaptRelease.outputs.dir "$project.buildDir/gen/r"
//        dynamicAaptRelease.outputs.file "$project.buildDir/intermediates/res/resources.zip"
//        dynamicAaptRelease.outputs.file "$project.buildDir/intermediates/res/aapt-rules.txt"
//
//        dynamicAaptRelease.doFirst {
//            project.logger.info("$project.path:dynamicAaptRelease:doFirst")
//
//            workingDir project.buildDir
//            executable MApplicationExtension.instance.aapt
//
//            def resourceId = ''
//            def parseApkXml = (new XmlParser()).parse(new File(MApplicationExtension.instance.moduleConfigFilePath))
//            parseApkXml.Module.each { module ->
//                if (module.@packageName == libraryExtension.packageName) {
//                    resourceId = module.@resourceId
//                    println "$project.path:dynamicAaptRelease:dynamicAaptRelease apk_module_config:[packageName:" + module.@packageName + "],[resourceId:$resourceId]"
//                }
//            }
//            def argv = []
//            argv << 'package' //打包
//            argv << "-v"
//            argv << '-f' //强制覆盖已有文件
//            argv << "-I"
//            argv << "$MApplicationExtension.instance.androidJar"  //添加一个已有的固化jar包
//            argv << '-I'
//            argv << MApplicationExtension.instance.buildOutputBaseApkFilePath
//            if (parentProject != null) {
//                argv << '-I'
//                argv << "$parentProject.buildDir/intermediates/res/resources.zip"
//            }
//            argv << '-M'
//            argv << libraryExtension.androidManifestFilePath  //指定manifest文件
//            argv << '-S'
//            argv << libraryExtension.resourceDirPath          //res目录
//            argv << '-A'
//            argv << libraryExtension.assetsDirPath            //assets目录
//            argv << '-m'        //make package directories under location specified by -J
//            argv << '-J'
//            argv << "$project.buildDir/gen/r"         //哪里输出R.java定义
//            argv << '-F'
//            argv << "$project.buildDir/intermediates/res/resources.zip"   //指定apk的输出位置
//            argv << '-G'        //-G  A file to output proguard options into.
//            argv << "$project.buildDir/intermediates/res/aapt-rules.txt"
//            // argv << '--debug-mode'      //manifest的application元素添加android:debuggable="true"
//            argv << '--custom-package'      //指定R.java生成的package包名
//            argv << libraryExtension.packageName
//            argv << '-0'    //指定哪些后缀名不会被压缩
//            argv << 'apk'
//            argv << '--public-R-path'
//            argv << inputRFile
//            argv << '--apk-module'
//            argv << "$resourceId"
//            argv << '--apk-package'
//            argv << "${packageName}"
//            args = argv
//        }
//        dynamicAaptRelease.dependsOn "dynamicInitDirs"
//        project.logger.info("$project.path:configure configureDynamicAaptReleaseTask:end")
//    }
}