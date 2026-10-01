package com.dao.convention.plugins

import com.dao.convention.createJacocoResolvableConfiguration
import com.dao.convention.dependencies.libs
import com.dao.convention.dependencies.version
import com.dao.convention.extensions.createJacocoAggregationExtension
import com.dao.convention.extensions.createJacocoVerificationExtension
import com.dao.convention.tasks.register.registerCoverageTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.build.event.BuildEventsListenerRegistry
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.testing.jacoco.plugins.JacocoPlugin
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import javax.inject.Inject

internal abstract class JacocoAggregationConventionPlugin @Inject constructor(
    private val registry: BuildEventsListenerRegistry,
) : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            pluginManager.apply(JacocoPlugin::class)

            configure<JacocoPluginExtension> {
                toolVersion = libs.versions.jacoco.version
            }

            val extensionVerification = createJacocoVerificationExtension()
            val extensionAggregation = createJacocoAggregationExtension()
            val configuration = createJacocoResolvableConfiguration()

            configuration.configure {
                dependencies.addAllLater(
                    extensionAggregation.modules.map { paths ->
                        paths.map { path ->
                            project.dependencies.project(mapOf("path" to path))
                        }
                    },
                )
            }

            registerCoverageTask(
                registry = registry,
                configuration = configuration,
                verification = extensionVerification,
            )
        }
    }
}
