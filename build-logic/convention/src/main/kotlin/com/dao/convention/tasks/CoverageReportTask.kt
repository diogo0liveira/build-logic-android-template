package com.dao.convention.tasks

import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.testing.jacoco.tasks.JacocoReport

@CacheableTask
internal abstract class CoverageReportTask : JacocoReport() {
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val coverageFiles: ConfigurableFileCollection

    init {
        reports.csv.required.set(true)
        reports.html.required.set(true)
        reports.xml.required.set(false)

        reports.csv.outputLocation.convention(
            project.layout.buildDirectory.file("reports/jacoco/$name/$name.csv"),
        )
        reports.html.outputLocation.convention(
            project.layout.buildDirectory.dir("reports/jacoco/$name/html"),
        )

        classDirectories.from(
            coverageFiles.elements.map { list ->
                list.map { it.asFile.resolve("classes") }
            },
        )
        sourceDirectories.from(
            coverageFiles.elements.map { list ->
                list.map { it.asFile.resolve("sources") }
            },
        )
        executionData.from(
            coverageFiles.asFileTree.matching {
                include("**/*.exec", "**/*.ec")
            },
        )
    }
}
