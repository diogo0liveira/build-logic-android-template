package com.dao.convention.plugins

import com.dao.convention.configureKotlin
import com.dao.convention.dependencies.apply
import com.dao.convention.dependencies.libs
import com.dao.convention.dependencies.testImplementation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinBaseExtension

internal abstract class KotlinJvmConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            pluginManager.apply(libs.plugins.kotlin.jvm)
            pluginManager.apply(JacocoJvmConventionPlugin::class)
            pluginManager.apply(SpotlessConventionPlugin::class)
            pluginManager.apply(DetektConventionPlugin::class)
            pluginManager.apply(LintConventionPlugin::class)

            configureKotlin<KotlinBaseExtension>()

            dependencies {
                testImplementation(libs.test.kotlin)
            }
        }
    }
}
