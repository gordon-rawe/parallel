package com.gordon.rawe

import org.gradle.api.Plugin
import org.gradle.api.Project

public class ParallelConfigurePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        ParallelSharedOptions options = project.extensions.create(ParallelSharedOptions.optionsName, ParallelSharedOptions.class)
        project.afterEvaluate {
            options.initOptions(project)
        }
    }
}