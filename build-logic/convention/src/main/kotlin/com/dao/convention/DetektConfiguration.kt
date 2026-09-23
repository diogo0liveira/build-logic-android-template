package com.dao.convention

import com.dao.convention.dependencies.detektPlugins
import com.dao.convention.dependencies.libs
import com.dao.convention.dependencies.withPlugin
import dev.detekt.gradle.Detekt
import dev.detekt.gradle.extensions.DetektExtension
import dev.detekt.gradle.plugin.DetektPlugin
import java.io.File
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType

internal fun Project.configureDetekt(
    vararg sourceSets: Any,
    baselineDir: File = projectDir,
) {
    pluginManager.apply(DetektPlugin::class)

    extensions.configure<DetektExtension> {
        config.setFrom(buildLogicDir.resolve("detekt.yml"))
        baseline.set(baselineDir.resolve("baseline.xml"))
        buildUponDefaultConfig.set(true)
        source.setFrom(sourceSets)
        allRules.set(false)
    }

    pluginManager.withPlugin(libs.plugins.kotlin.compose) {
        extensions.configure<DetektExtension> {
            config.from(buildLogicDir.resolve("detekt-compose.yml"))
        }
        dependencies {
            detektPlugins(libs.detekt.compose.rules)
            detektPlugins(libs.detekt.twitter.compose.rules)
        }
    }

    tasks.withType<Detekt>().configureEach {
        jvmTarget.set(JavaVersion.VERSION_21.toString())

        reports {
            html.required.set(false)
            sarif.required.set(false)
            markdown.required.set(true)
            checkstyle.required.set(false)
        }
    }
}
