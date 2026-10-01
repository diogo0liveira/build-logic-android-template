package com.dao.convention.services

import com.dao.convention.services.CoverageReportLinkService.Parameters
import com.dao.convention.services.coverage.CoverageThresholds
import java.math.BigDecimal
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import org.gradle.tooling.events.FinishEvent
import org.gradle.tooling.events.OperationCompletionListener
import org.gradle.tooling.events.task.TaskFinishEvent

internal abstract class CoverageReportLinkService :
    BuildService<Parameters>,
    OperationCompletionListener {
    interface Parameters : BuildServiceParameters {
        val task: Property<String>
        val reportCsv: RegularFileProperty
        val reportHtml: RegularFileProperty

        val minInstructionCoverage: Property<BigDecimal>
        val minBranchCoverage: Property<BigDecimal>
        val minLineCoverage: Property<BigDecimal>
        val warningTolerance: Property<BigDecimal>
    }

    override fun onFinish(event: FinishEvent) {
        if (event.isTargetTaskFinished()) {
            if (parameters.reportHtml.isPresent && parameters.reportCsv.isPresent) {
                val htmlReport = parameters.reportHtml.asFile.get()
                val csvReport = parameters.reportCsv.asFile.get()

                if (htmlReport.exists() && csvReport.exists()) {
                    val minInstruction = parameters.minInstructionCoverage.get().toDouble()
                    val minBranch = parameters.minBranchCoverage.get().toDouble()
                    val minLine = parameters.minLineCoverage(minInstruction).get().toDouble()
                    val tolerance = parameters.warningTolerance.get().toDouble()

                    val thresholds = CoverageThresholds(
                        minInstruction = minInstruction,
                        minBranch = minBranch,
                        minLine = minLine,
                        warningTolerance = tolerance,
                    )

                    val summary = CoverageConsoleSummary.parse(
                        task = parameters.task.get(),
                        htmlReport = htmlReport,
                        csvReport = csvReport,
                        thresholds = thresholds,
                    )

                    if (summary != null) {
                        println("\n" + CoverageConsoleFormatter.format(summary))
                    }
                }
            }
        }
    }

    private fun Parameters.minLineCoverage(minInstruction: Double): Provider<BigDecimal> {
        return minLineCoverage.map { minLine ->
            if (minLine > BigDecimal.ZERO) minLine else minInstruction.toBigDecimal()
        }
    }

    private fun FinishEvent.isTargetTaskFinished(): Boolean {
        return when {
            (this !is TaskFinishEvent) -> false
            (descriptor.taskPath != parameters.task.get()) -> false
            else -> true
        }
    }
}
