package com.dao.convention.services.coverage

import java.util.Locale

/** Contadores de cobertura do JaCoCo (itens perdidos e cobertos). */
internal data class CoverageCounter(
    val missed: Long,
    val covered: Long,
) {
    val total: Long get() = missed + covered

    /** Percentual de cobertura ou null se [total] for zero. */
    val percentage: Double? get() = if (total == 0L) null else covered * 100.0 / total

    /** Percentual com ponto decimal (ex: "85.50") ou "n/a" quando não há itens (ex: sem branches). */
    val formattedPercentage: String
        get() = percentage?.let { "%.2f".format(Locale.US, it) } ?: "n/a"

    /** Percentual com contagem absoluta (ex: "85.50 (171/200)") ou "n/a" quando não há itens. */
    val summary: String
        get() = percentage?.let { "%.2f (%d/%d)".format(Locale.US, it, covered, total) } ?: "n/a"

    operator fun plus(other: CoverageCounter) = CoverageCounter(missed + other.missed, covered + other.covered)

    companion object {
        val ZERO = CoverageCounter(0, 0)
    }
}
