package com.dao.convention.tasks

import com.dao.convention.COVERAGE_DIR_CLASSES
import com.dao.convention.COVERAGE_DIR_EXEC_ANDROID
import com.dao.convention.COVERAGE_DIR_EXEC_UNIT
import com.dao.convention.COVERAGE_DIR_SOURCES
import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.file.ArchiveOperations
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

@CacheableTask
internal abstract class CoverageCollectTask @Inject constructor(
    private val archiveOperations: ArchiveOperations,
    private val fileSystem: FileSystemOperations,
) : DefaultTask(), JacocoClassesArtifactAware {

    @get:Optional
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val unitTestExecutionData: ConfigurableFileCollection

    @get:Optional
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val androidTestExecutionData: ConfigurableFileCollection

    @get:Optional
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract override val scopedClassJars: ListProperty<RegularFile>

    @get:Optional
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract override val scopedClassDirectories: ListProperty<Directory>

    @get:Optional
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val classFiles: ConfigurableFileCollection

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val sourceDirectories: ConfigurableFileCollection

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun collect() {
        fileSystem.delete {
            delete(outputDir)
        }

        if (!unitTestExecutionData.isEmpty) {
            fileSystem.copy {
                from(unitTestExecutionData)
                into(outputDir.dir(COVERAGE_DIR_EXEC_UNIT))
                    .include("**/*.exec")
            }
        }
        if (!androidTestExecutionData.isEmpty) {
            fileSystem.copy {
                from(androidTestExecutionData)
                into(outputDir.dir(COVERAGE_DIR_EXEC_ANDROID))
                    .include("**/*.ec")
            }
        }
        if (scopedClassDirectories.isPresent) {
            fileSystem.copy {
                from(scopedClassDirectories)
                into(outputDir.dir(COVERAGE_DIR_CLASSES))
                    .exclude(JACOCO_EXCLUDE_PATTERNS)
            }
        }
        if (scopedClassJars.isPresent && scopedClassJars.get().isNotEmpty()) {
            scopedClassJars.get().forEach { jar ->
                fileSystem.copy {
                    from(archiveOperations.zipTree(jar))
                    into(outputDir.dir(COVERAGE_DIR_CLASSES))
                    exclude(JACOCO_EXCLUDE_PATTERNS)
                }
            }
        }

        if (!classFiles.isEmpty) {
            fileSystem.copy {
                from(classFiles)
                into(outputDir.dir(COVERAGE_DIR_CLASSES))
                    .exclude(JACOCO_EXCLUDE_PATTERNS)
            }
        }
        if (!sourceDirectories.isEmpty) {
            fileSystem.copy {
                from(sourceDirectories)
                into(outputDir.dir(COVERAGE_DIR_SOURCES))
                    .exclude(JACOCO_EXCLUDE_PATTERNS)
            }
        }
    }
}
