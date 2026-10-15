package com.dao.convention.plugins

import com.dao.convention.dependencies.apply
import com.dao.convention.dependencies.implementation
import com.dao.convention.dependencies.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Plugin de convenção para módulos de funcionalidade do app (`convention.android.feature`).
 *
 * Combina as convenções de biblioteca Android com o ecossistema de UI Compose.
 *
 * Responsabilidades:
 * - Aplica [AndroidLibraryConventionPlugin] (`convention.android.library`).
 * - Aplica [AndroidComposeConventionPlugin] (`convention.android.compose`).
 * - Injeta dependências fundamentais de arquitetura e ciclo de vida do Compose
 *   (`lifecycle-runtime-compose` e `lifecycle-viewmodel-compose`).
 *
 * Exemplo de uso em `module-feature/build.gradle.kts`:
 * ```kotlin
 * plugins {
 *     alias(libs.plugins.convention.android.feature)
 * }
 * ```
 */
internal abstract class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            pluginManager.apply(libs.plugins.convention.android.library)
            pluginManager.apply(libs.plugins.convention.android.compose)

            dependencies {
                implementation(libs.compose.lifecycle.runtime)
                implementation(libs.compose.lifecycle.viewmodel)
            }
        }
    }
}
