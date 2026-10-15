package com.dao.convention.plugins

import com.android.build.api.dsl.ApplicationExtension
import com.dao.convention.androidCompileOptions
import com.dao.convention.configureGradleManagedDevices
import com.dao.convention.configureJUnit6
import com.dao.convention.dependencies.apply
import com.dao.convention.dependencies.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

/**
 * Plugin de convenção para módulos de aplicação Android (`convention.android.application`).
 *
 * Aplica e padroniza as configurações essenciais para módulos executáveis (`:app`).
 *
 * Responsabilidades:
 * - Aplica o plugin oficial do Android Application (`com.android.application`).
 * - Aplica plugins internos de qualidade e governança: [JacocoAndroidConventionPlugin],
 *   [SpotlessConventionPlugin], [DetektConventionPlugin] e [LintConventionPlugin].
 * - Configura opções de compilação padrão do Android ([androidCompileOptions]), Java toolchain e explicit API.
 * - Configura dispositivos gerenciados pelo Gradle ([configureGradleManagedDevices] - GMD).
 * - Habilita JUnit 6 (Jupiter) para testes unitários locais ([configureJUnit6]).
 *
 * Exemplo de uso no `app/build.gradle.kts`:
 * ```kotlin
 * plugins {
 *     alias(libs.plugins.convention.android.application)
 * }
 * ```
 */
internal abstract class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            pluginManager.apply(libs.plugins.android.application)
            pluginManager.apply(JacocoAndroidConventionPlugin::class)
            pluginManager.apply(SpotlessConventionPlugin::class)
            pluginManager.apply(DetektConventionPlugin::class)
            pluginManager.apply(LintConventionPlugin::class)

            extensions.configure<ApplicationExtension> {
                androidCompileOptions(this)
                configureGradleManagedDevices(this)
            }

            configureJUnit6()
        }
    }
}
