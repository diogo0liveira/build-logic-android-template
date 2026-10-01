package com.dao.convention.services.coverage

internal data class JacocoReportSummary(
    val headerTitle: String,
    val packages: List<PackageCoverage>,
) {
    val totalLine: CoverageCounter = packages
        .fold(CoverageCounter.ZERO) { acc, p -> acc + p.line }

    val totalBranch: CoverageCounter = packages
        .fold(CoverageCounter.ZERO) { acc, p -> acc + p.branch }

    val totalInstruction: CoverageCounter = packages
        .fold(CoverageCounter.ZERO) { acc, p -> acc + p.instruction }
}
