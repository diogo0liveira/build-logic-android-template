package com.dao.convention

import com.dao.convention.dependencies.libs
import com.dao.convention.dependencies.version
import com.dao.convention.dependencies.withPlugin
import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.gradle.spotless.SpotlessPlugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

internal fun Project.configureSpotless(
    gradleTargets: String? = null,
    kotlinTargets: String? = null,
) {
    pluginManager.apply(SpotlessPlugin::class)

    extensions.configure<SpotlessExtension> {
        kotlinGradle {
            target(listOfNotNull("*.gradle.kts", gradleTargets))
            ktlint(libs.versions.ktlint.version)
                .setEditorConfigPath("$rootDir/.editorconfig")
        }

        kotlin {
            target(listOfNotNull("src/*/kotlin/**/*.kt", kotlinTargets))
            val config = ktlint(libs.versions.ktlint.version)
                .setEditorConfigPath("$rootDir/.editorconfig")

            pluginManager.withPlugin(libs.plugins.kotlin.compose) {
                config.customRuleSets(listOf(libs.ktlint.compose.rules.get().toString()))
            }
        }
    }
}
