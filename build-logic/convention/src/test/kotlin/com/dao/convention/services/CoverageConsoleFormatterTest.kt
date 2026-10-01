package com.dao.convention.services

import com.dao.convention.services.coverage.ClassCoverage
import com.dao.convention.services.coverage.CoverageCounter
import com.dao.convention.services.coverage.CoverageSummary
import com.dao.convention.services.coverage.CoverageThresholds
import com.dao.convention.services.coverage.PackageCoverage
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CoverageConsoleFormatterTest {
    private val defaultThresholds = CoverageThresholds(
        minInstruction = 0.90,
        minBranch = 0.80,
        minLine = 0.70,
        warningTolerance = 0.15,
    )

    @Test
    fun `should format coverage summary into aligned console table with header total and html report link`() {
        val class1 = ClassCoverage(
            name = "GetGreeting",
            line = CoverageCounter(missed = 0, covered = 3),
            branch = CoverageCounter(missed = 0, covered = 0),
            instruction = CoverageCounter(missed = 0, covered = 10),
        )
        val class2 = ClassCoverage(
            name = "SetGreeting",
            line = CoverageCounter(missed = 1, covered = 2),
            branch = CoverageCounter(missed = 0, covered = 0),
            instruction = CoverageCounter(missed = 1, covered = 5),
        )

        val pkg1 = PackageCoverage(
            name = "com.dao.android.module.core",
            classes = listOf(class1, class2),
        )

        val summary = CoverageSummary(
            header = "📊 JaCoCo Coverage Report (:coverageTestReport)",
            packages = listOf(pkg1),
            thresholds = defaultThresholds,
            htmlReport = File("/home/diogo/project/build/reports/jacoco/html/index.html"),
        )

        val formatted = CoverageConsoleFormatter.format(summary)
        val lines = formatted.lines()

        assertTrue(lines[0].startsWith("==="))
        assertEquals("📊 JaCoCo Coverage Report (:coverageTestReport)", lines[1])
        assertEquals(lines[0], lines[2])
        assertTrue(lines[3].startsWith("Package/Class"))
        assertTrue(lines[4].startsWith("---"))
        assertTrue(lines[5].contains("com.dao.android.module.core"))
        assertTrue(lines[6].contains("  ├── GetGreeting"))
        assertTrue(lines[7].contains("  └── SetGreeting"))
        assertTrue(lines[8].startsWith("---"))
        assertTrue(lines[9].startsWith("Total"))
        assertTrue(lines[9].contains("83.3% (5/6)"))
        assertTrue(lines[9].contains("n/a"))
        assertTrue(lines[9].contains("93.8% (15/16)"))
        assertEquals(lines[0], lines[10])
        assertEquals("👉 Relatório HTML: ${summary.htmlReportUrl}", lines[11])
        assertEquals(lines[0], lines[12])
    }

    @Test
    fun `should format total row with na when counters have zero total`() {
        val summary = CoverageSummary(
            header = "📊 JaCoCo Coverage Report (:emptyTask)",
            packages = emptyList(),
            thresholds = defaultThresholds,
            htmlReport = File("/reports/index.html"),
        )

        val formatted = CoverageConsoleFormatter.format(summary)
        val lines = formatted.lines()

        val totalLine = lines.first { line -> line.startsWith("Total") }
        assertTrue(totalLine.contains("n/a"))
    }

    @Test
    fun `should use default package label when package name is empty`() {
        val classCoverage = ClassCoverage(
            name = "RootClass",
            line = CoverageCounter(missed = 1, covered = 1),
            branch = CoverageCounter(missed = 0, covered = 0),
            instruction = CoverageCounter(missed = 2, covered = 2),
        )

        val pkg = PackageCoverage(
            name = "",
            classes = listOf(classCoverage),
        )

        val summary = CoverageSummary(
            header = "📊 JaCoCo Coverage Report (:rootTask)",
            packages = listOf(pkg),
            thresholds = defaultThresholds,
            htmlReport = File("/reports/index.html"),
        )

        val formatted = CoverageConsoleFormatter.format(summary)
        assertTrue(formatted.contains("(default)"))
        assertTrue(formatted.contains("  └── RootClass"))
    }

    @Test
    fun `should expand table width when footer html link is longer than columns width`() {
        val longHtmlPath = "/a/very/long/nested/directory/structure/reports/jacoco/task/html/index.html"
        val summary = CoverageSummary(
            header = "📊 JaCoCo Coverage Report (:task)",
            packages = emptyList(),
            thresholds = defaultThresholds,
            htmlReport = File(longHtmlPath),
        )

        val formatted = CoverageConsoleFormatter.format(summary)
        val lines = formatted.lines()
        val equalsLine = lines[0]
        val footerLine = lines[lines.size - 2]

        assertEquals(footerLine.length, equalsLine.length)
    }

    @Test
    fun `should colorize cells with ansi codes based on dynamic thresholds`() {
        val classCoverage = ClassCoverage(
            name = "TestClass",
            line = CoverageCounter(missed = 5, covered = 95),
            branch = CoverageCounter(missed = 20, covered = 80),
            instruction = CoverageCounter(missed = 50, covered = 50),
        )

        val pkg = PackageCoverage(name = "com.dao.sample", classes = listOf(classCoverage))
        val summary = CoverageSummary(
            header = "📊 JaCoCo Coverage Report (:coloredTask)",
            packages = listOf(pkg),
            htmlReport = File("/reports/index.html"),
            thresholds = CoverageThresholds(
                minInstruction = 0.90,
                minBranch = 0.90,
                minLine = 0.90,
                warningTolerance = 0.15,
            ),
        )

        val formatted = CoverageConsoleFormatter.format(summary, enableColors = true)
        val lines = formatted.lines()
        val classLine = lines.first { it.contains("TestClass") }

        // Green ANSI code (\u001B[32m) for 95%
        assertTrue(classLine.contains("\u001B[32m"))
        // Yellow ANSI code (\u001B[33m) for 80%
        assertTrue(classLine.contains("\u001B[33m"))
        // Red ANSI code (\u001B[31m) for 50%
        assertTrue(classLine.contains("\u001B[31m"))
    }

    @Test
    fun `should format plain cells without ansi codes when colors are disabled`() {
        val classCoverage = ClassCoverage(
            name = "TestClass",
            line = CoverageCounter(missed = 0, covered = 100),
            branch = CoverageCounter(missed = 0, covered = 0),
            instruction = CoverageCounter(missed = 50, covered = 50),
        )

        val pkg = PackageCoverage(name = "com.dao.sample", classes = listOf(classCoverage))
        val summary = CoverageSummary(
            header = "📊 JaCoCo Coverage Report (:plainTask)",
            packages = listOf(pkg),
            thresholds = defaultThresholds,
            htmlReport = File("/reports/index.html"),
        )

        val formatted = CoverageConsoleFormatter.format(summary, enableColors = false)
        assertTrue(!formatted.contains("\u001B["))
    }
}
