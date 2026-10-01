package com.dao.convention.tasks

import java.math.BigDecimal
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification

@CacheableTask
internal abstract class CoverageVerificationTask : JacocoCoverageVerification() {
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val coverageFiles: ConfigurableFileCollection

    @get:Input
    abstract val minInstructionCoverage: Property<BigDecimal>

    @get:Input
    abstract val minBranchCoverage: Property<BigDecimal>

    @get:Input
    abstract val minLineCoverage: Property<BigDecimal>

    @get:Input
    abstract val failOnViolation: Property<Boolean>

    init {
        minInstructionCoverage.convention(BigDecimal.ZERO)
        minBranchCoverage.convention(BigDecimal.ZERO)
        minLineCoverage.convention(BigDecimal.ZERO)
        failOnViolation.convention(false)

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
