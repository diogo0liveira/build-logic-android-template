package com.dao.convention.services.coverage

/**
 * Metas e tolerâncias dinâmicas de cobertura para instruções, ramificações e linhas.
 */
internal data class CoverageThresholds(
    val minInstruction: Double,
    val minBranch: Double,
    val minLine: Double = minInstruction,
    val warningTolerance: Double,
) {
    fun statusForInstruction(counter: CoverageCounter): CoverageStatus {
        return evaluate(counter.percentage, minInstruction)
    }

    fun statusForBranch(counter: CoverageCounter): CoverageStatus {
        return evaluate(counter.percentage, minBranch)
    }

    fun statusForLine(counter: CoverageCounter): CoverageStatus {
        return evaluate(counter.percentage, minLine)
    }

    private fun evaluate(
        percentage: Double?,
        targetRatio: Double,
    ): CoverageStatus {
        if (percentage == null) return CoverageStatus.NONE
        val target = targetRatio * PERCENTAGE_FACTOR
        val warningLimit = (targetRatio - warningTolerance) * PERCENTAGE_FACTOR

        return when {
            percentage >= target -> CoverageStatus.PASSED
            percentage >= warningLimit -> CoverageStatus.WARNING
            else -> CoverageStatus.FAILED
        }
    }

    companion object {
        private const val PERCENTAGE_FACTOR = 100.0
    }
}
