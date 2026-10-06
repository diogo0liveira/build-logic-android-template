package com.dao.convention.services.coverage

import com.dao.convention.services.JacocoReportSummary
import java.io.File
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

class JacocoReportSummaryTest {
    private lateinit var csvFile: File

    @BeforeEach
    fun setup() {
        csvFile = File.createTempFile("jacoco", ".csv")
    }

    @AfterEach
    fun teardown() {
        csvFile.delete()
    }

    @Test
    fun `should return null when file does not exist`() {
        val csvFile = File("non_existent.csv")
        val summary = JacocoReportSummary.parse(csvFile)
        assertNull(summary)
    }

    @Test
    fun `should return null when file is empty`() {
        val csvFile = File.createTempFile("empty", ".csv")
        val summary = JacocoReportSummary.parse(csvFile)
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
        val summary = JacocoReportSummary.parse(csvFile)

        assertNotNull(summary)
        assertEquals("JaCoCo Coverage Report", summary.headerTitle)
        assertEquals(2, summary.packages.size)

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
            JacocoReportSummary.parse(csvFile)
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
            JacocoReportSummary.parse(csvFile)
        }

        assertEquals("Valor inválido em INSTRUCTION_MISSED: 'invalid'", exception.message)
    }

    @Test
    fun `should use custom header title when provided`() {
        val csvContent = """
            GROUP,PACKAGE,CLASS,INSTRUCTION_MISSED,INSTRUCTION_COVERED,BRANCH_MISSED,BRANCH_COVERED,LINE_MISSED,LINE_COVERED
            com.dao,com.dao.convention,MyClass,10,90,5,15,2,8
        """.trimIndent()

        csvFile.writeText(csvContent)

        val summary = JacocoReportSummary.parse(csvFile, headerTitle = "Custom Title")
        assertEquals("Custom Title", summary?.headerTitle)
    }

    private fun List<PackageCoverage>.get(name: String): PackageCoverage {
        return first { coverage -> coverage.name == name }
    }

    private fun List<ClassCoverage>.get(name: String): ClassCoverage {
        return first { coverage -> coverage.name == name }
    }
}
