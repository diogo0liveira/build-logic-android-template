package com.dao.convention.services

import com.dao.convention.services.CoverageReportLinkService.Parameters
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import org.gradle.tooling.events.FinishEvent
import org.gradle.tooling.events.OperationCompletionListener
import org.gradle.tooling.events.task.TaskFinishEvent
import org.gradle.tooling.events.task.TaskSuccessResult

internal abstract class CoverageReportLinkService :
    BuildService<Parameters>,
    OperationCompletionListener {
    interface Parameters : BuildServiceParameters {
        val task: Property<String>
        val reportHtml: RegularFileProperty
    }

    override fun onFinish(event: FinishEvent) {
        if (event.isTaskSuccessResult()) {
            if (parameters.reportHtml.isPresent) {
                val report = parameters.reportHtml.asFile.get()

                if (report.exists()) {
                    println("\n📊  Relatório de Cobertura JaCoCo")
                    println("👉  ${report.toURI()}\n")
                }
            }
        }
    }

    private fun FinishEvent.isTaskSuccessResult(): Boolean {
        return when {
            (this !is TaskFinishEvent) -> false
            (descriptor.taskPath != parameters.task.get()) -> false
            else -> (result is TaskSuccessResult)
        }
    }
}
