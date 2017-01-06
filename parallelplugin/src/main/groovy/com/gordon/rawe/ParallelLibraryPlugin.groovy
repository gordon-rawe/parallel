package com.gordon.rawe

import com.gordon.rawe.TaskNames.TaskNames
import com.gordon.rawe.utils.Helper
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.bundling.Zip
import org.gradle.api.tasks.compile.JavaCompile
import proguard.gradle.ProGuardTask

public class ParallelLibraryPlugin implements Plugin<Project> {
    private ParallelLibraryOptions libraryOptions;

    private Project parentProject = null
    private String parentPackageName = null
    private String parentModuleJar = null
    private String parentRFile = null
    private ParallelLibraryOptions parentOptions = null

    private ConfigurableFileTree libJars = null


    @Override
    public void apply(Project project) {

        libraryOptions = project.extensions.create(ParallelLibraryOptions.optionsName, ParallelLibraryOptions.class);
        project.afterEvaluate {
            project.logger.info("$project.path start to configure...")

            boolean conditionCheck = true;
            if (ParallelSharedOptions.reference == null) {
                project.logger.error("$project.path fail to pass check because SharedOptions are empty...")
                conditionCheck = false
            }
            if (!ParallelSharedOptions.reference.enabled) {
                project.logger.error("$project.path fail to pass check because LibraryPlugin are disabled...")
                conditionCheck = false
            }
            if (libraryOptions == null) {
                project.logger.error("$project.path fail to pass check because acquire LibraryOptions failed...")
                conditionCheck = false
            }
            if (Helper.isInvalid(libraryOptions.packageName)) {
                project.logger.error("$project.path fail to pass check because acquire package name failed...")
                isConditionOK = false
            }
            if (conditionCheck) {
                libraryOptions.initOptionsAfterEvaluate(project)
                configureParentModule(project)
                ensureDirs(project)
                configureLibs(project)
                configureAaptRelease(project)
                configureCompileReleaseTask(project)
                configureCopySOOutputsTask(project)
                configureObfuscateReleaseTask(project)
                configureDexReleaseTask(project)
                configureBundleReleaseTask(project)
            }

            project.logger.info("$project.path configuration finished...")
        }
    }

    private void configureLibs(Project project) {
        libJars = project.fileTree(libraryOptions.libsDirPath).include('*.jar')
        if (libJars != null && !libJars.isEmpty()) {
            project.logger.info("$project.path:------------------------------------------------------------------------------------")
            libJars.each {
                project.logger.info("$project.path configureLibs libJars " + it.path)
            }
            project.logger.info("$project.path:------------------------------------------------------------------------------------")
        }
    }

    private void configureParentModule(Project project) {
        if (Helper.isInvalid(libraryOptions.parentModuleName)) return
        project.logger.info("$project.path configureParentModule parentModuleName is $libraryOptions.parentModuleName")
        parentProject = project.project(libraryOptions.parentModuleName)
        project.evaluationDependsOn(parentProject.path)
        if (parentProject != null) {
            parentOptions = parentProject.extensions.findByName(ParallelLibraryOptions.optionsName)
            if (parentOptions != null) {
                parentPackageName = parentOptions.packageName
                if (!Helper.isInvalid(parentPackageName))
                    parentRFile = "$parentProject.buildDir/gen/r/" + parentPackageName.replace('.', '/') + "/R.java"
            }
            parentModuleJar = "$parentProject.buildDir/intermediates/classes-obfuscated/classes-obfuscated.jar"
        }
        project.tasks.create(TaskNames.CONFIG_PARENT, Task.class) << {
            project.logger.info("$project.path configureParentModule parentModuleProjectName==" + parentProject == null ? "null" : parentProject.name)
            project.logger.info("$project.path configureParentModule parentModuleProjectPath==" + parentProject == null ? "null" : parentProject.path)
            project.logger.info("$project.path configureParentModule parentPackageName==$parentPackageName")
            project.logger.info("$project.path configureParentModule parentModuleJar==$parentModuleJar")
            project.logger.info("$project.path configureParentModule parentRFile==$parentRFile")
        }
    }

