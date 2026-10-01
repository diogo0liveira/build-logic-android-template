package com.dao.convention.services.coverage

import java.io.File

internal data class CoverageSummary(
    val header: String,
    val packages: List<PackageCoverage>,
    val thresholds: CoverageThresholds,
    val htmlReport: File,
) {
    val totalLine: CoverageCounter = packages
        .fold(CoverageCounter.ZERO) { acc, p -> acc + p.line }

    val totalBranch: CoverageCounter = packages
        .fold(CoverageCounter.ZERO) { acc, p -> acc + p.branch }

    val totalInstruction: CoverageCounter = packages
        .fold(CoverageCounter.ZERO) { acc, p -> acc + p.instruction }

    val htmlReportUrl: String
        get() = htmlReport.toURI().toString()
}
