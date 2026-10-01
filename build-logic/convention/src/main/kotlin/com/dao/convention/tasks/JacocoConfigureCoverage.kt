package com.dao.convention.tasks

import com.android.build.api.variant.Variant
import com.dao.convention.COVERAGE_DIR_CLASSES
import com.dao.convention.COVERAGE_DIR_SOURCES
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.file.ArchiveOperations
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.Directory
import org.gradle.api.file.FileCollection
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.SourceSet
import org.gradle.testing.jacoco.tasks.JacocoReportBase

/**
 * Vincula as coleções de arquivos de cobertura (classes, fontes e dados de execução)
 * às propriedades de entrada correspondentes de uma tarefa [JacocoReportBase].
 */
internal fun JacocoReportBase.configureCoverageInputs(coverageFiles: ConfigurableFileCollection) {
    sourceDirectories.from(
        coverageFiles.elements.map { list ->
            list.map { it.asFile.resolve(COVERAGE_DIR_SOURCES) }
        },
    )
    classDirectories.from(
        coverageFiles.elements.map { list ->
            list.map { it.asFile.resolve(COVERAGE_DIR_CLASSES) }
        },
    )
    executionData.from(
        coverageFiles.asFileTree.matching {
            include("**/*.exec", "**/*.ec")
        },
    )
}

/**
 * Vincula as saídas de compilação, fontes e dados de execução de um [SourceSet]
 * às propriedades de entrada correspondentes de uma tarefa [JacocoReportBase] sem cópia física de arquivos.
 */
internal fun JacocoReportBase.configureCoverageInputs(source: NamedDomainObjectProvider<SourceSet>) {
    executionData.from(project.layout.buildDirectory.file("jacoco/test.exec"))
    sourceDirectories.from(source.map { it.allSource.sourceDirectories })
    classDirectories.from(
        source.map { src ->
            src.output.classesDirs.asFileTree.matching {
                exclude(JACOCO_EXCLUDE_PATTERNS)
            }
        },
    )
}

/**
 * Vincula as saídas de compilação, fontes e dados de execução de uma [Variant] do Android
 * às propriedades de entrada correspondentes de uma tarefa [JacocoReportBase] sem cópia física de arquivos.
 */
internal fun JacocoReportBase.configureCoverageInputs(
    variant: Variant,
    scopedClassDirectories: ListProperty<Directory>,
    scopedClassJars: ListProperty<RegularFile>,
    archiveOperations: ArchiveOperations,
    executionDataFiles: FileCollection,
) {
    executionData.from(
        executionDataFiles.asFileTree.matching {
            include("**/*.exec", "**/*.ec")
        },
    )
    variant.sources.kotlin?.all?.let { provider ->
        sourceDirectories.from(provider)
    }
    classDirectories.from(
        scopedClassDirectories.map { dirs ->
            dirs.map { dir ->
                dir.asFileTree.matching {
                    exclude(JACOCO_EXCLUDE_PATTERNS)
                }
            }
        },
        scopedClassJars.map { jars ->
            jars.map { jar ->
                archiveOperations.zipTree(jar).matching {
                    exclude(JACOCO_EXCLUDE_PATTERNS)
                }
            }
        },
    )
}
