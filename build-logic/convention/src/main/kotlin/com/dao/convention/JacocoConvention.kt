package com.dao.convention

import com.android.build.api.artifact.ScopedArtifact
import com.android.build.api.variant.ScopedArtifacts
import com.android.build.api.variant.Variant
import com.dao.convention.services.CoverageReportLinkService
import com.dao.convention.tasks.CoverageCollectTask
import com.dao.convention.tasks.CoverageReportTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import org.gradle.build.event.BuildEventsListenerRegistry
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.registerIfAbsent
import org.gradle.testing.jacoco.tasks.JacocoReportsContainer

internal fun Task.linkServiceBuilder(reports: JacocoReportsContainer): Provider<CoverageReportLinkService> {
    return (if (project.isRoot) ":$name" else path).let { name ->
        project.gradle.sharedServices.registerIfAbsent(
            name = "${name}LinkService",
            implementationType = CoverageReportLinkService::class,
        ) {
            parameters.reportCsv.set(reports.csv.outputLocation.get())
            parameters.reportHtml.set(reports.html.entryPoint)
            parameters.task.set(name)
        }
    }
}

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
            registry.onTaskCompletion(linkServiceBuilder(reports))
            dependsOn(collect)
        }
    }
}
