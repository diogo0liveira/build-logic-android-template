package com.dao.convention.services

import com.dao.convention.services.coverage.CoverageCounter
import com.dao.convention.services.coverage.CoverageStatus
import com.dao.convention.services.coverage.CoverageSummary

/**
 * Formata um [CoverageSummary] como tabela de texto alinhada para o console.
 *
 * Colunas: Line, Branch e Instruction, cada uma como "percentual (covered/total)".
 * As larguras são calculadas a partir do conteúdo puro, garantindo alinhamento perfeito
 * mesmo com a aplicação de códigos de cor ANSI nas métricas.
 */
internal object CoverageConsoleFormatter {
    private const val MIN_LABEL_WIDTH = 50
    private const val COLUMN_GAP = "   "
    private const val TREE_BRANCH = "  ├── "
    private const val TREE_CORNER = "  └── "

    private const val TOTAL_LABEL = "Total"
    private const val LABEL_HEADER = "Package/Class"

    private const val FOOTER = "👉 Relatório HTML:"
    private val COLUMN_HEADERS = listOf("Line (%)", "Branch (%)", "Instruction (%)")

    private const val ANSI_RESET = "\u001B[0m"
    private const val ANSI_GREEN = "\u001B[32m"
    private const val ANSI_YELLOW = "\u001B[33m"
    private const val ANSI_RED = "\u001B[31m"

    private class Cell(
        val text: String,
        val status: CoverageStatus = CoverageStatus.NONE,
    ) {
        val length: Int = text.length
    }

    private class Row(
        val label: String,
        val cells: List<Cell>,
    ) {
        val labelLength: Int = label.length
    }

    fun format(
        summary: CoverageSummary,
        enableColors: Boolean = true,
    ): String {
        val rows = summary.toRows()
        val totalRow = Row(
            label = TOTAL_LABEL,
            cells = listOf(
                Cell(
                    summary.totalLine.totalSummary,
                    summary.thresholds.statusForLine(summary.totalLine),
                ),

                Cell(
                    summary.totalBranch.totalSummary,
                    summary.thresholds.statusForBranch(summary.totalBranch),
                ),

                Cell(
                    summary.totalInstruction.totalSummary,
                    summary.thresholds.statusForInstruction(summary.totalInstruction),
                ),
            ),
        )

        val allRows = rows + totalRow

        val minLabelWidth = maxOf(
            MIN_LABEL_WIDTH,
            LABEL_HEADER.length,
            allRows.maxOf(Row::labelLength),
        )

        val columnWidths = COLUMN_HEADERS.mapIndexed { index, header ->
            maxOf(header.length, allRows.maxOf { it.cells[index].length })
        }

        val footer = "$FOOTER ${summary.htmlReportUrl}"
        val columnsWidth = columnWidths.sumOf { COLUMN_GAP.length + it }
        val targetWidth = maxOf(minLabelWidth + columnsWidth, footer.length, summary.header.length)
        val labelWidth = targetWidth - columnsWidth

        fun line(
            label: String,
            cells: List<Cell>,
        ) = buildString {
            append(label.padEnd(labelWidth))
            cells.forEachIndexed { index, cell ->
                append(COLUMN_GAP)
                val padded = cell.text.padStart(columnWidths[index])
                append(if (enableColors) colorize(padded, cell.status) else padded)
            }
        }

        val equalsLine = "=".repeat(targetWidth)
        val dashLine = "-".repeat(targetWidth)
        val headerCells = COLUMN_HEADERS.map { Cell(it, CoverageStatus.NONE) }

        return buildString {
            appendLine(equalsLine)
            appendLine(summary.header)
            appendLine(equalsLine)
            appendLine(line(LABEL_HEADER, headerCells))
            appendLine(dashLine)
            rows.forEach { appendLine(line(it.label, it.cells)) }
            appendLine(dashLine)
            appendLine(line(totalRow.label, totalRow.cells))
            appendLine(equalsLine)
            appendLine(footer)
            append(equalsLine)
        }
    }

    private fun colorize(
        text: String,
        status: CoverageStatus,
    ): String = when (status) {
        CoverageStatus.PASSED -> "$ANSI_GREEN$text$ANSI_RESET"
        CoverageStatus.WARNING -> "$ANSI_YELLOW$text$ANSI_RESET"
        CoverageStatus.FAILED -> "$ANSI_RED$text$ANSI_RESET"
        CoverageStatus.NONE -> text
    }

    private fun CoverageSummary.toRows(): List<Row> = buildList {
        for (pkg in packages) {
            val name = pkg.name.ifEmpty { "(default)" }
            add(
                Row(
                    label = name,
                    cells = listOf(
                        Cell(pkg.line.summary, thresholds.statusForLine(pkg.line)),
                        Cell(pkg.branch.summary, thresholds.statusForBranch(pkg.branch)),
                        Cell(pkg.instruction.summary, thresholds.statusForInstruction(pkg.instruction)),
                    ),
                ),
            )
            pkg.classes.forEachIndexed { index, (name, line, branch, instruction) ->
                val prefix = if (index == pkg.classes.lastIndex) TREE_CORNER else TREE_BRANCH
                add(
                    Row(
                        label = "$prefix$name",
                        cells = listOf(
                            Cell(line.summary, thresholds.statusForLine(line)),
                            Cell(branch.summary, thresholds.statusForBranch(branch)),
                            Cell(instruction.summary, thresholds.statusForInstruction(instruction)),
                        ),
                    ),
                )
            }
        }
    }
}
