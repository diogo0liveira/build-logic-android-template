package com.dao.convention.plugins

import com.dao.convention.ANDROID_SOURCE_SETS
import com.dao.convention.JVM_SOURCE_SETS
import com.dao.convention.configureDetekt
import com.dao.convention.dependencies.libs
import com.dao.convention.dependencies.withPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

internal abstract class DetektConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            pluginManager.withPlugin(libs.plugins.android.base) {
                configureDetekt(*ANDROID_SOURCE_SETS)
            }
            pluginManager.withPlugin(libs.plugins.kotlin.jvm) {
                configureDetekt(*JVM_SOURCE_SETS)
            }
        }
    }
}
