package com.dao.convention.tasks

import com.android.build.api.variant.Variant
import com.dao.convention.COVERAGE_OUTPUT
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationPublications
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.register

internal fun Project.registerCoverageCollectTask(
    name: String,
    variant: Variant,
    dependsOn: Set<String>,
    configuration: NamedDomainObjectProvider<Configuration>,
): TaskProvider<CoverageCollectTask> {
    return registerCoverageCollectTask(
        name = name,
        dependsOn = dependsOn,
        configuration = configuration,
    ).apply {
        variant.bindClassesArtifact(this)

        configure {
            variant.sources.kotlin?.all?.let { provider ->
                sourceDirectories.from(provider)
            }
        }
    }
}

internal fun Project.registerCoverageCollectTask(
    name: String,
    dependsOn: Set<String>,
    source: NamedDomainObjectProvider<SourceSet>,
    configuration: NamedDomainObjectProvider<Configuration>,
): TaskProvider<CoverageCollectTask> {
    return registerCoverageCollectTask(
        name = name,
        dependsOn = dependsOn,
        configuration = configuration,
    ).apply {
        configure {
            unitTestExecutionData.from(project.layout.buildDirectory.file("jacoco/test.exec"))
            sourceDirectories.from(source.map { it.allSource.sourceDirectories })
            classFiles.from(source.map { it.output.classesDirs })
        }
    }
}

private fun Project.registerCoverageCollectTask(
    name: String,
    dependsOn: Set<String>,
    configuration: NamedDomainObjectProvider<Configuration>,
): TaskProvider<CoverageCollectTask> {
    return tasks.register<CoverageCollectTask>("collect${name}Coverage") {
        description = "Coleta a cobertura de testes para relatório."
        outputDir.convention(layout.buildDirectory.dir(COVERAGE_OUTPUT))
        dependsOn.forEach(this::dependsOn)
    }.apply {
        configuration.configure {
            outgoing.extractArtifacts(this@apply)
        }
    }
}

private fun ConfigurationPublications.extractArtifacts(collect: TaskProvider<CoverageCollectTask>) {
    artifact(collect.flatMap(CoverageCollectTask::outputDir))
}
