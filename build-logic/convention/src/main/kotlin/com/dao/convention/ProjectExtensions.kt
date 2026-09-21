@file:Suppress("UnstableApiUsage")

package com.dao.convention

import java.io.File
import org.gradle.api.Project

val Project.buildLogicDir: File
    //  get() = rootProject.rootDir.resolve(BUILD_LOGIC)
    get() = isolated.rootProject.projectDirectory.asFile.resolve(BUILD_LOGIC)

val Project.isRoot: Boolean
    //  get() = (this == rootProject)
    get() = (path == Project.PATH_SEPARATOR)

fun Project.requireIsRoot() {
    require(isRoot) { "Project is not root!" }
}
