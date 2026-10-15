package com.dao.convention.plugins

import com.dao.convention.configureJUnit6
import com.dao.convention.configureKotlin
import com.dao.convention.dependencies.apply
import com.dao.convention.dependencies.libs
import com.dao.convention.dependencies.testImplementation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinBaseExtension

/**
 * Plugin de convenção para módulos JVM puros (`convention.kotlin.jvm`).
 *
 * Destinado a módulos de regras de negócio ou utilitários agnósticos ao SDK Android.
 *
 * Responsabilidades:
 * - Aplica o plugin do Kotlin JVM (`org.jetbrains.kotlin.jvm`).
 * - Aplica plugins de qualidade e cobertura JVM: [JacocoJvmConventionPlugin],
 *   [SpotlessConventionPlugin], [DetektConventionPlugin] e [LintConventionPlugin].
 * - Configura jvmToolchain e modo `explicitApi` via [configureKotlin].
 * - Configura JUnit 6 (Jupiter) e injeta bibliotecas base de teste.
 *
 * Exemplo de uso em `module-jvm/build.gradle.kts`:
 * ```kotlin
 * plugins {
 *     alias(libs.plugins.convention.kotlin.jvm)
 * }
 * ```
 */
internal abstract class KotlinJvmConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            pluginManager.apply(libs.plugins.kotlin.jvm)
            pluginManager.apply(JacocoJvmConventionPlugin::class)
            pluginManager.apply(SpotlessConventionPlugin::class)
            pluginManager.apply(DetektConventionPlugin::class)
            pluginManager.apply(LintConventionPlugin::class)

            configureKotlin<KotlinBaseExtension>()
            configureJUnit6()

            dependencies {
                testImplementation(libs.test.junit)
                testImplementation(libs.test.kotlin5)
            }
        }
    }
}
