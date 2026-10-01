package com.dao.convention.services.coverage

import com.dao.convention.services.CoverageConsoleSummary
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.junit.After
import org.junit.Before

class CoverageConsoleSummaryTest {
    private val defaultThresholds = CoverageThresholds(
        minInstruction = 0.90,
        minBranch = 0.80,
        minLine = 0.70,
        warningTolerance = 0.15,
    )
    private lateinit var csvFile: File
    private lateinit var htmlFile: File

    @Before
    fun setup() {
        csvFile = File.createTempFile("jacoco", ".csv")
        htmlFile = File.createTempFile("index", ".html")
    }

    @After
    fun teardown() {
        csvFile.delete()
        htmlFile.delete()
    }

    @Test
    fun `should return null when file does not exist`() {
        val nonExistentCsv = File("non_existent.csv")
        val summary = CoverageConsoleSummary.parse(
            task = ":testTask",
            csvReport = nonExistentCsv,
            htmlReport = htmlFile,
            thresholds = defaultThresholds,
        )
        assertNull(summary)
    }

    @Test
    fun `should return null when file is empty`() {
        val summary = CoverageConsoleSummary.parse(
            task = ":testTask",
            csvReport = csvFile,
            htmlReport = htmlFile,
            thresholds = defaultThresholds,
        )
        assertNull(summary)
    }

    @Test
    fun `should correctly parse a valid JaCoCo CSV file`() {
        val csvContent = """
            GROUP,PACKAGE,CLASS,INSTRUCTION_MISSED,INSTRUCTION_COVERED,BRANCH_MISSED,BRANCH_COVERED,LINE_MISSED,LINE_COVERED,COMPLEXITY_MISSED,COMPLEXITY_COVERED,METHOD_MISSED,METHOD_COVERED
            com.dao,com.dao.convention,MyClass,10,90,5,15,2,8,1,2,0,1
            com.dao,com.dao.convention,AnotherClass,20,80,10,10,4,6,2,2,0,2
            com.dao,com.dao.other,OtherClass,0,100,0,20,0,10,0,5,0,5
        """.trimIndent()

        csvFile.writeText(csvContent)
        val summary = CoverageConsoleSummary.parse(
            task = ":testTask",
            csvReport = csvFile,
            htmlReport = htmlFile,
            thresholds = defaultThresholds,
        )

        assertNotNull(summary)
        assertEquals("📊 JaCoCo Coverage Report (:testTask)", summary.header)
        assertEquals(2, summary.packages.size)
        assertTrue(summary.htmlReportUrl.startsWith("file:/"))

        val pkg1 = summary.packages.get("com.dao.convention")
        assertEquals(2, pkg1.classes.size)

        val class1 = pkg1.classes.get("MyClass")
        assertEquals(10L, class1.instruction.missed)
        assertEquals(90L, class1.instruction.covered)
        assertEquals(5L, class1.branch.missed)
        assertEquals(15L, class1.branch.covered)
        assertEquals(2L, class1.line.missed)
        assertEquals(8L, class1.line.covered)

        val pkg2 = summary.packages.get("com.dao.other")
        assertEquals(1, pkg2.classes.size)

        val class2 = pkg2.classes.get("OtherClass")
        assertEquals(0L, class2.instruction.missed)
        assertEquals(100L, class2.instruction.covered)
    }

    @Test
    fun `should throw exception when a required column is missing`() {
        val csvContent = """
            GROUP,PACKAGE,CLASS,INSTRUCTION_MISSED,INSTRUCTION_COVERED
            com.dao,com.dao.convention,MyClass,10,90
        """.trimIndent()

        csvFile.writeText(csvContent)

        assertFailsWith<NoSuchElementException> {
            CoverageConsoleSummary.parse(
                task = ":testTask",
                csvReport = csvFile,
                htmlReport = htmlFile,
                thresholds = defaultThresholds,
            )
        }
    }

    @Test
    fun `should throw IllegalStateException when numeric value is invalid`() {
        val csvContent = """
            GROUP,PACKAGE,CLASS,INSTRUCTION_MISSED,INSTRUCTION_COVERED,BRANCH_MISSED,BRANCH_COVERED,LINE_MISSED,LINE_COVERED
            com.dao,com.dao.convention,MyClass,invalid,90,5,15,2,8
        """.trimIndent()

        csvFile.writeText(csvContent)

        val exception = assertFailsWith<IllegalStateException> {
            CoverageConsoleSummary.parse(
                task = ":testTask",
                csvReport = csvFile,
                htmlReport = htmlFile,
                thresholds = defaultThresholds,
            )
        }

        assertEquals("Valor inválido em INSTRUCTION_MISSED: 'invalid'", exception.message)
    }

    private fun List<PackageCoverage>.get(name: String): PackageCoverage {
        return first { coverage -> coverage.name == name }
    }

    private fun List<ClassCoverage>.get(name: String): ClassCoverage {
        return first { coverage -> coverage.name == name }
    }
}
