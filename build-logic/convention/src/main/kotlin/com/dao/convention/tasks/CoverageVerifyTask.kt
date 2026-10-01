package com.dao.convention.tasks

import java.math.BigDecimal
import javax.inject.Inject
import org.gradle.api.file.ArchiveOperations
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification

@CacheableTask
internal abstract class CoverageVerifyTask @Inject constructor(
    @get:Internal val archiveOperations: ArchiveOperations,
) : JacocoCoverageVerification(), JacocoClassesArtifactAware {

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

    @get:Input
    abstract val minInstructionCoverage: Property<BigDecimal>

    @get:Input
    abstract val minBranchCoverage: Property<BigDecimal>

    @get:Input
    abstract val minLineCoverage: Property<BigDecimal>

    @get:Input
    abstract val failOnViolation: Property<Boolean>

    @get:Input
    abstract val warningTolerance: Property<BigDecimal>

    init {
        minInstructionCoverage.convention(BigDecimal.ZERO)
        minBranchCoverage.convention(BigDecimal.ZERO)
        minLineCoverage.convention(BigDecimal.ZERO)
        warningTolerance.convention(BigDecimal.ZERO)
        failOnViolation.convention(false)

        configureCoverageInputs(coverageFiles)
    }

    override fun check() {
        violationRules {
            isFailOnViolation = failOnViolation.get()
            rule {
                element = "PACKAGE"

                limit {
                    counter = "INSTRUCTION"
                    value = "COVEREDRATIO"
                    minimum = minInstructionCoverage.get()
                }

                limit {
                    counter = "BRANCH"
                    value = "COVEREDRATIO"
                    minimum = minBranchCoverage.get()
                }

                limit {
                    counter = "LINE"
                    value = "COVEREDRATIO"
                    minimum = minLineCoverage.get()
                }
            }
        }

        super.check()
    }
}
