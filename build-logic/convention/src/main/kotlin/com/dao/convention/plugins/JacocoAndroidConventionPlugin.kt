package com.dao.convention.plugins

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.variant.AndroidComponentsExtension
import com.dao.convention.GMD_GROUP_NAME
import com.dao.convention.createJacocoConsumableConfiguration
import com.dao.convention.dependencies.id
import com.dao.convention.dependencies.libs
import com.dao.convention.dependencies.version
import com.dao.convention.extensions.createJacocoVerificationExtension
import com.dao.convention.isCoverage
import com.dao.convention.registerCoverageTask
import com.dao.convention.tasks.register.registerCoverageTask
import javax.inject.Inject
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.build.event.BuildEventsListenerRegistry
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.gradle.testing.jacoco.plugins.JacocoPlugin
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension

internal abstract class JacocoAndroidConventionPlugin @Inject constructor(
    private val registry: BuildEventsListenerRegistry,
) : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            pluginManager.apply(JacocoPlugin::class)

            pluginManager.withPlugin(libs.plugins.android.base.id) {
                configure<JacocoPluginExtension> {
                    toolVersion = libs.versions.jacoco.version
                }

                val configuration = createJacocoConsumableConfiguration()
                val verification = createJacocoVerificationExtension()
                val enableCoverage = isCoverage

                extensions.configure<CommonExtension> {
                    buildTypes.named(COVERAGE_BUILD_TYPE) {
                        enableAndroidTestCoverage = enableCoverage
                        enableUnitTestCoverage = enableCoverage
                    }
                }

                with(extensions.getByType(AndroidComponentsExtension::class)) {
                    val variantSelector = selector().withBuildType(COVERAGE_BUILD_TYPE)

                    onVariants(variantSelector) { variant ->
                        registerCoverageTask(
                            variant = variant,
                            registry = registry,
                            verification = verification,
                            configuration = configuration,
                        )
                    }
                }

                tasks.withType<Test>().configureEach {
                    configure<JacocoTaskExtension> {
                        excludes = listOf("jdk.internal.*")
                        isIncludeNoLocationClasses = true
                    }
                }
            }
        }
    }

    private companion object {
        private const val COVERAGE_BUILD_TYPE = "debug"
    }
}
