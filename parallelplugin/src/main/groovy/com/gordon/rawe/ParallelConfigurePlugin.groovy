package com.gordon.rawe

import org.gradle.api.Plugin
import org.gradle.api.Project

public class ParallelConfigurePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.afterEvaluate {
            project.task("show") << {
                println project.buildDir
                println project.project.buildDir
                println project.path
            }
        }
    }
}