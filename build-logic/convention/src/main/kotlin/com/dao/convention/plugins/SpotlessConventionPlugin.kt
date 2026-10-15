package com.dao.convention.plugins

import com.dao.convention.configureSpotless
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Plugin de convenção interno para formatação e linting de código com Spotless e KtLint.
 *
 * Aplica formatação consistente para fontes Kotlin e scripts Gradle KTS,
 * consumindo o arquivo `.editorconfig` localizado no diretório raiz do projeto.
 */
internal abstract class SpotlessConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.configureSpotless()
    }
}
