package com.dao.convention.tasks

import javax.inject.Inject
import org.gradle.api.file.ArchiveOperations
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.Directory
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.testing.jacoco.tasks.JacocoReport

@CacheableTask
internal abstract class CoverageReportTask @Inject constructor(
    layout: ProjectLayout,
    @get:Internal val archiveOperations: ArchiveOperations,
) : JacocoReport(), JacocoClassesArtifactAware {

    @get:Optional
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val coverageFiles: ConfigurableFileCollection

    @get:Optional
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract override val scopedClassJars: ListProperty<RegularFile>

    @get:Optional
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract override val scopedClassDirectories: ListProperty<Directory>

    init {
        reports.csv.required.set(true)
        reports.xml.required.set(true)
        reports.html.required.set(true)

        reports.csv.outputLocation.convention(
            layout.buildDirectory.file("reports/jacoco/$name/$name.csv"),
        )
        reports.xml.outputLocation.convention(
            layout.buildDirectory.file("reports/jacoco/$name/$name.xml"),
        )
        reports.html.outputLocation.convention(
            layout.buildDirectory.dir("reports/jacoco/$name/html"),
        )

        configureCoverageInputs(coverageFiles)
    }
}
