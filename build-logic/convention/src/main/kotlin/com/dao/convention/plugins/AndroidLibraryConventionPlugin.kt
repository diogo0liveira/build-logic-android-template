package com.dao.convention.plugins

import com.android.build.api.dsl.LibraryExtension
import com.dao.convention.androidCompileOptions
import com.dao.convention.configureGradleManagedDevices
import com.dao.convention.dependencies.androidTestImplementation
import com.dao.convention.dependencies.apply
import com.dao.convention.dependencies.implementation
import com.dao.convention.dependencies.libs
import com.dao.convention.dependencies.testImplementation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

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

            dependencies {
                testImplementation(libs.test.junit)
                testImplementation(libs.test.kotlin)
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
