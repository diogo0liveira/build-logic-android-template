package com.dao.convention

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.kotlin.dsl.register

internal fun Project.registerCoverageTask(
    name: String,
    action: Task.() -> Unit,
) {
    tasks.register<DefaultTask>("coverage${name}Report") {
        description = "Relatório de cobertura de teste."
        group = COVERAGE_TASK_GROUP
        action()
    }
}
