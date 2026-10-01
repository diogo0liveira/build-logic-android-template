package com.dao.convention.services

import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.tooling.events.FinishEvent
import org.gradle.tooling.events.task.TaskExecutionResult
import org.gradle.tooling.events.task.TaskFailureResult
import org.gradle.tooling.events.task.TaskFinishEvent
import org.gradle.tooling.events.task.TaskSuccessResult
import org.junit.After
import org.junit.Before

class CoverageReportLinkServiceTest {
    private lateinit var reportHtmlFile: File
    private lateinit var reportCsvFile: File
    private lateinit var service: CoverageReportLinkService
    private val output = ByteArrayOutputStream()
    private val standardOut = System.out

    @Before
    fun setup() {
        output.reset()
        System.setOut(PrintStream(output))
        val project = ProjectBuilder.builder().build()

        reportHtmlFile = File(
            project.layout.buildDirectory.get().asFile,
            "reports/jacoco/jacocoTestReport/html/index.html",
        )

        reportCsvFile = File(
            project.layout.buildDirectory.get().asFile,
            "reports/jacoco/jacocoTestReport/jacocoTestReport.csv",
        )

        val serviceProvider = project.gradle.sharedServices.registerIfAbsent(
            "testService",
            CoverageReportLinkService::class.java,
        ) {
            parameters.task.set(":testTask")
            parameters.reportHtml.set(reportHtmlFile)
            parameters.reportCsv.set(reportCsvFile)
            parameters.minInstructionCoverage.set(0.90.toBigDecimal())
            parameters.minBranchCoverage.set(0.80.toBigDecimal())
            parameters.minLineCoverage.set(0.70.toBigDecimal())
            parameters.warningTolerance.set(0.15.toBigDecimal())
        }

        service = serviceProvider.get()
    }

    @After
    fun teardown() {
        clearAllMocks()
        System.setOut(standardOut)
        reportHtmlFile.delete()
        reportCsvFile.delete()
    }

    @Test
    fun `should do nothing when event is not task finish event`() {
        val event = mockk<FinishEvent>()
        service.onFinish(event)
        assertEquals("", output.toString())
    }

    @Test
    fun `should do nothing when task path does not match`() {
        val event = mockTaskFinishEvent<TaskSuccessResult>(":wrongTask")
        service.onFinish(event)
        assertEquals("", output.toString())
    }

    @Test
    fun `should do nothing when report files do not exist`() {
        val event = mockTaskFinishEvent<TaskSuccessResult>(":testTask")
        service.onFinish(event)
        assertEquals("", output.toString())
    }

    @Test
    fun `should do nothing when csv file does not exist`() {
        reportHtmlFile.parentFile.mkdirs()
        reportHtmlFile.createNewFile()

        val event = mockTaskFinishEvent<TaskSuccessResult>(":testTask")
        service.onFinish(event)
        assertEquals("", output.toString())
    }

    @Test
    fun `should do nothing when html file does not exist`() {
        val csvContent = """
            GROUP,PACKAGE,CLASS,INSTRUCTION_MISSED,INSTRUCTION_COVERED,BRANCH_MISSED,BRANCH_COVERED,LINE_MISSED,LINE_COVERED,COMPLEXITY_MISSED,COMPLEXITY_COVERED,METHOD_MISSED,METHOD_COVERED
            "my-group,my-package,MyClass,0,10,0,2,0,5,0,1,0,1"
        """.trimIndent()

        reportCsvFile.parentFile.mkdirs()
        reportCsvFile.writeText(csvContent)

        val event = mockTaskFinishEvent<TaskSuccessResult>(":testTask")
        service.onFinish(event)
        assertEquals("", output.toString())
    }

    @Test
    fun `should print coverage report when task succeeds and both files exist`() {
        val csvContent = """
            GROUP,PACKAGE,CLASS,INSTRUCTION_MISSED,INSTRUCTION_COVERED,BRANCH_MISSED,BRANCH_COVERED,LINE_MISSED,LINE_COVERED,COMPLEXITY_MISSED,COMPLEXITY_COVERED,METHOD_MISSED,METHOD_COVERED
            "my-group,my-package,MyClass,0,10,0,2,0,5,0,1,0,1"
        """.trimIndent()

        reportHtmlFile.parentFile.mkdirs()
        reportHtmlFile.createNewFile()

        reportCsvFile.parentFile.mkdirs()
        reportCsvFile.writeText(csvContent)

        val event = mockTaskFinishEvent<TaskSuccessResult>(":testTask")
        service.onFinish(event)

        val out = output.toString()
        assertTrue(out.contains("📊 JaCoCo Coverage Report (:testTask)"))
        assertTrue(out.contains("my-package"))
        assertTrue(out.contains("Total"))
        assertTrue(out.contains("👉 Relatório HTML:"))
        assertTrue(out.contains(reportHtmlFile.toURI().toString()))
    }

    @Test
    fun `should print coverage report when task fails and both files exist`() {
        val csvContent = """
            GROUP,PACKAGE,CLASS,INSTRUCTION_MISSED,INSTRUCTION_COVERED,BRANCH_MISSED,BRANCH_COVERED,LINE_MISSED,LINE_COVERED,COMPLEXITY_MISSED,COMPLEXITY_COVERED,METHOD_MISSED,METHOD_COVERED
            "my-group,my-package,MyClass,0,10,0,2,0,5,0,1,0,1"
        """.trimIndent()

        reportHtmlFile.parentFile.mkdirs()
        reportHtmlFile.createNewFile()

        reportCsvFile.parentFile.mkdirs()
        reportCsvFile.writeText(csvContent)

        val event = mockTaskFinishEvent<TaskFailureResult>(":testTask")
        service.onFinish(event)

        val out = output.toString()
        assertTrue(out.contains("📊 JaCoCo Coverage Report (:testTask)"))
        assertTrue(out.contains("my-package"))
        assertTrue(out.contains("Total"))
        assertTrue(out.contains("👉 Relatório HTML:"))
        assertTrue(out.contains(reportHtmlFile.toURI().toString()))
    }

    private inline fun <reified R : TaskExecutionResult> mockTaskFinishEvent(taskPath: String): TaskFinishEvent =
        mockk {
            every { descriptor.taskPath } returns taskPath
            every { result } returns mockk<R>()
        }
}
