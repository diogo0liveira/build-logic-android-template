package com.dao.convention.plugins

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.variant.AndroidComponentsExtension
import com.dao.convention.COVERAGE_OUTPUT
import com.dao.convention.GMD_GROUP_NAME
import com.dao.convention.createJacocoConsumableConfiguration
import com.dao.convention.dependencies.id
import com.dao.convention.dependencies.libs
import com.dao.convention.dependencies.version
import com.dao.convention.registerCoverageTask
import com.dao.convention.tasks.CoverageCollectTask
import javax.inject.Inject
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.build.event.BuildEventsListenerRegistry
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.support.uppercaseFirstChar
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

                val enableCoverage = providers.gradleProperty("coverage")
                    .getOrElse("false").toBoolean()

                extensions.configure<CommonExtension> {
                    buildTypes.named(COVERAGE_BUILD_TYPE) {
                        enableAndroidTestCoverage = enableCoverage
                        enableUnitTestCoverage = enableCoverage
                    }
                }

                with(extensions.getByType(AndroidComponentsExtension::class)) {
                    val variantSelector = selector().withBuildType(COVERAGE_BUILD_TYPE)
                    val configuration = createJacocoConsumableConfiguration()

                    onVariants(variantSelector) { variant ->
                        val type = variant.name.uppercaseFirstChar()

                        registerCoverageTask(
                            variant = variant,
                            registry = registry,
                            taskName = "${type}UnitTestCoverage",
                            taskDependsOn = setOf("test${type}UnitTest"),
                            outputTaskDir = "$COVERAGE_OUTPUT${variant.name}/unit",
                        ) {
                            unitTestExecutionData.from(
                                layout.buildDirectory
                                    .files("$OUTPUT_UNIT_TEST${variant.name}UnitTest"),
                            )
                        }

                        registerCoverageTask(
                            variant = variant,
                            registry = registry,
                            taskName = "${type}AndroidTestCoverage",
                            taskDependsOn = setOf("$GMD_GROUP${type}AndroidTest"),
                            outputTaskDir = "$COVERAGE_OUTPUT${variant.name}/android",
                        ) {
                            androidTestExecutionData.from(
                                layout.buildDirectory
                                    .files("$OUTPUT_INST_TEST${variant.name}AndroidTest"),
                            )
                        }

                        registerCoverageTask(
                            variant = variant,
                            registry = registry,
                            taskName = "${type}AggregateCoverage",
                            taskDependsOn = setOf("test${type}UnitTest", "$GMD_GROUP${type}AndroidTest"),
                            outputTaskDir = "$COVERAGE_OUTPUT${variant.name}/Aggregate",
                        ) {
                            unitTestExecutionData.from(
                                layout.buildDirectory
                                    .files("$OUTPUT_UNIT_TEST${variant.name}UnitTest"),
                            )
                            androidTestExecutionData.from(
                                layout.buildDirectory
                                    .files("$OUTPUT_INST_TEST${variant.name}"),
                            )
                        }.also { collectTask ->
                            configuration.configure {
                                outgoing.artifact(
                                    collectTask.flatMap(CoverageCollectTask::outputDir),
                                )
                            }
                        }
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
        private const val GMD_GROUP = "${GMD_GROUP_NAME}Group"
        private const val OUTPUT_INST_TEST = "outputs/managed_device_code_coverage/"
        private const val OUTPUT_UNIT_TEST = "outputs/unit_test_code_coverage/"
    }
}
