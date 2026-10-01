package com.dao.convention

import com.dao.convention.extensions.JacocoVerificationExtension
import com.dao.convention.services.CoverageReportLinkService
import com.dao.convention.tasks.CoverageReportTask
import org.gradle.api.Task
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.registerIfAbsent
import org.gradle.testing.jacoco.tasks.JacocoReportsContainer

internal fun Task.listenerProvider(
    report: TaskProvider<CoverageReportTask>,
    verification: JacocoVerificationExtension,
): Provider<CoverageReportLinkService> {
    val reports = report.map(CoverageReportTask::getReports)
    return linkServiceBuilder(reports, verification)
}

private fun Task.linkServiceBuilder(
    reports: Provider<JacocoReportsContainer>,
    verification: JacocoVerificationExtension,
): Provider<CoverageReportLinkService> {
    return (if (project.isRoot) ":$name" else path).let { target ->
        project.gradle.sharedServices.registerIfAbsent(
            name = "${target}LinkService",
            implementationType = CoverageReportLinkService::class,
        ) {
            with(parameters) {
                reportCsv.fileProvider(reports.map { it.csv.outputLocation.get().asFile })
                reportHtml.fileProvider(reports.map { it.html.entryPoint })
                task.set(target)

                minInstructionCoverage.set(verification.minInstructionCoverage)
                minBranchCoverage.set(verification.minBranchCoverage)
                minLineCoverage.set(verification.minLineCoverage)
                warningTolerance.set(verification.warningTolerance)
            }
        }
    }
}
