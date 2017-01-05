package com.gordon.rawe

import com.gordon.rawe.TaskNames.TaskNames
import com.gordon.rawe.utils.Helper
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.tasks.Copy

public class ParallelApplicationPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        ParallelApplicationOptions applicationOptions = project.extensions.create(ParallelApplicationOptions.optionsName, ParallelApplicationOptions.class)
        project.afterEvaluate {
            applicationOptions.initOptions(project)
            configureClasspath(project)
            configureCleanTask(project)
            configureAssembleReleaseTask(project)
        }
    }

    private static void configureClasspath(Project project) {
        project.logger.info("$project.path configureClasspath start...")
        ConfigurableFileCollection classpath = project.files(
                ParallelSharedOptions.reference.androidJar,
                "$project.buildDir/intermediates/classes-proguard/release/classes.jar",
                project.fileTree("$project.buildDir/intermediates/exploded-aar/").include('**/**/**/**/*.jar'),
                project.fileTree("$ParallelSharedOptions.reference.sdkDir/extras/android/m2repository/com/android/support/").include("**/$ParallelSharedOptions.reference.supportLibraryVersion/*-sources.jar"),
                project.fileTree("$ParallelSharedOptions.reference.sdkDir/extras/android/support/v7/").include('**/**/*.jar'),//如果上一句path找不到，则在这个里面找，注意先后顺序
                //project.fileTree("$ParallelSharedOptions.reference.sdkDir/extras/android/support/v7/appcompat/libs/").include('*.jar'),//only need android-support-v4,v7 jar
        )
        if (!Helper.isInvalid(ParallelSharedOptions.reference.apacheJar))
            classpath.files.add(ParallelSharedOptions.reference.apacheJar)
        project.logger.info("$project.path:------------------------------------------------------------------------------------")
        classpath.each {
            project.logger.info("$project.path configure CompileReleaseTask classpath:" + it.path)
        }
        project.logger.info("$project.path:------------------------------------------------------------------------------------")
        ParallelSharedOptions.reference.classpath = classpath;
        project.logger.info("$project.path configureClasspath end...")
    }

    private static void configureCleanTask(Project project) {
        Task clean = project.tasks.getByName("clean")
        if (clean == null) {
            println("$project.path configureCleanTask tasks not contains clean, return!")
            return
        }
        clean.doLast {
            project.delete project.buildDir
            project.delete ParallelSharedOptions.reference.buildOutputPath
        }
    }

    private static void configureAssembleReleaseTask(Project project) {
        project.logger.info("$project.path apply configureDynamicAssembleRelease start...")
        Copy copy = project.tasks.create(TaskNames.ASSEMBLE_RELEASE, Copy.class);
        copy.inputs.file "$project.buildDir/outputs/mapping/release/mapping.txt"
        copy.inputs.file ParallelSharedOptions.reference.releaseApkFilePath
//        copy.inputs.file "$project.buildDir/outputs/apk/AndroidManifest.xml"
//        copy.inputs.file "$project.buildDir/intermediates/full/release/AndroidManifest.xml"

        copy.outputs.file "$ParallelSharedOptions.reference.buildOutputPath/$ParallelSharedOptions.reference.buildOutputPrefix-base-mapping.txt"
        copy.outputs.file ParallelSharedOptions.reference.buildOutputBaseApkFilePath
//        copy.outputs.file "$ParallelSharedOptions.reference.buildOutputPath/AndroidManifest.xml"

        copy.setDescription("Copy $project.buildDir to $ParallelSharedOptions.reference.buildOutputPath")
        copy.from(ParallelSharedOptions.reference.releaseApkFilePath) {
            rename ParallelSharedOptions.reference.releaseApkFileName, "$ParallelSharedOptions.reference.buildOutputPrefix-base-release.apk"
        }
        copy.from("$project.buildDir/outputs/mapping/release/mapping.txt") {
            rename 'mapping.txt', "$ParallelSharedOptions.reference.buildOutputPrefix-base-mapping.txt"
        }
        //todo make sure
//        copy.from("$project.buildDir/intermediates/manifests/full/release/AndroidManifest.xml")

        copy.into(ParallelSharedOptions.reference.buildOutputPath)
        copy.dependsOn "assembleRelease"
        project.logger.info("$project.path apply $TaskNames.ASSEMBLE_RELEASE end")
    }
}