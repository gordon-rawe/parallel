package com.gordon.rawe

import com.gordon.rawe.TaskNames.TaskNames
import com.gordon.rawe.utils.Helper
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.bundling.Zip

import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

public class ParallelApplicationPlugin implements Plugin<Project> {
    ParallelApplicationOptions applicationOptions

    @Override
    void apply(Project project) {
        applicationOptions = project.extensions.create(ParallelApplicationOptions.optionsName, ParallelApplicationOptions.class)
        project.afterEvaluate {
            applicationOptions.initOptions(project)
            configureClasspath(project)
            configureCleanTask(project)
            configureAssembleReleaseTask(project)
            configureReloadTask(project)
            configureRepackTask(project)
            configureResignTask(project)
            configureRealignTask(project)
            configureConcatMappingsTask(project)
            letsGo(project)
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
            project.logger.info("$project.path configureCleanTask tasks not contains clean, return!")
            return
        }
        clean.doLast {
            project.delete project.buildDir
            project.delete ParallelSharedOptions.reference.buildOutputPath
        }
    }

    /** 这里的manifest拷贝出来不知道有没有用，待后续验证 */
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
            rename ParallelSharedOptions.reference.releaseApkFileName, "$ParallelSharedOptions.reference.buildOutputPrefix$ParallelSharedOptions.Default.DEFAULT_BASE_APK_SUFFIX"
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

    private static void configureReloadTask(Project project) {
        project.logger.info("$project.path configure $TaskNames.RELOAD start...")
        Zip zip = project.tasks.create(TaskNames.RELOAD, Zip.class);

        zip.inputs.file ParallelSharedOptions.reference.buildOutputBaseApkFilePath
        zip.inputs.files project.fileTree(new File(ParallelSharedOptions.reference.buildOutputPath)).include('*.so')
        //增加so文件输入
        zip.inputs.file project.fileTree(new File(ParallelSharedOptions.reference.buildOutputPath, 'jni'))
        zip.outputs.file ParallelSharedOptions.reference.buildOutputReloadedApkFilePath
        zip.setDescription("$TaskNames.RELOAD task")

        zip.into(ParallelSharedOptions.reference.soLocation) {
            from project.fileTree(new File(ParallelSharedOptions.reference.buildOutputPath)).include('*.so')
        }
        zip.into('lib') {
            from project.fileTree(new File(ParallelSharedOptions.reference.buildOutputPath, 'jni'))
        }
        zip.from(project.zipTree(ParallelSharedOptions.reference.buildOutputBaseApkFilePath)) {
            exclude('**/META-INF/*.SF')
            exclude('**/META-INF/*.RSA')
        }

        zip.destinationDir project.file(ParallelSharedOptions.reference.buildOutputPath)
        zip.archiveName "$ParallelSharedOptions.reference.buildOutputPrefix$ParallelSharedOptions.Default.DEFAULT_RELOADED_APK_SUFFIX"

        Set<Task> dependTasks = project.rootProject.getTasksByName(TaskNames.BUNDLE_COMPILE, true)
        zip.setDependsOn(dependTasks)
        dependTasks.each {
            project.logger.info("$project.path find dependency: " + it.toString())
        }
        project.logger.info("$project.path configure $TaskNames.RELOAD end...")
    }

