package com.dao.convention.plugins

import com.dao.convention.BUILD_LOGIC
import com.dao.convention.configureSpotless
import com.dao.convention.extensions.JacocoAggregationExtension
import com.dao.convention.requireIsRoot
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

internal abstract class RootConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.requireIsRoot()

        with(project) {
            pluginManager.apply(JacocoAggregationConventionPlugin::class)

            configureSpotless(
                gradleTargets = "$BUILD_LOGIC/**/*.gradle.kts",
                kotlinTargets = "$BUILD_LOGIC/**/*.kt",
            )

            extensions.configure<JacocoAggregationExtension> {
                modules.addAll(
                    ":app",
                    ":module-feature",
                    ":module-core",
                    ":module-jvm",
                )
            }
        }
    }
}