    private void ensureDirs(Project project) {
        project.logger.info("$project.path ensureDirs start...")
        Task configDirs = project.tasks.create(TaskNames.CONFIG_DIRS, Task.class);

        configDirs.outputs.dir "$project.buildDir/gen/r"
        configDirs.outputs.dir "$project.buildDir/intermediates"
        configDirs.outputs.dir "$project.buildDir/intermediates/classes"
        configDirs.outputs.dir "$project.buildDir/intermediates/classes-obfuscated"
        configDirs.outputs.dir "$project.buildDir/intermediates/res"
        configDirs.outputs.dir "$project.buildDir/intermediates/dex"

        configDirs.outputs.dir ParallelSharedOptions.reference.buildOutputPath
        configDirs.outputs.dir "$ParallelSharedOptions.reference.buildOutputPath/remoteApk"
        configDirs.outputs.dir "$ParallelSharedOptions.reference.buildOutputPath/jni/armeabi/"
        configDirs.outputs.dir "$ParallelSharedOptions.reference.buildOutputPath/jni/x86/"
        project.logger.info("$project.path $TaskNames.CONFIG_DIRS $ParallelSharedOptions.reference.buildOutputPath")

        configDirs << {
            project.logger.info("$project.path making dirs start...")

            new File(ParallelSharedOptions.reference.buildOutputPath).mkdirs()
            new File(ParallelSharedOptions.reference.buildOutputPath, 'remoteApk').mkdirs()
            new File(ParallelSharedOptions.reference.buildOutputPath, "jni/armeabi/").mkdirs()
            new File(ParallelSharedOptions.reference.buildOutputPath, "jni/x86/").mkdirs()

            project.buildDir.mkdirs()
            new File(project.buildDir, 'gen/r').mkdirs()
            new File(project.buildDir, 'intermediates').mkdirs()
            new File(project.buildDir, 'intermediates/classes').mkdirs()
            new File(project.buildDir, 'intermediates/classes-obfuscated').mkdirs()
            new File(project.buildDir, 'intermediates/res').mkdirs()
            new File(project.buildDir, 'intermediates/dex').mkdirs()

            project.logger.info("$project.path making dirs end...")
        }
        Set<Task> dependTasks = project.rootProject.getTasksByName(TaskNames.ASSEMBLE_RELEASE, true)

        if (parentProject != null)
            configDirs.dependsOn(dependTasks, "$parentProject.path:$TaskNames.BUNDLE_COMPILE")
        else
            configDirs.dependsOn(dependTasks)

        configDirs.getDependsOn().each {
            project.logger.info("$project.path configParallelDirs findDependency: " + it.toString())
        }
        project.logger.info("$project.path ensureDirs end...")
    }

