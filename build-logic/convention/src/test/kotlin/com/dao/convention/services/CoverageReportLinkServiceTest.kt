package com.dao.convention.services

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream
import java.lang.reflect.Proxy
import kotlin.test.assertTrue
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.tooling.events.FinishEvent
import org.gradle.tooling.events.task.TaskFailureResult
import org.gradle.tooling.events.task.TaskFinishEvent
import org.gradle.tooling.events.task.TaskOperationDescriptor
import org.gradle.tooling.events.task.TaskSuccessResult
import org.junit.After
import org.junit.Before
import org.junit.Test

class CoverageReportLinkServiceTest {

    private lateinit var service: CoverageReportLinkService
    private lateinit var reportHtmlFile: File
    private lateinit var reportCsvFile: File

    private val standardOut = System.out
    private val outputStreamCaptor = ByteArrayOutputStream()

    @Before
    fun setup() {
        System.setOut(PrintStream(outputStreamCaptor))

        val project = ProjectBuilder.builder().build()

        reportHtmlFile = File(
            project.layout.buildDirectory.get().asFile,
            "reports/jacoco/jacocoTestReport/html/index.html",
        )
        reportCsvFile =
            File(project.layout.buildDirectory.get().asFile, "reports/jacoco/jacocoTestReport/jacocoTestReport.csv")

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
        System.setOut(standardOut)
    }

    @Test
    fun testEventIsNotTaskFinishEvent() {
        val event = mockFinishEvent()
        service.onFinish(event)
        assertTrue(outputStreamCaptor.toString().isEmpty())
    }

    @Test
    fun testEventIsTaskFinishEventButWrongTaskPath() {
        val event = mockTaskFinishEvent(":wrongTask", isSuccess = true)
        service.onFinish(event)
        assertTrue(outputStreamCaptor.toString().isEmpty())
    }

    @Test
    fun testEventIsTaskFinishEventRightTaskPathButNotSuccess() {
        val event = mockTaskFinishEvent(":testTask", isSuccess = false)
        service.onFinish(event)
        assertTrue(outputStreamCaptor.toString().isEmpty())
    }

    @Test
    fun testSuccessButFilesDoNotExist() {
        val event = mockTaskFinishEvent(":testTask", isSuccess = true)
        service.onFinish(event)
        assertTrue(outputStreamCaptor.toString().isEmpty())
    }

    @Test
    fun testSuccessAndHtmlFileExists() {
        reportHtmlFile.parentFile.mkdirs()
        reportHtmlFile.createNewFile()

        val event = mockTaskFinishEvent(":testTask", isSuccess = true)
        service.onFinish(event)

        val output = outputStreamCaptor.toString()
        assertTrue(output.contains("Relatório de Cobertura JaCoCo"))
        assertTrue(output.contains(reportHtmlFile.toURI().toString()))
    }

    @Test
    fun testSuccessAndCsvFileExists() {
        reportCsvFile.parentFile.mkdirs()
        reportCsvFile.writeText(
            "GROUP,PACKAGE,CLASS,INSTRUCTION_MISSED,INSTRUCTION_COVERED,BRANCH_MISSED,BRANCH_COVERED,LINE_MISSED,LINE_COVERED,COMPLEXITY_MISSED,COMPLEXITY_COVERED,METHOD_MISSED,METHOD_COVERED\n",
        )
        reportCsvFile.appendText("mygroup,mypackage,MyClass,0,10,0,2,0,5,0,1,0,1\n")

        val event = mockTaskFinishEvent(":testTask", isSuccess = true)
        service.onFinish(event)

        val output = outputStreamCaptor.toString()
        assertTrue(output.contains("JaCoCo Coverage Report (:testTask)"))
        assertTrue(output.contains("mypackage"))
    }

    @Test
    fun testSuccessAndBothFilesExist() {
        reportHtmlFile.parentFile.mkdirs()
        reportHtmlFile.createNewFile()

        reportCsvFile.parentFile.mkdirs()
        reportCsvFile.writeText(
            "GROUP,PACKAGE,CLASS,INSTRUCTION_MISSED,INSTRUCTION_COVERED,BRANCH_MISSED,BRANCH_COVERED,LINE_MISSED,LINE_COVERED,COMPLEXITY_MISSED,COMPLEXITY_COVERED,METHOD_MISSED,METHOD_COVERED\n",
        )
        reportCsvFile.appendText("mygroup,mypackage,MyClass,0,10,0,2,0,5,0,1,0,1\n")

        val event = mockTaskFinishEvent(":testTask", isSuccess = true)
        service.onFinish(event)

        val output = outputStreamCaptor.toString()
        assertTrue(output.contains("Relatório de Cobertura JaCoCo"))
        assertTrue(output.contains(reportHtmlFile.toURI().toString()))
        assertTrue(output.contains("JaCoCo Coverage Report (:testTask)"))
        assertTrue(output.contains("mypackage"))
    }

    private inline fun <reified T> mock(noinline handler: (method: String) -> Any?): T {
        return Proxy.newProxyInstance(
            T::class.java.classLoader,
            arrayOf(T::class.java),
        ) { _, method, _ ->
            if (method.name == "toString") {
                "Mock of ${T::class.simpleName}"
            } else {
                handler(method.name)
            }
        } as T
    }

    private fun mockFinishEvent(): FinishEvent {
        return mock { null }
    }

    private fun mockTaskFinishEvent(
        taskPath: String,
        isSuccess: Boolean,
    ): TaskFinishEvent {
        val descriptor = mock<TaskOperationDescriptor> { method ->
            when (method) {
                "getTaskPath" -> taskPath
                else -> null
            }
        }
        val result = if (isSuccess) {
            mock<TaskSuccessResult> { null }
        } else {
            mock<TaskFailureResult> { null }
        }

        return mock<TaskFinishEvent> { method ->
            when (method) {
                "getDescriptor" -> descriptor
                "getResult" -> result
                else -> null
            }
        }
    }
}
