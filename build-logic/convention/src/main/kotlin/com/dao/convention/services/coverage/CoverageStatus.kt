package com.dao.convention.services.coverage

/**
 * Representa os possíveis status de cobertura em relação às metas estabelecidas.
 */
internal enum class CoverageStatus {
    /** Atingiu a meta de cobertura esperada. */
    PASSED,

    /** Próximo da meta (zona de alerta/tolerância). */
    WARNING,

    /** Abaixo da meta esperada. */
    FAILED,

    /** Métrica não aplicável (ex: classes sem branches). */
    NONE,
}
