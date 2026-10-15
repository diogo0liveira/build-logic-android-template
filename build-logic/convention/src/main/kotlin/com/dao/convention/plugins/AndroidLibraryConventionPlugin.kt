package com.dao.convention.plugins

import com.android.build.api.dsl.LibraryExtension
import com.dao.convention.androidCompileOptions
import com.dao.convention.configureGradleManagedDevices
import com.dao.convention.configureJUnit6
import com.dao.convention.dependencies.androidTestImplementation
import com.dao.convention.dependencies.apply
import com.dao.convention.dependencies.libs
import com.dao.convention.dependencies.testImplementation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

/**
 * Plugin de convenção para bibliotecas Android reutilizáveis (`convention.android.library`).
 *
 * Responsabilidades:
 * - Aplica o plugin oficial do Android Library (`com.android.library`).
 * - Aplica plugins de qualidade e cobertura: [JacocoAndroidConventionPlugin],
 *   [SpotlessConventionPlugin], [DetektConventionPlugin] e [LintConventionPlugin].
 * - Define automaticamente o `resourcePrefix` baseado no caminho do módulo no projeto.
 * - Configura opções de compilação Android, GMD e JUnit 6.
 * - Injeta as dependências base de teste unitário e instrumentado (JUnit 4, Kotlin test, Android Test runners).
 *
 * Exemplo de uso em `module-core/build.gradle.kts`:
 * ```kotlin
 * plugins {
 *     alias(libs.plugins.convention.android.library)
 * }
 * ```
 */
internal abstract class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            pluginManager.apply(libs.plugins.android.library)
            pluginManager.apply(JacocoAndroidConventionPlugin::class)
            pluginManager.apply(SpotlessConventionPlugin::class)
            pluginManager.apply(DetektConventionPlugin::class)
            pluginManager.apply(LintConventionPlugin::class)

            extensions.configure<LibraryExtension> {
                resourcePrefix = resourcePrefixBuilder(path)
                androidCompileOptions(this)
                configureGradleManagedDevices(this)
            }

            configureJUnit6()

            dependencies {
                testImplementation(libs.test.junit)
                testImplementation(libs.test.kotlin5)
                androidTestImplementation(libs.test.android.junit)
                androidTestImplementation(libs.test.android.rules)
                androidTestImplementation(libs.test.android.runner)
                androidTestImplementation(libs.test.kotlin)
            }
        }
    }

    private fun resourcePrefixBuilder(path: String): String {
        return path.split("""\W""".toRegex()).drop(1)
            .distinct().joinToString(separator = "_")
            .lowercase() + "_"
    }
}
