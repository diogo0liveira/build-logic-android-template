package com.dao.convention.extensions

import java.math.BigDecimal
import org.gradle.api.provider.Property

/**
 * Extensão de configuração para as regras de verificação de cobertura de código JaCoCo.
 */
abstract class JacocoVerificationExtension {
    /**
     * Taxa mínima de cobertura de instruções (bytecode). Convenção padrão: 0 (desabilitado).
     */
    abstract val minInstructionCoverage: Property<BigDecimal>

    /**
     * Taxa mínima de cobertura de ramificações (condicionais). Convenção padrão: 0 (desabilitado).
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

    init {
        minInstructionCoverage.convention(BigDecimal.ZERO)
        minBranchCoverage.convention(BigDecimal.ZERO)
        minLineCoverage.convention(BigDecimal.ZERO)
        haltOnFailure.convention(false)
    }
}
