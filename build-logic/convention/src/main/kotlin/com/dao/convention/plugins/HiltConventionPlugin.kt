package com.dao.convention.plugins

import com.dao.convention.dependencies.apply
import com.dao.convention.dependencies.implementation
import com.dao.convention.dependencies.ksp
import com.dao.convention.dependencies.libs
import com.dao.convention.dependencies.withPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

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