    public void configureAaptRelease(Project project) {
        project.logger.info("$project.path configure $TaskNames.AAPT Task start...")
        Exec aaptTask = project.tasks.create(TaskNames.AAPT, Exec.class)
        aaptTask.inputs.file ParallelSharedOptions.reference.androidJar
        aaptTask.inputs.file ParallelSharedOptions.reference.buildOutputBaseApkFilePath

        aaptTask.inputs.file libraryOptions.androidManifestFilePath
        aaptTask.inputs.dir libraryOptions.resourceDirPath
        aaptTask.inputs.dir libraryOptions.assetsDirPath
        aaptTask.inputs.dir ParallelSharedOptions.reference.moduleConfigFilePath

        //no inspection Groovy Unused Assignment
        def packageName = ParallelSharedOptions.reference.applicationPackageName
        def inputRFile = "$ParallelSharedOptions.reference.applicationBuildDir/generated/source/r/release/" + packageName.replace('.', '/') + "/R.java"
        if (parentProject != null) {
            inputRFile = parentRFile
            packageName = "${parentPackageName}-$packageName"
            project.logger.info("$project.path $TaskNames.AAPT inputRFile is $inputRFile")
            project.logger.info("$project.path $TaskNames.AAPT packageName is $packageName")
        }

        aaptTask.inputs.file inputRFile
        aaptTask.outputs.dir "$project.buildDir/gen/r"
        aaptTask.outputs.file "$project.buildDir/intermediates/res/resources.zip"
        aaptTask.outputs.file "$project.buildDir/intermediates/res/aapt-rules.txt"

        aaptTask.doFirst {
            project.logger.info("$project.path configure $TaskNames.AAPT start...")

            workingDir project.buildDir
            executable ParallelSharedOptions.reference.aapt

            def resourceId = ''
            def parseApkXml = (new XmlParser()).parse(new File(ParallelSharedOptions.reference.moduleConfigFilePath))
            parseApkXml.Module.each { module ->
                if (module.@packageName == libraryOptions.packageName) {
                    resourceId = module.@resourceId
                    project.logger.info("$project.path parallelAaptRelease apk_module_config:[packageName:" + module.@packageName + "],[resourceId:$resourceId]")
                }
            }
            def argv = []
            argv << 'package' //打包
            argv << "-v"
            argv << '-f' //强制覆盖已有文件
            argv << "-I"
            argv << ParallelSharedOptions.reference.androidJar //添加一个已有的固化jar包
            argv << '-I'
            argv << ParallelSharedOptions.reference.buildOutputBaseApkFilePath
            if (parentProject != null) {
                argv << '-I'
                argv << "$parentProject.buildDir/intermediates/res/resources.zip"
            }
            argv << '-M'
            argv << libraryOptions.androidManifestFilePath  //指定manifest文件
            argv << '-S'
            argv << libraryOptions.resourceDirPath          //res目录
            if (new File(libraryOptions.assetsDirPath).exists()) {
                argv << '-A'
                argv << libraryOptions.assetsDirPath            //assets目录
            }
            argv << '-m'        //make package directories under location specified by -J
            argv << '-J'
            argv << "$project.buildDir/gen/r"         //哪里输出R.java定义
            argv << '-F'
            argv << "$project.buildDir/intermediates/res/resources.zip"   //指定apk的输出位置
            argv << '-G'        //-G  A file to output proguard options into.
            argv << "$project.buildDir/intermediates/res/aapt-rules.txt"
            // argv << '--debug-mode'      //manifest的application元素添加android:debuggable="true"
            argv << '--custom-package'      //指定R.java生成的package包名
            argv << libraryOptions.packageName
            argv << '-0'    //指定哪些后缀名不会被压缩
            argv << 'apk'
            argv << '--public-R-path'
            argv << inputRFile
            argv << '--apk-module'
            argv << "$resourceId"
            argv << '--apk-package'
            argv << "${packageName}"
            args = argv
        }
        aaptTask.dependsOn TaskNames.CONFIG_DIRS
        project.logger.info("$project.path configure $TaskNames.AAPT end...")
    }

    private ConfigurableFileCollection getClasspath(Project project) {
        assert ParallelSharedOptions.reference.classpath != null && !ParallelSharedOptions.reference.classpath.isEmpty(), "base-classpath is null or empty!"
        ConfigurableFileCollection fileCollection = project.files(ParallelSharedOptions.reference.classpath)

        if (libJars != null && !libJars.isEmpty())
            fileCollection = project.files(ParallelSharedOptions.reference.classpath, libJars)
        if (!Helper.isInvalid(parentModuleJar))
            fileCollection = project.files(ParallelSharedOptions.reference.classpath, libJars, parentModuleJar)
        return fileCollection
    }

