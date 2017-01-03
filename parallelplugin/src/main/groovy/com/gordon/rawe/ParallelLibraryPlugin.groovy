package com.gordon.rawe

import com.gordon.rawe.TaskNames.TaskNames
import com.gordon.rawe.utils.Helper
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

public class ParallelLibraryPlugin implements Plugin<Project> {
    private ParallelLibraryOptions libraryOptions;

    private Project parentProject = null
    private String parentPackageName = null
    private String parentModuleJar = null
    private String parentRFile = null
    private ParallelLibraryOptions parentOptions = null

//    private ConfigurableFileTree libJars = null;


    @Override
    public void apply(Project project) {

        libraryOptions = project.extensions.create(ParallelLibraryOptions.optionsName, ParallelLibraryOptions.class);
        project.afterEvaluate {
            configureParentModule(project)
        }
    }

    private void configureParentModule(Project project) {
        println "$project.path:configureParentModule:parentModuleName==$libraryOptions.parentModuleName"
        if (!Helper.isInvalid(libraryOptions.parentModuleName)) {
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
        }
        Task task = project.tasks.create(TaskNames.CONFIG_PARENT, Task.class)
        task << {
            println "$project.path:configureParentModule:parentModuleExtension:" + (parentOptions == null ? "null" : "\n" + parentOptions.toString())
            println "$project.path:configureParentModule:parentModuleProjectName==" + (parentProject == null ? "null" : parentProject.name)
            println "$project.path:configureParentModule:parentModuleProjectPath==" + (parentProject == null ? "null" : parentProject.path)
            println "$project.path:configureParentModule:parentPackageName==$parentPackageName"
            println "$project.path:configureParentModule:parentModuleJar==$parentModuleJar"
            println "$project.path:configureParentModule:parentRFile==$parentRFile"
        }
    }

//    public void configureAaptRelease(Project project) {
//        project.logger.info("$project.path:configure $TaskNames.AAPT Task start...")
//        Exec aaptTask = project.tasks.create(TaskNames.AAPT, Exec.class)
//        aaptTask.inputs.file ParallelSharedOptions.reference.androidJar
//        aaptTask.inputs.file ParallelSharedOptions.reference.buildOutputBaseApkFilePath
//        aaptTask.inputs.file libraryOptions.androidManifestFilePath
//        aaptTask.inputs.dir libraryOptions.resourceDirPath
//        aaptTask.inputs.dir libraryOptions.assetsDirPath
//
//        //no inspection Groovy Unused Assignment
//        def packageName = ParallelSharedOptions.reference.applicationPackageName
//        def inputRFile = "$ParallelSharedOptions.reference.applicationBuildDir/generated/source/r/release/" + packageName.replace('.', '/') + "/R.java"
//        //def appPackageName = MApplicationPlugin.appPackageName
//        if (parentProject != null) {
//            inputRFile = parentRFile
//            packageName = "${parentPackageName}-$packageName"
//        }
//
//        println "$project.path:dynamicAaptRelease:packageName==$packageName"
//
//        aaptTask.inputs.file inputRFile
//        aaptTask.outputs.dir "$project.buildDir/gen/r"
//        aaptTask.outputs.file "$project.buildDir/intermediates/res/resources.zip"
//        aaptTask.outputs.file "$project.buildDir/intermediates/res/aapt-rules.txt"
//
//        aaptTask.doFirst {
//            project.logger.info("$project.path:dynamicAaptRelease:doFirst")
//
//            workingDir project.buildDir
//            executable MApplicationExtension.instance.aapt
//
//            def resourceId = ''
//            def parseApkXml = (new XmlParser()).parse(new File(MApplicationExtension.instance.moduleConfigFilePath))
//            parseApkXml.Module.each { module ->
//                if (module.@packageName == libraryOptions.packageName) {
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
//            argv << libraryOptions.androidManifestFilePath  //指定manifest文件
//            argv << '-S'
//            argv << libraryOptions.resourceDirPath          //res目录
//            argv << '-A'
//            argv << libraryOptions.assetsDirPath            //assets目录
//            argv << '-m'        //make package directories under location specified by -J
//            argv << '-J'
//            argv << "$project.buildDir/gen/r"         //哪里输出R.java定义
//            argv << '-F'
//            argv << "$project.buildDir/intermediates/res/resources.zip"   //指定apk的输出位置
//            argv << '-G'        //-G  A file to output proguard options into.
//            argv << "$project.buildDir/intermediates/res/aapt-rules.txt"
//            // argv << '--debug-mode'      //manifest的application元素添加android:debuggable="true"
//            argv << '--custom-package'      //指定R.java生成的package包名
//            argv << libraryOptions.packageName
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
//        aaptTask.dependsOn "dynamicInitDirs"
//        project.logger.info("$project.path:configure configureDynamicAaptReleaseTask:end")
//    }
}