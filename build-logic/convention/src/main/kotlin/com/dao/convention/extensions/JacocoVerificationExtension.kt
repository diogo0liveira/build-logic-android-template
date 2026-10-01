package com.dao.convention.extensions

import java.math.BigDecimal
import org.gradle.api.provider.Property

/**
 * Extensão de configuração para as regras de verificação de cobertura de código JaCoCo.
 */
abstract class JacocoVerificationExtension {
    /**
     * Taxa mínima de cobertura de instruções (bytecode). Convenção padrão: 0.90 (90%).
     */
    abstract val minInstructionCoverage: Property<BigDecimal>

    /**
     * Taxa mínima de cobertura de ramificações (condicionais). Convenção padrão: 0.80 (80%).
     */
    abstract val minBranchCoverage: Property<BigDecimal>

    /**
     * Taxa mínima de cobertura de linhas. Convenção padrão: 0 (desabilitado).
     */
    abstract val minLineCoverage: Property<BigDecimal>

    /**
     * Define se violações devem interromper a compilação (build). Convenção padrão: true.
     */
    abstract val haltOnFailure: Property<Boolean>

    /**
     * Margem de tolerância percentual abaixo da meta, amarelo de atenção. Convenção padrão: 0.15 (15%).
     */
    abstract val warningTolerance: Property<BigDecimal>

    init {
        minInstructionCoverage.convention(0.90.toBigDecimal())
        minBranchCoverage.convention(0.80.toBigDecimal())
        minLineCoverage.convention(BigDecimal.ZERO)
        warningTolerance.convention(0.15.toBigDecimal())
        haltOnFailure.convention(true)
    }
}
