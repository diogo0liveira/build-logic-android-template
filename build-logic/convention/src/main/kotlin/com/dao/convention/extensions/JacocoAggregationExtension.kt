package com.dao.convention.extensions

import org.gradle.api.provider.ListProperty

/**
 * Extensão de configuração para a agregação de relatórios de cobertura de código JaCoCo.
 */
interface JacocoAggregationExtension {
    /**
     * Lista de módulos incluídos na agregação de cobertura do JaCoCo.
     */
    val modules: ListProperty<String>
}
