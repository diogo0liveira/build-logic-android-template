package com.dao.convention.plugins

import com.dao.convention.dependencies.apply
import com.dao.convention.dependencies.implementation
import com.dao.convention.dependencies.ksp
import com.dao.convention.dependencies.libs
import com.dao.convention.dependencies.withPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Plugin de convenção para injeção de dependência com Dagger Hilt (`convention.hilt`).
 *
 * Configura o processador de anotações KSP e adapta as dependências conforme o tipo de módulo.
 *
 * Responsabilidades:
 * - Aplica o plugin KSP (`com.google.devtools.ksp`) e injeta `hilt-compiler`.
 * - Em módulos Android: aplica o plugin `dagger.hilt.android` e injeta `hilt-android`.
 * - Em módulos JVM puros: injeta `hilt-core`.
 *
 * Exemplo de uso:
 * ```kotlin
 * plugins {
 *     alias(libs.plugins.convention.android.library)
 *     alias(libs.plugins.convention.hilt)
 * }
 * ```
 */
internal abstract class HiltConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            pluginManager.apply(libs.plugins.ksp)

            dependencies {
                ksp(libs.hilt.compiler)
            }

            pluginManager.withPlugin(libs.plugins.android.base) {
                pluginManager.apply(libs.plugins.hilt.android)
                dependencies {
                    implementation(libs.hilt.android)
                }
            }

            pluginManager.withPlugin(libs.plugins.kotlin.jvm) {
                dependencies {
                    implementation(libs.hilt.core)
                }
            }
        }
    }
}
