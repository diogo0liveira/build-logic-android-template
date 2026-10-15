package com.dao.convention.plugins

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.dsl.Lint
import com.dao.convention.configureLint
import com.dao.convention.dependencies.apply
import com.dao.convention.dependencies.libs
import com.dao.convention.dependencies.withPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * Plugin de convenção interno para execução unificada do Android Lint.
 *
 * Parametriza a verificação estática de severidades, baselines e checagem de fontes de teste
 * em módulos Android Application, Android Library e módulos JVM puros.
 */
internal abstract class LintConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            pluginManager.withPlugin(libs.plugins.android.application) {
                configure<ApplicationExtension> {
                    lint {
                        configureLint()
                    }
                }
            }

            pluginManager.withPlugin(libs.plugins.android.library) {
                configure<LibraryExtension> {
                    lint {
                        configureLint()
                    }
                }
            }

            pluginManager.withPlugin(libs.plugins.kotlin.jvm) {
                pluginManager.apply(libs.plugins.android.lint)
                configure<Lint> {
                    configureLint()
                }
            }
        }
    }
}
