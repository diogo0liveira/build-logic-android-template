package com.dao.convention.plugins

import com.dao.convention.createJacocoConsumableConfiguration
import com.dao.convention.dependencies.id
import com.dao.convention.dependencies.libs
import com.dao.convention.dependencies.version
import com.dao.convention.extensions.createJacocoVerificationExtension
import com.dao.convention.tasks.register.registerCoverageTask
import javax.inject.Inject
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.testing.Test
import org.gradle.build.event.BuildEventsListenerRegistry
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.gradle.testing.jacoco.plugins.JacocoPlugin
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension

/**
 * Plugin de convenção interno para habilitar cobertura de código JaCoCo em módulos JVM puros.
 *
 * Vincula a saída de compilação do `SourceSet.main` às tarefas de relatório local
 * e publica os artefatos de cobertura na configuração consumível `coverageDataProducer`.
 */
internal abstract class JacocoJvmConventionPlugin @Inject constructor(
    private val registry: BuildEventsListenerRegistry,
) : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            pluginManager.apply(JacocoPlugin::class)

            pluginManager.withPlugin(libs.plugins.kotlin.jvm.id) {
                configure<JacocoPluginExtension> {
                    toolVersion = libs.versions.jacoco.version
                }

                val sourceSets = extensions.getByType<SourceSetContainer>().named("main")
                val configuration = createJacocoConsumableConfiguration()
                val verification = createJacocoVerificationExtension()

                registerCoverageTask(
                    source = sourceSets,
                    registry = registry,
                    verification = verification,
                    configuration = configuration,
                )

                tasks.withType<Test>().configureEach {
                    configure<JacocoTaskExtension> {
                        excludes = listOf("jdk.internal.*")
                    }
                }
            }
        }
    }
}
