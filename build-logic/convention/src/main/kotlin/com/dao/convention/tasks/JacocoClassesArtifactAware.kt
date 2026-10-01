package com.dao.convention.tasks

import com.android.build.api.artifact.ScopedArtifact
import com.android.build.api.variant.ScopedArtifacts
import com.android.build.api.variant.Variant
import org.gradle.api.Task
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskProvider

/**
 * Contrato para tarefas que consomem classes compiladas do Android Gradle Plugin (AGP).
 */
internal interface JacocoClassesArtifactAware : Task {
    @get:Optional
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    val scopedClassJars: ListProperty<RegularFile>

    @get:Optional
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    val scopedClassDirectories: ListProperty<Directory>
}

/**
 * Vincula as classes compiladas do projeto (ScopedArtifact.CLASSES) à tarefa consumidora.
 */
internal inline fun <reified T> Variant.bindClassesArtifact(
    taskProvider: TaskProvider<T>,
) where T : Task, T : JacocoClassesArtifactAware {
    artifacts
        .forScope(ScopedArtifacts.Scope.PROJECT)
        .use(taskProvider)
        .toGet(
            ScopedArtifact.CLASSES,
            JacocoClassesArtifactAware::scopedClassJars,
            JacocoClassesArtifactAware::scopedClassDirectories,
        )
}
