package com.dao.convention.plugins

import com.android.build.api.dsl.ApplicationExtension
import com.dao.convention.androidCompileOptions
import com.dao.convention.configureGradleManagedDevices
import com.dao.convention.dependencies.apply
import com.dao.convention.dependencies.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

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
        }
    }
}
