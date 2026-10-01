package com.dao.convention.services

import com.dao.convention.services.coverage.CoverageCounter
import com.dao.convention.services.coverage.JacocoReportSummary

/**
 * Formata um [JacocoReportSummary] como tabela de texto alinhada para o console.
 *
 * Colunas: Line, Branch e Instruction, cada uma como "percentual (covered/total)".
 * As larguras são calculadas a partir do conteúdo, então nada desalinha com nomes longos
 * ou números grandes.
 */
internal object JacocoConsoleFormatter {
    private const val MIN_LABEL_WIDTH = 50
    private const val CLASS_INDENT = "  "
    private const val COLUMN_GAP = "   "

    private const val LABEL_HEADER = "Class/Package"
    private val COLUMN_HEADERS = listOf("Line (%)", "Branch (%)", "Instruction (%)")

    private class Row(
        val label: String,
        val cells: List<String>,
    ) {
        constructor(
            label: String,
            line: CoverageCounter,
            branch: CoverageCounter,
            instruction: CoverageCounter,
        ) : this(label, listOf(line.summary, branch.summary, instruction.summary))

        val labelLength: Int = label.length
    }

    fun format(summary: JacocoReportSummary): String {
        val rows = summary.toRows()

        val labelWidth = maxOf(
            MIN_LABEL_WIDTH,
            LABEL_HEADER.length,
            rows.maxOf(Row::labelLength),
        )

        val columnWidths = COLUMN_HEADERS.mapIndexed { index, header ->
            maxOf(header.length, rows.maxOf { it.cells[index].length })
        }

        val totalWidth = labelWidth + columnWidths.sumOf { COLUMN_GAP.length + it }

        fun line(
            label: String,
            cells: List<String>,
        ) = buildString {
            append(label.padEnd(labelWidth))
            cells.forEachIndexed { index, cell ->
                append(COLUMN_GAP)
                append(cell.padStart(columnWidths[index]))
            }
        }

        val equalsLine = "=".repeat(totalWidth)
        val dashLine = "-".repeat(totalWidth)

        return buildString {
            appendLine(equalsLine)
            appendLine(summary.headerTitle)
            appendLine(equalsLine)
            appendLine(line(LABEL_HEADER, COLUMN_HEADERS))
            appendLine(dashLine)
            rows.forEach { appendLine(line(it.label, it.cells)) }
            append(dashLine)
        }
    }

    private fun JacocoReportSummary.toRows(): List<Row> = buildList {
        add(Row("PROJECT TOTAL", totalLine, totalBranch, totalInstruction))
        for (pkg in packages) {
            val name = pkg.name.ifEmpty { "(default)" }
            add(Row("$name (package)", pkg.line, pkg.branch, pkg.instruction))
            for ((name, line, branch, instruction) in pkg.classes) {
                val name = if (pkg.name.isEmpty()) name else "${pkg.name}.$name"
                add(Row("$CLASS_INDENT$name", line, branch, instruction))
            }
        }
    }
}
