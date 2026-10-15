@file:Suppress("UnstableApiUsage")

package com.dao.convention

import java.io.File
import org.gradle.api.Project
import org.gradle.api.file.Directory

/**
 * Diretório raiz do projeto isolado, compatível com Gradle Project Isolation.
 */
val Project.projectDirectory: Directory
    get() = isolated.rootProject.projectDirectory

/**
 * Diretório do módulo build-logic a partir da raiz do projeto.
 */
val Project.buildLogicDir: File
    //  get() = rootProject.rootDir.resolve(BUILD_LOGIC)
    get() = isolated.rootProject.projectDirectory.asFile.resolve(BUILD_LOGIC)

/**
 * Indica se o projeto atual é o projeto raiz da compilação.
 */
val Project.isRoot: Boolean
    //  get() = (this == rootProject)
    get() = (path == Project.PATH_SEPARATOR)

/**
 * Indica se a cobertura de código foi explicitamente solicitada via propriedade Gradle ('-Pcoverage').
 */
val Project.isCoverage: Boolean
    get() = providers.gradleProperty("coverage")
        .getOrElse("false").toBoolean()

/**
 * Valida se o projeto atual é o projeto raiz, lançando exceção caso contrário.
 */
fun Project.requireIsRoot() {
    require(isRoot) { "Project is not root!" }
}
