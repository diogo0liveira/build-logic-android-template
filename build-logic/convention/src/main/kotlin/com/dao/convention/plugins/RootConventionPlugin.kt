package com.dao.convention.plugins

import com.dao.convention.BUILD_LOGIC
import com.dao.convention.configureSpotless
import com.dao.convention.extensions.JacocoAggregationExtension
import com.dao.convention.extensions.JacocoVerificationExtension
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
                gradleTargets = listOf("$BUILD_LOGIC/**/*.gradle.kts"),
                kotlinTargets = listOf("$BUILD_LOGIC/**/*.kt"),
            )

            extensions.configure<JacocoVerificationExtension> {
                minInstructionCoverage.set(0.90.toBigDecimal())
                minBranchCoverage.set(0.80.toBigDecimal())
                minLineCoverage.set(0.70.toBigDecimal())
                haltOnFailure.set(true)
            }

            extensions.configure<JacocoAggregationExtension> {
                modules.addAll(
                    ":app",
                    ":module-feature",
                    ":module-core",
                    ":module-jvm",
                )
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
