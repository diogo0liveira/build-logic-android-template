package com.dao.convention.plugins

import com.dao.convention.COVERAGE_TASK_GROUP
import com.dao.convention.artifactFiles
import com.dao.convention.createJacocoResolvableConfiguration
import com.dao.convention.dependencies.libs
import com.dao.convention.dependencies.version
import com.dao.convention.extensions.JacocoAggregationExtension
import com.dao.convention.isRoot
import com.dao.convention.services.CoverageReportLinkService
import com.dao.convention.tasks.CoverageReportTask
import java.io.File
import javax.inject.Inject
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.build.event.BuildEventsListenerRegistry
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.registerIfAbsent
import org.gradle.testing.jacoco.plugins.JacocoPlugin
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension

internal abstract class JacocoAggregationConventionPlugin @Inject constructor(
    private val registry: BuildEventsListenerRegistry,
) : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            pluginManager.apply(JacocoPlugin::class)

            configure<JacocoPluginExtension> {
                toolVersion = libs.versions.jacoco.version
            }

            val configuration = createJacocoResolvableConfiguration()
            val extension = extensions.create<JacocoAggregationExtension>(JACOCO_AGGREGATION_EXTENSION)
            extension.modules.convention(emptyList())

            configuration.configure {
                dependencies.addAllLater(
                    extension.modules.map { paths ->
                        paths.map { path ->
                            project.dependencies.project(mapOf("path" to path))
                        }
                    },
                )
            }

            tasks.register<CoverageReportTask>(JACOCO_AGGREGATION_TASK) {
                group = COVERAGE_TASK_GROUP
                description = "Gera o relatório agregado de cobertura para os módulos do projeto."
                coverageFiles.from(configuration.artifactFiles)
                registry.onTaskCompletion(linkServiceBuilder(reports.html.entryPoint))
            }
        }
    }

    private fun Project.linkServiceBuilder(report: File): Provider<CoverageReportLinkService> {
        return gradle.sharedServices.registerIfAbsent(
            name = JACOCO_REPORT_LINK_SERVICE,
            implementationType = CoverageReportLinkService::class,
        ) {
            parameters.task.set(linkServiceIdentify())
            parameters.reportHtml.set(report)
        }
    }

    private fun Project.linkServiceIdentify(): String {
        return if (isRoot) {
            ":$JACOCO_AGGREGATION_TASK"
        } else {
            "$path:$JACOCO_AGGREGATION_TASK"
        }
    }

    private companion object {
        private const val JACOCO_REPORT_LINK_SERVICE = "jacocoReportLinkService"
        private const val JACOCO_AGGREGATION_EXTENSION = "jacocoAggregation"
        private const val JACOCO_AGGREGATION_TASK = "jacocoAggregatedReport"
    }
}
