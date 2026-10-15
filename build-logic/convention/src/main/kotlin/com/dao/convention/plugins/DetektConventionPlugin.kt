package com.dao.convention.plugins

import com.dao.convention.ANDROID_SOURCE_SETS
import com.dao.convention.JVM_SOURCE_SETS
import com.dao.convention.configureDetekt
import com.dao.convention.dependencies.libs
import com.dao.convention.dependencies.withPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Plugin de convenção interno para análise estática de código com Detekt.
 *
 * Configura regras customizadas em `build-logic/detekt.yml` e `build-logic/detekt-compose.yml`,
 * gerando relatórios em Markdown e vinculando os source sets adequados para Android e JVM.
 */
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
