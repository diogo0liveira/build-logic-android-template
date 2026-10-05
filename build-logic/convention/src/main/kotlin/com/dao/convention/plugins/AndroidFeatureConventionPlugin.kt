package com.dao.convention.plugins

import com.dao.convention.dependencies.apply
import com.dao.convention.dependencies.implementation
import com.dao.convention.dependencies.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

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
