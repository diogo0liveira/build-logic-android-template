package com.dao.convention.plugins

import com.android.build.api.dsl.CommonExtension
import com.dao.convention.dependencies.androidTestImplementation
import com.dao.convention.dependencies.apply
import com.dao.convention.dependencies.debugImplementation
import com.dao.convention.dependencies.implementation
import com.dao.convention.dependencies.libs
import com.dao.convention.dependencies.lintChecks
import com.dao.convention.dependencies.testImplementation
import com.dao.convention.dependencies.withPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

/**
 * Plugin de convenção para habilitação e padronização do Jetpack Compose (`convention.android.compose`).
 *
 * Deve ser aplicado em conjunto com um plugin base Android (Application ou Library).
 *
 * Responsabilidades:
 * - Aplica o plugin do compilador Kotlin Compose (`org.jetbrains.kotlin.plugin.compose`).
 * - Habilita `buildFeatures.compose = true` na extensão comum do Android.
 * - Gerencia dependências de UI via BOM do Compose (`compose-bom`), Material3, UI Tooling e Activity Compose.
 * - Adiciona regras de verificação estática do Compose via Slack Lint checks.
 *
 * Exemplo de uso:
 * ```kotlin
 * plugins {
 *     alias(libs.plugins.convention.android.library)
 *     alias(libs.plugins.convention.android.compose)
 * }
 * ```
 */
internal abstract class AndroidComposeConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            pluginManager.withPlugin(libs.plugins.android.base) {
                pluginManager.apply(libs.plugins.kotlin.compose)

                extensions.configure<CommonExtension> {
                    buildFeatures.compose = true
                }

                dependencies {
                    val bom = platform(libs.compose.bom)

                    implementation(bom)
                    implementation(libs.compose.activity)
                    implementation(libs.compose.material3)
                    implementation(libs.compose.ui)
                    implementation(libs.compose.ui.graphics)
                    implementation(libs.compose.ui.tooling.preview)

                    testImplementation(bom)
                    testImplementation(libs.test.compose.ui.junit4)

                    androidTestImplementation(bom)
                    androidTestImplementation(libs.test.compose.ui.junit4)

                    debugImplementation(bom)
                    debugImplementation(libs.bundles.compose.ui.tooling)
                    lintChecks(libs.slack.lint.compose)
                }
            }
        }
    }
}
