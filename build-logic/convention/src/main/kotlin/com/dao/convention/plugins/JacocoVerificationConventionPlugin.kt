package com.dao.convention.plugins

import com.dao.convention.COVERAGE_TASK_GROUP
import com.dao.convention.artifactFiles
import com.dao.convention.createJacocoResolvableConfiguration
import com.dao.convention.extensions.JacocoVerificationExtension
import com.dao.convention.isRoot
import com.dao.convention.tasks.CoverageVerificationTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register

internal abstract class JacocoVerificationConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            val extension = extensions.create<JacocoVerificationExtension>(JACOCO_VERIFICATION_EXTENSION)
            val configuration = createJacocoResolvableConfiguration()

            val verificationTask = tasks.register<CoverageVerificationTask>(JACOCO_VERIFICATION_TASK) {
                group = COVERAGE_TASK_GROUP
                description = "Verifica se a cobertura de testes atinge as metas mínimas."
                minInstructionCoverage.convention(extension.minInstructionCoverage)
                minBranchCoverage.convention(extension.minBranchCoverage)
                minLineCoverage.convention(extension.minLineCoverage)
                failOnViolation.convention(extension.haltOnFailure)
                coverageFiles.from(configuration.artifactFiles)
            }

            if (!isRoot) {
                // No projeto raiz, a coverageDataConsumer é compartilhada com o
                // JacocoAggregationConventionPlugin, que já declara os módulos nela.
                // Em subprojetos, declaramos o próprio projeto como fonte de dados de cobertura.
                configuration.configure {
                    dependencies.add(
                        project.dependencies.project(mapOf("path" to project.path)),
                    )
                }
            }

            // Integra a verificação ao ciclo de vida padrão do Gradle nos subprojetos.
            // Tasks.matching{} é lazy e seguro para tasks registradas após a aplicação
            // deste plugin, como a task 'check' criada pelo AGP.
            tasks.matching { it.name == "check" }.configureEach {
                dependsOn(verificationTask)
            }
        }
    }

    private companion object {
        private const val JACOCO_VERIFICATION_EXTENSION = "jacocoVerification"
        private const val JACOCO_VERIFICATION_TASK = "jacocoCoverageVerification"
    }
}
