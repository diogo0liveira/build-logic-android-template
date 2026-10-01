package com.dao.convention.services.coverage

/** Pacote com seus totais derivados da soma das classes. */
internal data class PackageCoverage(
    val name: String,
    val classes: List<ClassCoverage>,
) {
    val line: CoverageCounter = classes
        .fold(CoverageCounter.ZERO) { acc, c -> acc + c.line }

    val branch: CoverageCounter = classes
        .fold(CoverageCounter.ZERO) { acc, c -> acc + c.branch }

    val instruction: CoverageCounter = classes
        .fold(CoverageCounter.ZERO) { acc, c -> acc + c.instruction }
}
