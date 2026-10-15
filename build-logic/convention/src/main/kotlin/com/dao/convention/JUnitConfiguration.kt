package com.dao.convention

import com.dao.convention.dependencies.libs
import com.dao.convention.dependencies.testImplementation
import com.dao.convention.dependencies.testRuntimeOnly
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType

/**
 * Habilita JUnit 6 (Jupiter) nos testes unitários locais do módulo, mantendo JUnit 4 funcional.
 *
 * - Adiciona o BOM e o Jupiter via `testImplementation`.
 * - Adiciona o `junit-vintage-engine` para que testes JUnit 4 (Robolectric, Compose) sigam rodando.
 * - Adiciona o `junit-platform-launcher` exigido pelo Gradle em runtime.
 * - Configura as tasks [Test] com `useJUnitPlatform()` e silencia o aviso de depreciação do vintage.
 *
 * Testes instrumentados (`androidTest`) permanecem em JUnit 4, pois o `AndroidJUnitRunner`
 * não suporta JUnit 6 sem plugins de terceiros.
 */
internal fun Project.configureJUnit6() {
    dependencies {
        testImplementation(platform(libs.test.junit6.bom))
        testImplementation(libs.test.junit6.jupiter)
        testRuntimeOnly(libs.test.junit6.launcher)
        testRuntimeOnly(libs.test.junit6.vintage)
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
        systemProperty("junit.vintage.discovery.issue.reporting.enabled", "false")
    }
}
