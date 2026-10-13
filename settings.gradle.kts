@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        includeBuild("build-logic")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("com.android.settings") version "9.4.1"
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

android {
    ndkVersion = "30.0.16248370"

    compileSdk {
        version = release(37) {
            minorApiLevel = 2
        }
    }

    targetSdk = 37
    minSdk = 28
}

rootProject.name = "build-logic-android-template"
include(":app")
include(":module-feature")
include(":module-core")
include(":module-jvm")