    private void configureCompileReleaseTask(Project project) {
        try {
            project.logger.info("$project.path configureCompileReleaseTask start...")
            JavaCompile javaCompile = project.tasks.create(TaskNames.JAVA_COMPILE, JavaCompile.class)
            javaCompile.inputs.files project.fileTree(libraryOptions.libsDirPath).include('*.jar')
            javaCompile.inputs.files project.fileTree(libraryOptions.sourceDirPath).include('**/*.java')
            javaCompile.inputs.files project.fileTree("$project.buildDir/gen/r").include('**/*.java')
            javaCompile.inputs.file ParallelSharedOptions.reference.androidJar
            javaCompile.inputs.file "$ParallelSharedOptions.reference.applicationBuildDir/intermediates/classes-proguard/release/classes.jar"
            javaCompile.inputs.file "$project.buildDir/intermediates/res/resources.zip"
            javaCompile.inputs.file "$project.buildDir/intermediates/res/aapt-rules.txt"
            javaCompile.inputs.dir "$project.buildDir/gen/r"
            javaCompile.outputs.dir "$project.buildDir/intermediates/classes"

            if (!Helper.isInvalid(parentModuleJar))
                javaCompile.inputs.file parentModuleJar

            javaCompile.setClasspath(getClasspath(project))

            //必须设置，否则报错
            //https://docs.gradle.org/current/dsl/org.gradle.api.tasks.compile.JavaCompile.html Properties of JavaCompile
            //The character encoding to be used when reading source files. Defaults to null, in which case the platform default encoding will be used.
            javaCompile.options.encoding = 'UTF-8'
            javaCompile.setSourceCompatibility(ParallelSharedOptions.reference.javaCompileVersion)
            javaCompile.setTargetCompatibility(ParallelSharedOptions.reference.javaCompileVersion)
            javaCompile.setDependencyCacheDir(project.file("$project.buildDir/dependency-cache"))
            javaCompile.setDestinationDir(project.file("$project.buildDir/intermediates/classes"))
            javaCompile.source(
                    project.fileTree(libraryOptions.sourceDirPath).include('**/*.java'),
                    project.fileTree("$project.buildDir/gen/r").include('**/*.java')
            )
            javaCompile.dependsOn TaskNames.AAPT
        } catch (Exception e) {
            e.printStackTrace()
        }
        project.logger.info("$project.path configure $TaskNames.AAPT end")
    }

    private static void configureCopySOOutputsTask(Project project) {
        project.logger.info("$project.path configure $TaskNames.JAVA_COMPILE start...")
        Copy copy = project.tasks.create(TaskNames.COPY_SO_OUTPUT, Copy.class);
        String subPath = project.buildDir.getParent()
        copy.inputs.dir "$subPath/libs/armeabi/"
        copy.inputs.dir "$subPath/libs/x86/"
        copy.outputs.dir "$ParallelSharedOptions.reference.buildOutputPath/jni/armeabi/"
        copy.outputs.dir "$ParallelSharedOptions.reference.buildOutputPath/jni/x86/"
        copy.setDescription("copy so fils to $ParallelSharedOptions.reference.buildOutputPath")
        copy.from("$subPath/libs/armeabi/").into("$ParallelSharedOptions.reference.buildOutputPath/jni/armeabi/")
        copy.from("$subPath/libs/x86/").into("$ParallelSharedOptions.reference.buildOutputPath/jni/x86/")
        copy.dependsOn TaskNames.JAVA_COMPILE
        project.logger.info("$project.path configure $TaskNames.JAVA_COMPILE end...")
    }

