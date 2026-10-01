package com.dao.convention.tasks

import com.android.build.api.variant.Variant
import com.dao.convention.extensions.JacocoVerificationExtension
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.register

internal fun Project.registerCoverageVerifyTask(
    name: String,
    verification: JacocoVerificationExtension,
    collectTask: TaskProvider<CoverageCollectTask>,
): TaskProvider<CoverageVerifyTask> {
    return registerCoverageVerifyTask(name, verification) {
        coverageFiles.from(collectTask.flatMap(CoverageCollectTask::outputDir))
    }
}

/**
 * Registra a tarefa de verificação de cobertura configurada diretamente a partir de um [SourceSet].
 */
internal fun Project.registerCoverageVerifyTask(
    name: String,
    verification: JacocoVerificationExtension,
    source: NamedDomainObjectProvider<SourceSet>,
    dependsOn: Set<String> = emptySet(),
): TaskProvider<CoverageVerifyTask> {
    return registerCoverageVerifyTask(name, verification) {
        configureCoverageInputs(source)
        dependsOn.forEach(this::dependsOn)
    }
}

/**
 * Registra a tarefa de verificação de cobertura configurada diretamente a partir de uma [Variant] do Android.
 */
internal fun Project.registerCoverageVerifyTask(
    name: String,
    variant: Variant,
    verification: JacocoVerificationExtension,
    executionDataFiles: FileCollection,
    dependsOn: Set<String> = emptySet(),
): TaskProvider<CoverageVerifyTask> {
    val verifyTask = registerCoverageVerifyTask(name, verification) {
        configureCoverageInputs(
            variant = variant,
            scopedClassDirectories = scopedClassDirectories,
            scopedClassJars = scopedClassJars,
            archiveOperations = archiveOperations,
            executionDataFiles = executionDataFiles,
        )
        dependsOn.forEach(this::dependsOn)
    }

    variant.bindClassesArtifact(verifyTask)

    return verifyTask
}

internal fun Project.registerCoverageVerifyTask(
    name: String,
    verification: JacocoVerificationExtension,
    configure: CoverageVerifyTask.() -> Unit,
): TaskProvider<CoverageVerifyTask> {
    return tasks.register<CoverageVerifyTask>("verify${name}Coverage") {
        description = "Verifica se a cobertura de testes atinge as metas mínimas."
        minInstructionCoverage.set(verification.minInstructionCoverage)
        minBranchCoverage.set(verification.minBranchCoverage)
        minLineCoverage.set(verification.minLineCoverage)
        failOnViolation.set(verification.haltOnFailure)
        warningTolerance.set(verification.warningTolerance)
        configure()
    }
}
