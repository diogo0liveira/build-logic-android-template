package com.dao.convention.services

import com.dao.convention.services.coverage.ClassCoverage
import com.dao.convention.services.coverage.CoverageCounter
import com.dao.convention.services.coverage.JacocoReportSummary
import com.dao.convention.services.coverage.PackageCoverage
import java.io.File

/**
 * Lê o CSV gerado pelo JaCoCo ('GROUP, PACKAGE, CLASS, INSTRUCTION_MISSED, … ').
 *
 * O CSV do JaCoCo não usa aspas/escape; nomes de classe com vírgula não são suportados.
 * Colunas são localizadas pelo nome do cabeçalho, então a ordem não importa.
 */
internal object JacocoReportSummary {
    private const val PACKAGE = "PACKAGE"
    private const val CLASS = "CLASS"
    private const val LINE_MISSED = "LINE_MISSED"
    private const val LINE_COVERED = "LINE_COVERED"
    private const val BRANCH_MISSED = "BRANCH_MISSED"
    private const val BRANCH_COVERED = "BRANCH_COVERED"
    private const val INSTRUCTION_MISSED = "INSTRUCTION_MISSED"
    private const val INSTRUCTION_COVERED = "INSTRUCTION_COVERED"

    /**
     * @return o resumo, ou null se o arquivo não existir ou estiver vazio.
     * @throws IllegalStateException se faltar coluna obrigatória ou houver valor numérico inválido.
     */
    internal fun parse(
        csvFile: File,
        headerTitle: String = "JaCoCo Coverage Report",
    ): JacocoReportSummary? {
        if (!csvFile.isFile || csvFile.length() == 0L) return null

        return csvFile.useLines { lines ->
            val lines = lines.filter(String::isNotBlank).iterator()

            val header = lines.next().split(',').withIndex()
                .associate { (index, name) -> name.trim() to index }

            fun List<String>.counter(
                missedCol: String,
                coveredCol: String,
            ) = CoverageCounter(
                missed = getValue(missedCol, header),
                covered = getValue(coveredCol, header),
            )

            val packages = lines.asSequence()
                .map { line ->
                    val columns = line.split(',')
                    columns[header.getValue(PACKAGE)] to ClassCoverage(
                        name = columns[header.getValue(CLASS)],
                        line = columns.counter(LINE_MISSED, LINE_COVERED),
                        branch = columns.counter(BRANCH_MISSED, BRANCH_COVERED),
                        instruction = columns.counter(INSTRUCTION_MISSED, INSTRUCTION_COVERED),
                    )
                }
                .groupBy({ it.first }, { it.second })
                .map { (name, classes) -> PackageCoverage(name, classes.sortedBy(ClassCoverage::name)) }
                .sortedBy(PackageCoverage::name).toList()

            JacocoReportSummary(
                headerTitle = headerTitle,
                packages = packages,
            )
        }
    }

    private fun List<String>.getValue(
        column: String,
        header: Map<String, Int>,
    ): Long {
        val raw = this[header.getValue(column)].trim()
        return checkNotNull(raw.toLongOrNull()) {
            "Valor inválido em $column: '$raw'"
        }
    }
}
