package com.dao.convention

import com.android.build.api.dsl.Lint

internal fun Lint.configureLint() {
    // Fallback lint configuration (default severities, etc.)
    // lintConfig = file("default-lint.xml")
    // Use (or create) a baseline file for issues that should not be reported
    // baseline = file("lint-baseline.xml")

    checkDependencies = false
    checkReleaseBuilds = true
    checkTestSources = true
    explainIssues = true
    noLines = false
    showAll = true
}
