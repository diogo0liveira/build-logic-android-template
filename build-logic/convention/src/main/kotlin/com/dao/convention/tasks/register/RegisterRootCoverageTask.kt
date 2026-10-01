package com.dao.convention.tasks.register

import com.dao.convention.artifactFiles
import com.dao.convention.extensions.JacocoVerificationExtension
import com.dao.convention.listenerProvider
import com.dao.convention.registerCoverageTask
import com.dao.convention.tasks.registerCoverageReportTask
import com.dao.convention.tasks.registerCoverageVerifyTask
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.build.event.BuildEventsListenerRegistry

internal fun Project.registerCoverageTask(
    registry: BuildEventsListenerRegistry,
    verification: JacocoVerificationExtension,
    configuration: NamedDomainObjectProvider<Configuration>,
) {
    registerTask(
        name = "Test",
        registry = registry,
        verification = verification,
        configuration = configuration,
    )
}

private fun Project.registerTask(
    name: String,
    registry: BuildEventsListenerRegistry,
    verification: JacocoVerificationExtension,
    configuration: NamedDomainObjectProvider<Configuration>,
) {
    val reportTask = registerCoverageReportTask(name) {
        coverageFiles.from(configuration.artifactFiles)
    }

    val verifyTask = registerCoverageVerifyTask(name, verification) {
        coverageFiles.from(configuration.artifactFiles)
    }

    registerCoverageTask(name) {
        registry.onTaskCompletion(listenerProvider(reportTask, verification))
        dependsOn(reportTask)
        dependsOn(verifyTask)
    }
}
