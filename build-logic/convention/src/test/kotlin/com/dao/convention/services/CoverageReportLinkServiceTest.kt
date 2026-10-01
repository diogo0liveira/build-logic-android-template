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
    fun `should do nothing when task execution fails`() {
        val event = mockTaskFinishEvent<TaskFailureResult>(":testTask")
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
    fun `should print html link when task succeeds and html file exists`() {
        reportHtmlFile.parentFile.mkdirs()
        reportHtmlFile.createNewFile()

        val event = mockTaskFinishEvent<TaskSuccessResult>(":testTask")
        service.onFinish(event)

        val output = output.toString()
        assertTrue(output.contains("Relatório de Cobertura JaCoCo"))
        assertTrue(output.contains(reportHtmlFile.toURI().toString()))
    }

    @Test
    fun `should print console summary when task succeeds and csv file exists`() {
        reportCsvFile.parentFile.mkdirs()
        reportCsvFile.writeText(CSV_CONTENT)

        val event = mockTaskFinishEvent<TaskSuccessResult>(":testTask")
        service.onFinish(event)

        val output = output.toString()
        assertTrue(output.contains("JaCoCo Coverage Report (:testTask)"))
        assertTrue(output.contains("my-package"))
    }

    @Test
    fun `should print both html link and console summary when task succeeds and both files exist`() {
        reportHtmlFile.parentFile.mkdirs()
        reportHtmlFile.createNewFile()

        reportCsvFile.parentFile.mkdirs()
        reportCsvFile.writeText(CSV_CONTENT)

        val event = mockTaskFinishEvent<TaskSuccessResult>(":testTask")
        service.onFinish(event)

        val output = output.toString()
        assertTrue(output.contains("Relatório de Cobertura JaCoCo"))
        assertTrue(output.contains(reportHtmlFile.toURI().toString()))
        assertTrue(output.contains("JaCoCo Coverage Report (:testTask)"))
        assertTrue(output.contains("my-package"))
    }

    private inline fun <reified R : TaskExecutionResult> mockTaskFinishEvent(taskPath: String): TaskFinishEvent =
        mockk {
            every { descriptor.taskPath } returns taskPath
            every { result } returns mockk<R>()
        }
}
