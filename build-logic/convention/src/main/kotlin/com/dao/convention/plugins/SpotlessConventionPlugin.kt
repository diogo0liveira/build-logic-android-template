package com.dao.convention.plugins

import com.dao.convention.configureSpotless
import org.gradle.api.Plugin
import org.gradle.api.Project

internal abstract class SpotlessConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.configureSpotless()
    }
}
