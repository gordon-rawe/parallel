package com.gordon.rawe

import org.gradle.api.Plugin
import org.gradle.api.Project

public class ParallelApplicationPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        ParallelApplicationOptions applicationOptions = project.extensions.create(ParallelApplicationOptions.optionsName, ParallelApplicationOptions.class)
        applicationOptions.initOptions(project)
    }
}