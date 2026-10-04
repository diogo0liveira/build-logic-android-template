package com.dao.convention.services.coverage

import kotlin.test.Test
import kotlin.test.assertEquals

class PackageCoverageTest {

    @Test
    fun `test aggregation logic with empty classes list`() {
        val packageCoverage = PackageCoverage(
            name = "com.dao.empty",
            classes = emptyList(),
        )

        assertEquals(CoverageCounter.ZERO, packageCoverage.line)
        assertEquals(CoverageCounter.ZERO, packageCoverage.branch)
        assertEquals(CoverageCounter.ZERO, packageCoverage.instruction)
    }

    @Test
    fun `test aggregation logic with a single class`() {
        val class1 = ClassCoverage(
            name = "Class1",
            line = CoverageCounter(missed = 10, covered = 90),
            branch = CoverageCounter(missed = 5, covered = 15),
            instruction = CoverageCounter(missed = 20, covered = 80),
        )

        val packageCoverage = PackageCoverage(
            name = "com.dao.single",
            classes = listOf(class1),
        )

        assertEquals(CoverageCounter(missed = 10, covered = 90), packageCoverage.line)
        assertEquals(CoverageCounter(missed = 5, covered = 15), packageCoverage.branch)
        assertEquals(CoverageCounter(missed = 20, covered = 80), packageCoverage.instruction)
    }

    @Test
    fun `test aggregation logic with multiple classes`() {
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

        val packageCoverage = PackageCoverage(
            name = "com.dao.multiple",
            classes = listOf(class1, class2),
        )

        assertEquals(CoverageCounter(missed = 35, covered = 165), packageCoverage.line)
        assertEquals(CoverageCounter(missed = 15, covered = 25), packageCoverage.branch)
        assertEquals(CoverageCounter(missed = 50, covered = 150), packageCoverage.instruction)
    }
}
