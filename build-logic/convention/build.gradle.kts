import org.gradle.initialization.DependenciesAccessors
import org.gradle.kotlin.dsl.support.serviceOf

group = "com.dao.android.buildlogic"

plugins {
    `kotlin-dsl`
    alias(libs.plugins.android.lint)
}

kotlin {
    jvmToolchain(libs.versions.jvm.target.get().toInt())
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

dependencies {
    compileOnly(files(gradle.serviceOf<DependenciesAccessors>().classes.asFiles))
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.detekt.gradle.plugin)
    compileOnly(libs.spotless.gradle.plugin)
    lintChecks(libs.android.gradle.lint)
}

gradlePlugin {
    plugins {
        register("root") {
            id = libs.plugins.convention.root.get().pluginId
            implementationClass = "com.dao.convention.plugins.RootConventionPlugin"
            group = "Project Convention"
        }

        register("application") {
            id = libs.plugins.convention.android.application.get().pluginId
            implementationClass = "com.dao.convention.plugins.AndroidApplicationConventionPlugin"
            group = "Project Convention"
        }

        register("library") {
            id = libs.plugins.convention.android.library.get().pluginId
            implementationClass = "com.dao.convention.plugins.AndroidLibraryConventionPlugin"
            group = "Project Convention"
        }

        register("library-feature") {
            id = libs.plugins.convention.android.feature.get().pluginId
            implementationClass = "com.dao.convention.plugins.AndroidFeatureConventionPlugin"
            group = "Project Convention"
        }

        register("jvm") {
            id = libs.plugins.convention.kotlin.jvm.get().pluginId
            implementationClass = "com.dao.convention.plugins.KotlinJvmConventionPlugin"
            group = "Project Convention"
        }

        register("compose") {
            id = libs.plugins.convention.android.compose.get().pluginId
            implementationClass = "com.dao.convention.plugins.AndroidComposeConventionPlugin"
            group = "Project Convention"
        }

        register("hilt") {
            id = libs.plugins.convention.hilt.get().pluginId
            implementationClass = "com.dao.convention.plugins.HiltConventionPlugin"
            group = "Project Convention"
        }
    }
}
