package com.gordon.rawe

import com.gordon.rawe.TaskNames.TaskNames
import com.gordon.rawe.utils.Helper
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.tasks.Exec

public class ParallelLibraryPlugin implements Plugin<Project> {
    private ParallelLibraryOptions libraryOptions;
    private String packageName;

    private Project parentProject = null
    private String parentPackageName = null
    private String parentModuleJar = null
    private String parentRFile = null
    private ParallelLibraryOptions parentOptions = null

    private ConfigurableFileTree libJars = null;


    @Override
    public void apply(Project project) {

        libraryOptions = project.extensions.create(ParallelLibraryOptions.optionsName, ParallelLibraryOptions.class);
        project.afterEvaluate {
            libraryOptions.initOptionsAfterEvaluate(project)
            configureParentModule(project)
            configureInitTask(project)
            configureLibs(project)
            configureAaptRelease(project)
        }
    }

    private void configureLibs(Project project) {
        libJars = project.fileTree(libraryOptions.libsDirPath).include('*.jar')
        if (libJars != null && !libJars.isEmpty()) {
            project.logger.info("$project.path:------------------------------------------------------------------------------------")
            libJars.each {
                project.logger.info("$project.path:configureLibs:libJars:" + it.path)
            }
            project.logger.info("$project.path:------------------------------------------------------------------------------------")
        }
    }

    private void configureParentModule(Project project) {
        if (Helper.isInvalid(libraryOptions.parentModuleName)) return
        println "$project.path configureParentModule parentModuleName is $libraryOptions.parentModuleName"
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
            println "$project.path configureParentModule parentModuleProjectName==" + (parentProject == null ? "null" : parentProject.name)
            println "$project.path configureParentModule parentModuleProjectPath==" + (parentProject == null ? "null" : parentProject.path)
            println "$project.path configureParentModule parentPackageName==$parentPackageName"
            println "$project.path configureParentModule parentModuleJar==$parentModuleJar"
            println "$project.path configureParentModule parentRFile==$parentRFile"
        }
    }

    private void configureInitTask(Project project) {
        project.logger.info("$project.path configureInitTask start...")
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

            new File(MApplicationExtension.instance.buildOutputPath).mkdirs()
            new File(MApplicationExtension.instance.buildOutputPath, 'remoteApk').mkdirs()
            new File(MApplicationExtension.instance.buildOutputPath, "jni/armeabi/").mkdirs()
            new File(MApplicationExtension.instance.buildOutputPath, "jni/x86/").mkdirs()

            project.buildDir.mkdirs()
            new File(project.buildDir, 'gen/r').mkdirs()
            new File(project.buildDir, 'intermediates').mkdirs()
            new File(project.buildDir, 'intermediates/classes').mkdirs()
            new File(project.buildDir, 'intermediates/classes-obfuscated').mkdirs()
            new File(project.buildDir, 'intermediates/res').mkdirs()
            new File(project.buildDir, 'intermediates/dex').mkdirs()

            project.logger.info("$project.path making dirs end...")
        }
//        Set<Task> dependTasks = project.rootProject.getTasksByName("dynamicAssembleRelease", true)
//
//        if (parentProject != null)
//            configDirs.dependsOn(dependTasks, "$parentProject.path:dynamicBundleRelease")
//        else
//            configDirs.dependsOn(dependTasks)
//
//        configDirs.getDependsOn().each {
//            println "$project.path:dynamicInitDirs:findDependency: " + it.toString()
//        }
//        project.logger.info("$project.path:configureInitTask:end")
    }

    public void configureAaptRelease(Project project) {
        project.logger.info("$project.path configure $TaskNames.AAPT Task start...")
        Exec aaptTask = project.tasks.create(TaskNames.AAPT, Exec.class)
        aaptTask.inputs.file ParallelSharedOptions.reference.androidJar
        aaptTask.inputs.file ParallelSharedOptions.reference.buildOutputBaseApkFilePath

        aaptTask.inputs.file libraryOptions.androidManifestFilePath
        aaptTask.inputs.dir libraryOptions.resourceDirPath
        aaptTask.inputs.dir libraryOptions.assetsDirPath

        //no inspection Groovy Unused Assignment
        def packageName = ParallelSharedOptions.reference.applicationPackageName
        def inputRFile = "$ParallelSharedOptions.reference.applicationBuildDir/generated/source/r/release/" + packageName.replace('.', '/') + "/R.java"

        if (parentProject != null) {
            inputRFile = parentRFile
            packageName = "${parentPackageName}-$packageName"
        }

        println "$project.path dynamicAaptRelease packageName $packageName"

        aaptTask.inputs.file inputRFile
        aaptTask.outputs.dir "$project.buildDir/gen/r"
        aaptTask.outputs.file "$project.buildDir/intermediates/res/resources.zip"
        aaptTask.outputs.file "$project.buildDir/intermediates/res/aapt-rules.txt"

        aaptTask.doFirst {
            project.logger.info("$project.path:dynamicAaptRelease:doFirst")

            workingDir project.buildDir
            executable ParallelSharedOptions.reference.aapt

            def resourceId = ''
            def parseApkXml = (new XmlParser()).parse(new File(ParallelSharedOptions.reference.moduleConfigFilePath))
            parseApkXml.Module.each { module ->
                if (module.@packageName == libraryOptions.packageName) {
                    resourceId = module.@resourceId
                    println "$project.path parallelAaptRelease apk_module_config:[packageName:" + module.@packageName + "],[resourceId:$resourceId]"
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
            argv << '-A'
            argv << libraryOptions.assetsDirPath            //assets目录
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
        project.logger.info("$project.path configure $TaskNames.AAPT :end")
    }
}