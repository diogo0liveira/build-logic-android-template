package com.dao.convention.services.coverage

import kotlin.test.Test
import kotlin.test.assertEquals

class PackageCoverageTest {
    @Test
    fun `should return zero coverage when classes list is empty`() {
        val coverage = PackageCoverage(
            name = "com.dao.empty",
            classes = emptyList(),
        )

        assertEquals(CoverageCounter.ZERO, coverage.line)
        assertEquals(CoverageCounter.ZERO, coverage.branch)
        assertEquals(CoverageCounter.ZERO, coverage.instruction)
    }

    @Test
    fun `should aggregate coverage when single class is provided`() {
        val class1 = ClassCoverage(
            name = "Class1",
            line = CoverageCounter(missed = 10, covered = 90),
            branch = CoverageCounter(missed = 5, covered = 15),
            instruction = CoverageCounter(missed = 20, covered = 80),
        )

        val coverage = PackageCoverage(
            name = "com.dao.single",
            classes = listOf(class1),
        )

        assertEquals(CoverageCounter(missed = 10, covered = 90), coverage.line)
        assertEquals(CoverageCounter(missed = 5, covered = 15), coverage.branch)
        assertEquals(CoverageCounter(missed = 20, covered = 80), coverage.instruction)
    }

    @Test
    fun `should aggregate coverage when multiple classes are provided`() {
        val class1 = ClassCoverage(
            name = "Class1",
            line = CoverageCounter(missed = 10, covered = 90),
            branch = CoverageCounter(missed = 5, covered = 15),
            instruction = CoverageCounter(missed = 20, covered = 80),
        )

        val class2 = ClassCoverage(
            name = "Class2",
            line = CoverageCounter(missed = 25, covered = 75),
            branch = CoverageCounter(missed = 10, covered = 10),
            instruction = CoverageCounter(missed = 30, covered = 70),
        )

        val coverage = PackageCoverage(
            name = "com.dao.multiple",
            classes = listOf(class1, class2),
        )

        assertEquals(CoverageCounter(missed = 35, covered = 165), coverage.line)
        assertEquals(CoverageCounter(missed = 15, covered = 25), coverage.branch)
        assertEquals(CoverageCounter(missed = 50, covered = 150), coverage.instruction)
    }
}
