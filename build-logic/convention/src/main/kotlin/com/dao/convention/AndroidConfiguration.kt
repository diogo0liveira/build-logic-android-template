package com.dao.convention

import com.android.build.api.dsl.CommonExtension
import com.dao.convention.dependencies.libs
import com.dao.convention.dependencies.version
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidExtension

internal fun Project.androidCompileOptions(commonExtension: CommonExtension) {
    commonExtension.apply {
        defaultConfig.apply {
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }

        compileOptions.apply {
            sourceCompatibility(libs.versions.jvm.target.version)
            targetCompatibility(libs.versions.jvm.target.version)
        }
    }

    configureKotlin<KotlinAndroidExtension>()
}
