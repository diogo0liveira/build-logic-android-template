package com.dao.convention.plugins

import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.gradle.spotless.SpotlessPlugin
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.kotlin.dsl.add
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.hasPlugin
import org.gradle.plugin.use.PluginDependency
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class SpotlessConventionPluginTest {
    private lateinit var librariesForLibs: LibrariesForLibs
    private lateinit var project: Project

    @BeforeTest
    fun setUp() {
        project = ProjectBuilder.builder().build()
        librariesForLibs = mockk<LibrariesForLibs>(relaxed = true)

        every { librariesForLibs.versions.ktlint.get() } returns "1.8.0"

        every { librariesForLibs.plugins.kotlin.compose.get() } returns
            mockk<PluginDependency> {
                every { pluginId } returns "base"
            }

        every { librariesForLibs.ktlint.compose.rules.get() } returns
            mockk<MinimalExternalModuleDependency>()

        project.extensions.add(
            publicType = LibrariesForLibs::class,
            name = "libs",
            extension = librariesForLibs,
        )
    }

    @Test
    fun `should apply SpotlessPlugin and register extension when plugin is applied`() {
        project.plugins.apply(SpotlessConventionPlugin::class.java)

        assertTrue(project.plugins.hasPlugin(SpotlessPlugin::class))
        assertNotNull(project.extensions.findByType(SpotlessExtension::class))
    }

    @Test
    fun `should register spotless tasks when plugin is applied`() {
        project.plugins.apply(SpotlessConventionPlugin::class.java)

        assertNotNull(project.tasks.findByName("spotlessCheck"))
        assertNotNull(project.tasks.findByName("spotlessApply"))
        assertNotNull(project.tasks.findByName("spotlessKotlin"))
        assertNotNull(project.tasks.findByName("spotlessKotlinGradle"))
    }

    @Test
    fun `should configure compose rules when compose plugin is applied`() {
        project.plugins.apply(SpotlessConventionPlugin::class)
        project.pluginManager.apply("base")

        assertNotNull(project.extensions.findByType(SpotlessExtension::class))
    }
}