    private void configureObfuscateReleaseTask(Project project) {
        project.logger.info("$project.path configure $TaskNames.OBFUSCATE start...")
        ProGuardTask proGuard = project.tasks.create(TaskNames.OBFUSCATE, ProGuardTask)

        proGuard.inputs.file ParallelSharedOptions.reference.androidJar
        proGuard.inputs.file "$project.buildDir/intermediates/res/resources.zip"
        proGuard.inputs.file "$project.buildDir/intermediates/res/aapt-rules.txt"
        proGuard.inputs.file libraryOptions.moduleProguardRulesFilePath
        proGuard.inputs.file "$ParallelSharedOptions.reference.applicationBuildDir/intermediates/classes-proguard/release/classes.jar"
        proGuard.inputs.files project.fileTree(libraryOptions.libsDirPath).include('*.jar')
        proGuard.inputs.files project.fileTree("$project.buildDir/gen/r").include('**/*.java')
        proGuard.inputs.dir "$project.buildDir/intermediates/classes"
        proGuard.outputs.file "$project.buildDir/intermediates/classes-obfuscated/classes-obfuscated.jar"
        proGuard.outputs.file "$ParallelSharedOptions.reference.buildOutputPath/$libraryOptions.soName-mapping.txt"
        if (parentModuleJar != null)
            proGuard.inputs.file parentModuleJar

        proGuard.injars("$project.buildDir/intermediates/classes")
        File libDir = new File(libraryOptions.libsDirPath)
        if (!libDir.exists()) libDir.mkdirs()
        proGuard.injars(project.fileTree(libraryOptions.libsDirPath).include('*.jar'))
        proGuard.outjars("$project.buildDir/intermediates/classes-obfuscated/classes-obfuscated.jar")

        proGuard.configuration(libraryOptions.moduleProguardRulesFilePath)
        proGuard.configuration("$project.buildDir/intermediates/res/aapt-rules.txt")

        proGuard.libraryjars(getClasspath(project))
        proGuard.dependsOn TaskNames.COPY_SO_OUTPUT
        project.logger.info("$project.path configure $TaskNames.OBFUSCATE end...")
    }

    private static void configureDexReleaseTask(Project project) {
        project.logger.info("$project.path configure $TaskNames.DEX_COMPILE start...")
        Exec exec = project.tasks.create(TaskNames.DEX_COMPILE, Exec.class)

        exec.inputs.file "$project.buildDir/intermediates/classes-obfuscated/classes-obfuscated.jar"
        exec.outputs.file "$project.buildDir/intermediates/dex/${project.name}_dex.zip"

        exec.workingDir project.buildDir
        exec.executable ParallelSharedOptions.reference.dex

        def argv = []
        argv << '--dex'
        argv << '--debug'
        argv << "--output=$project.buildDir/intermediates/dex/${project.name}_dex.zip"
        argv << "$project.buildDir/intermediates/classes-obfuscated/classes-obfuscated.jar"
        exec.args = argv
        exec.dependsOn TaskNames.OBFUSCATE
        project.logger.info("$project.path configure $TaskNames.DEX_COMPILE end...")
    }

    private void configureBundleReleaseTask(Project project) {
        project.logger.info("$project.path configure $TaskNames.BUNDLE_COMPILE start...")
        Zip zip = project.tasks.create(TaskNames.BUNDLE_COMPILE, Zip.class)

        zip.inputs.file "$project.buildDir/intermediates/dex/${project.name}_dex.zip"
        zip.inputs.file "$project.buildDir/intermediates/res/resources.zip"
        zip.outputs.file "$ParallelSharedOptions.reference.buildOutputPath/${libraryOptions.soName}.so"

        zip.archiveName = "${libraryOptions.soName}.so"
        zip.destinationDir = project.file(ParallelSharedOptions.reference.buildOutputPath)
        zip.duplicatesStrategy = "fail"
        zip.from project.zipTree("$project.buildDir/intermediates/dex/${project.name}_dex.zip")
        zip.from project.zipTree("$project.buildDir/intermediates/res/resources.zip")
        zip.doLast {
            assert new File("$ParallelSharedOptions.reference.buildOutputPath/${libraryOptions.soName}.so").exists()
        }
        zip.dependsOn TaskNames.DEX_COMPILE
        project.logger.info("$project.path configure $TaskNames.BUNDLE_COMPILE end")
    }
}