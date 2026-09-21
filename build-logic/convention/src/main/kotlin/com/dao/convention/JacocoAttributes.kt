package com.dao.convention

import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.attributes.Category
import org.gradle.api.attributes.VerificationType
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.named

private const val JACOCO_COVERAGE_DATA = "jacoco-coverage-data"
private const val JACOCO_CONSUMABLE_CONFIGURATION_NAME = "coverageDataProducer"
private const val JACOCO_RESOLVABLE_CONFIGURATION_NAME = "coverageDataConsumer"

@Suppress("UnstableApiUsage")
private fun Project.configureAttributes(configuration: Configuration) {
    configuration.attributes {
        attribute(
            Category.CATEGORY_ATTRIBUTE,
            objects.named(
                Category::class,
                Category.VERIFICATION,
            ),
        )
        attribute(
            VerificationType.VERIFICATION_TYPE_ATTRIBUTE,
            objects.named(
                VerificationType::class,
                JACOCO_COVERAGE_DATA,
            ),
        )
    }
}

/**
 * Cria e configura a Consumable Configuration padrão para compartilhamento
 * de dados de cobertura entre projetos.
 */
internal fun Project.createJacocoConsumableConfiguration(): NamedDomainObjectProvider<Configuration> {
    return configurations.register(JACOCO_CONSUMABLE_CONFIGURATION_NAME) {
        configureAttributes(this)
        isCanBeConsumed = true
        isCanBeResolved = false
    }
}

/**
 * Cria e configura a Resolvable Configuration no projeto consumidor (Root Project)
 * para solicitar os dados expostos pelas Consumable Configurations.
 */
internal fun Project.createJacocoResolvableConfiguration(): NamedDomainObjectProvider<Configuration> {
    return configurations.register(JACOCO_RESOLVABLE_CONFIGURATION_NAME) {
        configureAttributes(this)
        isCanBeConsumed = false
        isCanBeResolved = true
    }
}

/**
 * Retorna os arquivos de artefatos de forma lazy (tardia),
 * garantindo compatibilidade com o Task Configuration Avoidance do Gradle.
 */
internal val NamedDomainObjectProvider<Configuration>.artifactFiles: Provider<FileCollection>
    get() = map { configuration -> configuration.incoming.artifactView { }.files }
