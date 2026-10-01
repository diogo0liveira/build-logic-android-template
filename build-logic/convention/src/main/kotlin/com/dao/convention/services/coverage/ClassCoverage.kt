package com.dao.convention.services.coverage

internal data class ClassCoverage(
    val name: String,
    val line: CoverageCounter,
    val branch: CoverageCounter,
    val instruction: CoverageCounter,
)
