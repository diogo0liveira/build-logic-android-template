package com.dao.convention.tasks.register

import com.dao.convention.extensions.JacocoVerificationExtension
import com.dao.convention.listenerProvider
import com.dao.convention.registerCoverageTask
import com.dao.convention.tasks.registerCoverageCollectTask
import com.dao.convention.tasks.registerCoverageReportTask
import com.dao.convention.tasks.registerCoverageVerifyTask
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.SourceSet
import org.gradle.build.event.BuildEventsListenerRegistry

internal fun Project.registerCoverageTask(
    source: NamedDomainObjectProvider<SourceSet>,
    registry: BuildEventsListenerRegistry,
    verification: JacocoVerificationExtension,
    configuration: NamedDomainObjectProvider<Configuration>,
) {
    registerTask(
        name = "Test",
        source = source,
        registry = registry,
        verification = verification,
        configuration = configuration,
        dependsOn = setOf("test"),
    )
}

private fun Project.registerTask(
    name: String,
    source: NamedDomainObjectProvider<SourceSet>,
    dependsOn: Set<String>,
    registry: BuildEventsListenerRegistry,
    verification: JacocoVerificationExtension,
    configuration: NamedDomainObjectProvider<Configuration>,
) {
    registerCoverageCollectTask(
        name = name,
        source = source,
        dependsOn = dependsOn,
        configuration = configuration,
    )

    val reportTask = registerCoverageReportTask(
        name = name,
        source = source,
        dependsOn = dependsOn,
    )
    val verifyTask = registerCoverageVerifyTask(
        name = name,
        verification = verification,
        source = source,
        dependsOn = dependsOn,
    )

    registerCoverageTask(name) {
        registry.onTaskCompletion(listenerProvider(reportTask, verification))
        dependsOn(reportTask)
        dependsOn(verifyTask)
    }
}
