package com.dao.convention.tasks

import javax.inject.Inject
import org.gradle.api.DefaultTask
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

private val JACOCO_EXCLUDE_PATTERNS = listOf(
    "**/R.class",
    "**/R$*.class",
    "**/Manifest*.*",
    "**/BuildConfig.*",
    $$"**/*$ViewBinder*.*",
    $$"**/*$ViewInjector*.*",
    "android/**/*.*",
    // Data Binding
    "**/*Binding.*",
    "**/*BindingImpl.*",
    "**/databinding/*",
    "**/DataBinderMapperImpl*",
    // Compose
    "**/ui/theme/**",
    $$"**/*$Lambda$*.*",
    $$"**/*$default*.*",
    "**/*Preview*.*",
    "**/*PreviewKt.class",
    "**/ComposableSingletons$*",
    "**/*ComposableSingletons*.*",
    // Navigation
    "**/*Args*.*",
    "**/*NavGraph*.*",
    "**/*Directions*.*",
    "**/*Destinations*.*",
    $$"**/*Screen$Companion*.*",
    // Kotlin
    $$"**/*$WhenMappings.*",
    "**/*Kt$*.class", // top-level funções lambda internas
    "**/*JsonAdapter.*", // Moshi
    "**/*_Impl.*", // Room DAOs/Database
    $$"**/*$serializer.*", // kotlinx.serialization
    "**/*JsonAdapter.*", // Moshi generated adapters
    // Hilt/Dagger
    "dagger/**",
    "**/*_Factory*",
    "**/*_GeneratedInjector*",
    "**/*_ComponentTreeDeps*",
    "**/*_MembersInjector*",
    "**/*_Provide*Factory*",
    "**/*_Factory.*",
    "**/*Module_*Factory.*",
    "**/*_MembersInjector.*",
    "**/Dagger*Component*.*",
    "**/*_HiltModules*.*",
    "**/*_HiltComponents*.*",
    "hilt_aggregated_deps/**",
    "**/hilt_aggregated_deps/**",
    "**/*_HiltModules*",
    "**/HiltWrapper_*",
    "**/*_Hilt*",
    "**/Hilt_*",
)

@CacheableTask
internal abstract class CoverageCollectTask @Inject constructor(private val fileSystem: FileSystemOperations) :
    DefaultTask() {
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
    abstract val classJars: ListProperty<RegularFile>

    @get:Optional
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val classDirectories: ListProperty<Directory>

    @get:Optional
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val classFiles: ConfigurableFileCollection

    @get:Optional
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
                into(outputDir.dir("exec/unit"))
                    .include("**/*.exec")
            }
        }
        if (!androidTestExecutionData.isEmpty) {
            fileSystem.copy {
                from(androidTestExecutionData)
                into(outputDir.dir("exec/android"))
                    .include("**/*.ec")
            }
        }
        if (classDirectories.orNull?.isNotEmpty() == true) {
            fileSystem.copy {
                from(classDirectories)
                into(outputDir.dir("classes"))
                    .exclude(JACOCO_EXCLUDE_PATTERNS)
            }
        }
        if (!classFiles.isEmpty) {
            fileSystem.copy {
                from(classFiles)
                into(outputDir.dir("classes"))
                    .exclude(JACOCO_EXCLUDE_PATTERNS)
            }
        }
        if (!sourceDirectories.isEmpty) {
            fileSystem.copy {
                from(sourceDirectories)
                into(outputDir.dir("sources"))
                    .exclude(JACOCO_EXCLUDE_PATTERNS)
            }
        }
    }
}
