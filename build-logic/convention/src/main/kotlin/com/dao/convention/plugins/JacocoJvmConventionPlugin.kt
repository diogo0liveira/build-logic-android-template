package com.dao.convention.plugins

import com.dao.convention.COVERAGE_OUTPUT
import com.dao.convention.COVERAGE_TASK_GROUP
import com.dao.convention.createJacocoConsumableConfiguration
import com.dao.convention.dependencies.id
import com.dao.convention.dependencies.libs
import com.dao.convention.dependencies.version
import com.dao.convention.tasks.CoverageCollectTask
import com.dao.convention.tasks.CoverageReportTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.gradle.testing.jacoco.plugins.JacocoPlugin
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension

internal abstract class JacocoJvmConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            pluginManager.apply(JacocoPlugin::class)

            pluginManager.withPlugin(libs.plugins.kotlin.jvm.id) {
                configure<JacocoPluginExtension> {
                    toolVersion = libs.versions.jacoco.version
                }

                val configuration = createJacocoConsumableConfiguration()
                val sourceSets = extensions.getByType<SourceSetContainer>().named("main")

                val collectTask = tasks.register<CoverageCollectTask>(JACOCO_COLLECT_TASK) {
                    group = COVERAGE_TASK_GROUP
                    description = "Coleta os dados de cobertura de testes para o módulo JVM."
                    outputDir.set(layout.buildDirectory.dir("${COVERAGE_OUTPUT}jvm"))
                    dependsOn("test")

                    unitTestExecutionData.from(
                        layout.buildDirectory.file("jacoco/test.exec"),
                    )

                    classFiles.from(sourceSets.map { it.output.classesDirs })
                    sourceDirectories.from(sourceSets.map { it.allSource.sourceDirectories })
                }

                tasks.register<CoverageReportTask>(JACOCO_REPORT_TASK) {
                    group = COVERAGE_TASK_GROUP
                    description = "Gera e exibe o relatório de cobertura de testes do módulo JVM."
                    dependsOn(collectTask)
                    coverageFiles.from(collectTask.flatMap(CoverageCollectTask::outputDir))
                }

                configuration.configure {
                    outgoing.artifact(
                        collectTask.flatMap(CoverageCollectTask::outputDir),
                    )
                }

                tasks.withType<Test>().configureEach {
                    configure<JacocoTaskExtension> {
                        excludes = listOf("jdk.internal.*")
                    }
                }
            }
        }
    }

    private companion object {
        private const val JACOCO_COLLECT_TASK = "collectJvmCoverage"
        private const val JACOCO_REPORT_TASK = "reportJvmCoverage"
    }
}
