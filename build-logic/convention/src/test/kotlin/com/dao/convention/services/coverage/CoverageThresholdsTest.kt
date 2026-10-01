package com.dao.convention.services.coverage

import kotlin.test.Test
import kotlin.test.assertEquals

class CoverageThresholdsTest {
    private val defaultThresholds = CoverageThresholds(
        minInstruction = 0.90,
        minBranch = 0.80,
        minLine = 0.70,
        warningTolerance = 0.15,
    )

    @Test
    fun `should return passed status when percentage meets or exceeds target`() {
        val thresholds = defaultThresholds
        val counter = CoverageCounter(missed = 5, covered = 95) // 95%

        assertEquals(CoverageStatus.PASSED, thresholds.statusForInstruction(counter))
    }

    @Test
    fun `should return warning status when percentage is within warning tolerance`() {
        val thresholds = defaultThresholds
        val counter = CoverageCounter(missed = 20, covered = 80) // 80% (between 75% and 90%)

        assertEquals(CoverageStatus.WARNING, thresholds.statusForInstruction(counter))
    }

    @Test
    fun `should return failed status when percentage is below warning limit`() {
        val thresholds = defaultThresholds
        val counter = CoverageCounter(missed = 40, covered = 60) // 60% (below 75%)

        assertEquals(CoverageStatus.FAILED, thresholds.statusForInstruction(counter))
    }

    @Test
    fun `should return none status when counter has zero total`() {
        val thresholds = defaultThresholds
        val counter = CoverageCounter.ZERO

        assertEquals(CoverageStatus.NONE, thresholds.statusForBranch(counter))
    }

    @Test
    fun `should evaluate line and branch with respective specific thresholds`() {
        val thresholds = CoverageThresholds(
            minInstruction = 0.90,
            minBranch = 0.70,
            minLine = 0.85,
            warningTolerance = 0.10,
        )

        val branchCounter = CoverageCounter(missed = 25, covered = 75) // 75% >= 70% -> PASSED
        val lineCounter = CoverageCounter(missed = 20, covered = 80) // 80% (target 85%, warning 75%) -> WARNING

        assertEquals(CoverageStatus.PASSED, thresholds.statusForBranch(branchCounter))
        assertEquals(CoverageStatus.WARNING, thresholds.statusForLine(lineCounter))
    }
}
