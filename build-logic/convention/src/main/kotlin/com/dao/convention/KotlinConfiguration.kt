package com.dao.convention

import com.dao.convention.dependencies.libs
import com.dao.convention.dependencies.version
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinBaseExtension

/**
 * Configuração global do Kotlin compartilhada entre módulos Android e JVM
 */
internal inline fun <reified T : KotlinBaseExtension> Project.configureKotlin() {
    configure<T> {
        jvmToolchain(libs.versions.jvm.target.version.toInt())
        explicitApi()
    }
}
