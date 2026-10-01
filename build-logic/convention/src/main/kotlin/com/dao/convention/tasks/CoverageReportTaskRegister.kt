package com.dao.convention.tasks

import com.android.build.api.variant.Variant
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.register

internal fun Project.registerCoverageReportTask(
    name: String,
    collectTask: TaskProvider<CoverageCollectTask>,
): TaskProvider<CoverageReportTask> {
    return registerCoverageReportTask(name) {
        coverageFiles.from(collectTask.flatMap(CoverageCollectTask::outputDir))
        dependsOn(collectTask)
    }
}

internal fun Project.registerCoverageReportTask(
    name: String,
    configure: CoverageReportTask.() -> Unit,
): TaskProvider<CoverageReportTask> {
    return tasks.register<CoverageReportTask>("report${name}Coverage") {
        description = "Gera o relatório de cobertura de testes."
        configure()
    }
}

/**
 * Registra a tarefa de relatório de cobertura configurada diretamente a partir de um [SourceSet].
 */
internal fun Project.registerCoverageReportTask(
    name: String,
    source: NamedDomainObjectProvider<SourceSet>,
    dependsOn: Set<String>,
): TaskProvider<CoverageReportTask> {
    return registerCoverageReportTask(name) {
        configureCoverageInputs(source)
        dependsOn.forEach(this::dependsOn)
    }
}

/**
 * Registra a tarefa de relatório de cobertura configurada diretamente a partir de uma [Variant] do Android.
 */
internal fun Project.registerCoverageReportTask(
    name: String,
    variant: Variant,
    executionDataFiles: FileCollection,
    dependsOn: Set<String>,
): TaskProvider<CoverageReportTask> {
    val reportTask = registerCoverageReportTask(name) {
        configureCoverageInputs(
            variant = variant,
            scopedClassDirectories = scopedClassDirectories,
            scopedClassJars = scopedClassJars,
            archiveOperations = archiveOperations,
            executionDataFiles = executionDataFiles,
        )
        dependsOn.forEach(this::dependsOn)
    }

    variant.bindClassesArtifact(reportTask)

    return reportTask
}
