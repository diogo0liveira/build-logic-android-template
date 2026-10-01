package com.dao.convention.extensions

import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

private const val JACOCO_AGGREGATION_EXTENSION = "jacocoAggregation"

internal fun Project.createJacocoAggregationExtension(): JacocoAggregationExtension {
    return extensions.create<JacocoAggregationExtension>(JACOCO_AGGREGATION_EXTENSION)
        .apply { modules.convention(emptyList()) }
}
