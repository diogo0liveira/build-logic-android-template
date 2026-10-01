package com.dao.convention.extensions

import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.findByType

private const val JACOCO_VERIFICATION_EXTENSION = "jacocoVerification"

internal fun Project.createJacocoVerificationExtension(): JacocoVerificationExtension {
    return extensions.findByType<JacocoVerificationExtension>() ?: extensions
        .create<JacocoVerificationExtension>(JACOCO_VERIFICATION_EXTENSION)
}
