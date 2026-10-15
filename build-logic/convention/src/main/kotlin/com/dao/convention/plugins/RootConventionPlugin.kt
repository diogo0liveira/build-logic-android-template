package com.dao.convention.plugins

import com.dao.convention.BUILD_LOGIC
import com.dao.convention.configureSpotless
import com.dao.convention.extensions.JacocoVerificationExtension
import com.dao.convention.requireIsRoot
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

/**
 * Plugin de convenção raiz do projeto (`convention.root`).
 *
 * Deve ser aplicado exclusivamente no `build.gradle.kts` da raiz do projeto.
 *
 * Responsabilidades:
 * - Garante que o projeto é a raiz do build multi-módulo ([requireIsRoot]).
 * - Aplica o [JacocoAggregationConventionPlugin] para orquestrar a agregação de relatórios de cobertura.
 * - Configura o Spotless ([configureSpotless]) para scripts de build Gradle (`*.gradle.kts`) e fontes do build-logic.
 * - Configura as metas globais de verificação de cobertura através de [JacocoVerificationExtension].
 * - Cria pontes para as tarefas de verificação do included build `:build-logic` (`test` e `check`).
 *
 * Exemplo de uso no `build.gradle.kts` raiz:
 * ```kotlin
 * plugins {
 *     alias(libs.plugins.convention.root)
 * }
 *
 * jacocoAggregation {
 *     modules.addAll(
 *         ":app",
 *         ":module-feature",
 *         ":module-core",
 *         ":module-jvm",
 *     )
 * }
 * ```
 */
internal abstract class RootConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.requireIsRoot()

        with(project) {
            pluginManager.apply(JacocoAggregationConventionPlugin::class)

            configureSpotless(
                gradleTargets = listOf("$BUILD_LOGIC/**/*.gradle.kts"),
                kotlinTargets = listOf("$BUILD_LOGIC/**/*.kt"),
            )

            extensions.configure<JacocoVerificationExtension> {
                minInstructionCoverage.set(0.90.toBigDecimal())
                minBranchCoverage.set(0.80.toBigDecimal())
                minLineCoverage.set(0.70.toBigDecimal())
                haltOnFailure.set(true)
            }

            with(gradle.includedBuild(BUILD_LOGIC)) {
                tasks.register("test") {
                    description = "Executa a suíte de testes do build-logic."
                    group = "verification"
                    dependsOn(task(":convention:test"))
                }

                tasks.named("check").configure {
                    dependsOn(task(":convention:check"))
                }
            }
        }
    }
}
