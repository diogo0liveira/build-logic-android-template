package com.dao.convention.tasks.register

import com.android.build.api.variant.Variant
import com.dao.convention.GMD_GROUP_NAME
import com.dao.convention.extensions.JacocoVerificationExtension
import com.dao.convention.listenerProvider
import com.dao.convention.registerCoverageTask
import com.dao.convention.tasks.CoverageCollectTask
import com.dao.convention.tasks.registerCoverageCollectTask
import com.dao.convention.tasks.registerCoverageReportTask
import com.dao.convention.tasks.registerCoverageVerifyTask
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.file.FileCollection
import org.gradle.build.event.BuildEventsListenerRegistry
import org.gradle.kotlin.dsl.support.uppercaseFirstChar

private const val GMD_GROUP = "${GMD_GROUP_NAME}Group"
private const val OUTPUT_UNIT_TEST = "outputs/unit_test_code_coverage/"
private const val OUTPUT_INST_TEST = "outputs/managed_device_code_coverage/"

internal fun Project.registerCoverageTask(
    variant: Variant,
    registry: BuildEventsListenerRegistry,
    verification: JacocoVerificationExtension,
    configuration: NamedDomainObjectProvider<Configuration>,
) {
    val type = variant.name.uppercaseFirstChar()
    val buildDirectory = layout.buildDirectory
    val unitTestDataFiles = buildDirectory.files("$OUTPUT_UNIT_TEST${variant.name}UnitTest")
    val instTestDataFiles = layout.buildDirectory.files("$OUTPUT_INST_TEST${variant.name}")

    registerReportTask(
        name = "UnitTest",
        variant = variant,
        registry = registry,
        verification = verification,
        executionDataFiles = unitTestDataFiles,
        dependsOn = setOf("test${type}UnitTest"),
    )

    registerReportTask(
        name = "AndroidTest",
        variant = variant,
        registry = registry,
        verification = verification,
        executionDataFiles = instTestDataFiles,
        dependsOn = setOf("$GMD_GROUP${type}AndroidTest"),
    )

    registerAggregateTask(
        name = "Test",
        variant = variant,
        registry = registry,
        verification = verification,
        configuration = configuration,
        executionDataFiles = layout.buildDirectory.files(unitTestDataFiles, instTestDataFiles),
        dependsOn = setOf("test${type}UnitTest", "$GMD_GROUP${type}AndroidTest"),
    ) {
        buildDirectory.files("$OUTPUT_UNIT_TEST${variant.name}UnitTest")
            .apply(unitTestExecutionData::setFrom)

        buildDirectory.files("$OUTPUT_INST_TEST${variant.name}")
            .apply(androidTestExecutionData::setFrom)
    }
}

private fun Project.registerReportTask(
    name: String,
    variant: Variant,
    executionDataFiles: FileCollection,
    dependsOn: Set<String>,
    registry: BuildEventsListenerRegistry,
    verification: JacocoVerificationExtension,
) {
    val reportTask = registerCoverageReportTask(
        name = name,
        variant = variant,
        executionDataFiles = executionDataFiles,
        dependsOn = dependsOn,
    )

    registerCoverageTask(name) {
        registry.onTaskCompletion(listenerProvider(reportTask, verification))
        dependsOn(reportTask)
    }
}

private fun Project.registerAggregateTask(
    name: String,
    variant: Variant,
    executionDataFiles: FileCollection,
    dependsOn: Set<String>,
    registry: BuildEventsListenerRegistry,
    verification: JacocoVerificationExtension,
    configuration: NamedDomainObjectProvider<Configuration>,
    action: CoverageCollectTask.() -> Unit,
) {
    val reportTask = registerCoverageReportTask(
        name = name,
        variant = variant,
        executionDataFiles = executionDataFiles,
        dependsOn = dependsOn,
    )
    val verifyTask = registerCoverageVerifyTask(
        name = name,
        variant = variant,
        verification = verification,
        executionDataFiles = executionDataFiles,
        dependsOn = dependsOn,
    )

    registerCoverageTask(name) {
        registry.onTaskCompletion(listenerProvider(reportTask, verification))
        dependsOn(reportTask)
        dependsOn(verifyTask)
    }

    registerCoverageCollectTask(
        name = name,
        variant = variant,
        dependsOn = dependsOn,
        configuration = configuration,
    ).configure { action() }
}
