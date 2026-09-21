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
        reports.html.required.set(true)
        reports.xml.required.set(false)
        reports.csv.required.set(false)

        reports.html.outputLocation.convention(
            project.layout.buildDirectory.dir("reports/jacoco/$name/html"),
        )

        executionData.from(
            coverageFiles.asFileTree.matching {
                include("**/*.exec", "**/*.ec")
            },
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
    }
}