    private static repackApk(String originApk, String targetApk, Project project) {
        project.logger.info("$project.path 重新打包apk: 增加压缩,压缩resources.arsc)")
        def noCompressExt = [".jpg", ".jpeg", ".png", ".gif",
                             ".wav", ".mp2", ".mp3", ".ogg", ".aac",
                             ".mpg", ".mpeg", ".mid", ".midi", ".smf", ".jet",
                             ".rtttl", ".imy", ".xmf", ".mp4", ".m4a",
                             ".m4v", ".3gp", ".3gpp", ".3g2", ".3gpp2",
                             ".amr", ".awb", ".wma", ".wmv"]
        ZipFile zipFile = new ZipFile(originApk)
        ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(targetApk)))
        zipFile.entries().each { entryIn ->
            if (entryIn.directory) {
                project.logger.info("$project.path:apply dynamicApplication:${entryIn.name} is a directory")
            } else {
                def entryOut = new ZipEntry(entryIn.name)
                def dotPos = entryIn.name.lastIndexOf('.')
                def ext = (dotPos >= 0) ? entryIn.name.substring(dotPos) : ""
                def isRes = entryIn.name.startsWith('res/')
                if (isRes && ext in noCompressExt) {
                    entryOut.method = ZipEntry.STORED
                    entryOut.size = entryIn.size
                    entryOut.compressedSize = entryIn.size
                    entryOut.crc = entryIn.crc
                } else {
                    entryOut.method = ZipEntry.DEFLATED
                }
                zos.putNextEntry(entryOut)
                zos << zipFile.getInputStream(entryIn)
                zos.closeEntry()
            }
        }
        zos.finish()
        zos.close()
        zipFile.close()
        project.logger.info("$project.path 压缩结束")
    }

    private void configureRepackTask(Project project) {
        project.logger.info("$project.path configure $TaskNames.REPACK start...")
        Task repackTask = project.tasks.create(TaskNames.REPACK, Task.class);
        repackTask.inputs.file ParallelSharedOptions.reference.buildOutputReloadedApkFilePath
        repackTask.outputs.file ParallelSharedOptions.reference.buildOutputRepackedApkFilePath

        repackTask.doLast {
            File oldApkFile = project.file(ParallelSharedOptions.reference.buildOutputReloadedApkFilePath)
            assert oldApkFile != null: "没有找到release包！"
            File newApkFile = new File(ParallelSharedOptions.reference.buildOutputRepackedApkFilePath)
            repackApk(oldApkFile.absolutePath, newApkFile.absolutePath, project) //重新打包
            assert newApkFile.exists(): "没有找到重新压缩的release包！"
        }
        repackTask.dependsOn TaskNames.RELOAD
        project.logger.info("$project.path configure $TaskNames.REPACK end...")
    }

    private void configureResignTask(Project project) {
        project.logger.info("$project.path configure $TaskNames.RESIGN start...")
        Exec resignTask = project.tasks.create(TaskNames.RESIGN, Exec.class)
        resignTask.inputs.file ParallelSharedOptions.reference.buildOutputRepackedApkFilePath
        resignTask.outputs.file ParallelSharedOptions.reference.buildOutputResignedApkFilePath

        def jarSigner = ParallelSharedOptions.reference.jarSigner
        if (jarSigner == null && $ { System.env.'JAVA_HOME' } != null) {
            jarSigner = $ { System.env.'JAVA_HOME' }
        }
        assert jarSigner != null: "没有找到jarsigner，可以手动在JAVA_HOME中配置或在插件中指定！"
        resignTask.doFirst {
            workingDir ParallelSharedOptions.reference.buildOutputPath
            executable jarSigner

            def argv = []
            argv << '-verbose'
            argv << '-sigalg'
            argv << 'SHA1withRSA'
            argv << '-digestalg'
            argv << 'SHA1'
            argv << '-keystore'
            argv << applicationOptions.keystore
            argv << '-storepass'
            argv << applicationOptions.storePassword
            argv << '-keypass'
            argv << applicationOptions.keyPassword
            argv << '-signedjar'
            argv << "$ParallelSharedOptions.reference.buildOutputPrefix$ParallelSharedOptions.Default.DEFAULT_RESIGNED_APK_SUFFIX"
            argv << "$ParallelSharedOptions.reference.buildOutputPrefix$ParallelSharedOptions.Default.DEFAULT_REPACKED_APK_SUFFIX"
            argv << applicationOptions.keyAlias
            args = argv
        }
        resignTask.dependsOn TaskNames.REPACK
        project.logger.info("$project.path configure $TaskNames.RESIGN end...")
    }

    private static void configureRealignTask(Project project) {
        project.logger.info("$project.path configure $TaskNames.REALIGN start...")
        Exec realignTask = project.tasks.create(TaskNames.REALIGN, Exec.class)
        realignTask.inputs.file ParallelSharedOptions.reference.buildOutputReloadedApkFilePath
        realignTask.outputs.file ParallelSharedOptions.reference.buildOutputRepackedApkFilePath

        File oldApkFile = project.file(ParallelSharedOptions.reference.buildOutputResignedApkFilePath)
        assert oldApkFile != null: "没有找到release包！"
        File newApkFile = new File(ParallelSharedOptions.reference.buildOutputFinalApkFilePath)

        realignTask.doFirst {
            commandLine ParallelSharedOptions.reference.zipAlign
            def argv = []
            argv << '-f'    //overwrite existing outfile.zip
            // argv << '-z'    //recompress using Zopfli
            argv << '-v'    //verbose output
            argv << '4'     //alignment in bytes, e.g. '4' provides 32-bit alignment
            argv << oldApkFile.absolutePath
            argv << newApkFile.absolutePath
            args = argv
        }
        realignTask << {
            assert newApkFile.exists(): "没有找到重新zipalign的release包！"
        }
        realignTask.dependsOn TaskNames.RESIGN
        project.logger.info("$project.path configure $TaskNames.REALIGN end...")
    }

    private static void configureConcatMappingsTask(Project project) {
        project.logger.info("$project.path configure $TaskNames.CONCAT_MAPPINGS start...")

        Task concatMapTask = project.tasks.create(TaskNames.CONCAT_MAPPINGS, Task.class);
        concatMapTask.inputs.files project.fileTree(new File(ParallelSharedOptions.reference.buildOutputPath)).include('*mapping.txt')
        concatMapTask.outputs.file "$ParallelSharedOptions.reference.buildOutputPath/$ParallelSharedOptions.reference.buildOutputPrefix-mapping-final.txt"

        concatMapTask << {
            FileCollection sources = project.fileTree(new File(ParallelSharedOptions.reference.buildOutputPath)).include('*mapping.txt')
            File target = new File("$ParallelSharedOptions.reference.buildOutputPath/$ParallelSharedOptions.reference.buildOutputPrefix-mapping-final.txt")
            File tmp = File.createTempFile('concat', null, target.getParentFile())
            tmp.withWriter { writer ->
                sources.each { file ->
                    file.withReader { reader ->
                        writer << reader
                    }
                }
            }
            target.delete()
            tmp.renameTo(target)
        }
        concatMapTask.dependsOn TaskNames.REALIGN
        project.logger.info("$project.path configure $TaskNames.CONCAT_MAPPINGS end...")
    }

    private void letsGo(Project project) {
        project.logger.info("$project.path configure $TaskNames.GO start...")
        Task letsGo = project.tasks.create(TaskNames.GO, Task.class)
        letsGo.dependsOn TaskNames.CONCAT_MAPPINGS
        project.logger.info("$project.path configure $TaskNames.GO end...")
    }
}