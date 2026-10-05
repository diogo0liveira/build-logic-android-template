package com.dao.convention.services.coverage

import com.dao.convention.services.JacocoReportSummary
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class JacocoReportSummaryTest {

    @Test
    fun `should return null when file does not exist`() {
        val file = File("non_existent.csv")
        val summary = JacocoReportSummary.parse(file)
        assertNull(summary)
    }

    @Test
    fun `should return null when file is empty`() {
        val file = File.createTempFile("empty", ".csv")
        file.deleteOnExit()

        val summary = JacocoReportSummary.parse(file)
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
        val file = File.createTempFile("jacoco", ".csv")
        file.deleteOnExit()
        file.writeText(csvContent)

        val summary = JacocoReportSummary.parse(file)

        assertEquals("JaCoCo Coverage Report", summary?.headerTitle)
        assertEquals(2, summary?.packages?.size)

        val pkg1 = summary?.packages?.find { it.name == "com.dao.convention" }
        assertEquals(2, pkg1?.classes?.size)

        val myClass = pkg1?.classes?.find { it.name == "MyClass" }
        assertEquals(10L, myClass?.instruction?.missed)
        assertEquals(90L, myClass?.instruction?.covered)
        assertEquals(5L, myClass?.branch?.missed)
        assertEquals(15L, myClass?.branch?.covered)
        assertEquals(2L, myClass?.line?.missed)
        assertEquals(8L, myClass?.line?.covered)

        val pkg2 = summary?.packages?.find { it.name == "com.dao.other" }
        assertEquals(1, pkg2?.classes?.size)
        val otherClass = pkg2?.classes?.find { it.name == "OtherClass" }
        assertEquals(0L, otherClass?.instruction?.missed)
        assertEquals(100L, otherClass?.instruction?.covered)
    }

    @Test
    fun `should throw exception when a required column is missing`() {
        val csvContent = """
            GROUP,PACKAGE,CLASS,INSTRUCTION_MISSED,INSTRUCTION_COVERED
            com.dao,com.dao.convention,MyClass,10,90
        """.trimIndent()
        val file = File.createTempFile("missing_columns", ".csv")
        file.deleteOnExit()
        file.writeText(csvContent)

        assertFailsWith<NoSuchElementException> {
            JacocoReportSummary.parse(file)
        }
    }

    @Test
    fun `should throw IllegalStateException when numeric value is invalid`() {
        val csvContent = """
            GROUP,PACKAGE,CLASS,INSTRUCTION_MISSED,INSTRUCTION_COVERED,BRANCH_MISSED,BRANCH_COVERED,LINE_MISSED,LINE_COVERED
            com.dao,com.dao.convention,MyClass,invalid,90,5,15,2,8
        """.trimIndent()
        val file = File.createTempFile("invalid_numeric", ".csv")
        file.deleteOnExit()
        file.writeText(csvContent)

        val exception = assertFailsWith<IllegalStateException> {
            JacocoReportSummary.parse(file)
        }
        assertEquals("Valor inválido em INSTRUCTION_MISSED: 'invalid'", exception.message)
    }

    @Test
    fun `should use custom header title when provided`() {
        val csvContent = """
            GROUP,PACKAGE,CLASS,INSTRUCTION_MISSED,INSTRUCTION_COVERED,BRANCH_MISSED,BRANCH_COVERED,LINE_MISSED,LINE_COVERED
            com.dao,com.dao.convention,MyClass,10,90,5,15,2,8
        """.trimIndent()
        val file = File.createTempFile("jacoco_custom", ".csv")
        file.deleteOnExit()
        file.writeText(csvContent)

        val summary = JacocoReportSummary.parse(file, headerTitle = "Custom Title")

        assertEquals("Custom Title", summary?.headerTitle)
    }
}
