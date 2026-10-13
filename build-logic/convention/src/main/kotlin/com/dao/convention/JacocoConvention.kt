package com.dao.convention

import com.android.build.api.artifact.ScopedArtifact
import com.android.build.api.variant.ScopedArtifacts
import com.android.build.api.variant.Variant
import com.dao.convention.services.CoverageReportLinkService
import com.dao.convention.tasks.CoverageCollectTask
import com.dao.convention.tasks.CoverageReportTask
import java.io.File
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import org.gradle.build.event.BuildEventsListenerRegistry
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.registerIfAbsent

internal fun Project.registerCoverageTask(
    variant: Variant,
    taskName: String,
    outputTaskDir: String,
    taskDependsOn: Set<String>,
    registry: BuildEventsListenerRegistry,
    configure: CoverageCollectTask.() -> Unit,
): TaskProvider<CoverageCollectTask> {
    return tasks.register<CoverageCollectTask>("collect$taskName") {
        group = COVERAGE_TASK_GROUP
        description = "Coleta a cobertura de testes para: ${variant.name}."
        outputDir.set(layout.buildDirectory.dir(outputTaskDir))
        taskDependsOn.forEach(::dependsOn)
        configure()
    }.also { collect ->
        variant.artifacts
            .forScope(ScopedArtifacts.Scope.PROJECT)
            .use(collect)
            .toGet(
                ScopedArtifact.CLASSES,
                CoverageCollectTask::classJars,
                CoverageCollectTask::classDirectories,
            )

        collect.configure {
            variant.sources.kotlin?.all?.let { provider ->
                sourceDirectories.from(provider)
            }
        }

        tasks.register<CoverageReportTask>("report$taskName") {
            group = COVERAGE_TASK_GROUP
            description = "Gera o relatório de cobertura de testes para: ${variant.name}."
            coverageFiles.from(collect.flatMap(CoverageCollectTask::outputDir))
            registry.onTaskCompletion(linkServiceBuilder(name, reports.html.entryPoint))
            dependsOn(collect)
        }
    }
}

private fun Project.linkServiceBuilder(taskName: String, report: File): Provider<CoverageReportLinkService> {
    return gradle.sharedServices.registerIfAbsent(
        name = taskName,
        implementationType = CoverageReportLinkService::class,
    ) {
        parameters.task.set("$path:$taskName")
        parameters.reportHtml.set(report)
    }
}
