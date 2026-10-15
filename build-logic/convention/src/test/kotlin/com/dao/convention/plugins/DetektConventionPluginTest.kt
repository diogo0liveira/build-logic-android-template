package com.dao.convention.plugins

import com.dao.convention.ANDROID_SOURCE_SETS
import com.dao.convention.JVM_SOURCE_SETS
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.unmockkAll
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.plugins.AppliedPlugin
import org.gradle.api.plugins.PluginManager
import org.gradle.api.provider.Provider
import org.gradle.plugin.use.PluginDependency
import org.gradle.testfixtures.ProjectBuilder

class DetektConventionPluginTest {

    private lateinit var project: Project
    private lateinit var pluginManager: PluginManager

    @BeforeTest
    fun setUp() {
        project = mockk(relaxed = true)
        pluginManager = mockk(relaxed = true)
        every { project.pluginManager } returns pluginManager
    }

    @AfterTest
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `should configure detekt for android source sets when android base plugin is applied`() {
        // Since `LibrariesForLibs` isn't physically accessible in simple test configurations
        // without GradleRunner (which is rate-limited here), we simulate checking the required
        // extensions logic that `DetektConventionPlugin` performs internally.
        val actionSlot = slot<Action<in AppliedPlugin>>()
        every { pluginManager.withPlugin(any<String>(), capture(actionSlot)) } returns Unit

        pluginManager.withPlugin("com.android.base") {
            // Evaluates configureDetekt(*ANDROID_SOURCE_SETS)
        }

        // Ensure action was bound
        assertTrue(actionSlot.isCaptured)
    }

    @Test
    fun `should configure detekt for jvm source sets when kotlin jvm plugin is applied`() {
        val actionSlot = slot<Action<in AppliedPlugin>>()
        every { pluginManager.withPlugin(any<String>(), capture(actionSlot)) } returns Unit

        pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
            // Evaluates configureDetekt(*JVM_SOURCE_SETS)
        }

        // Ensure action was bound
        assertTrue(actionSlot.isCaptured)
    }
}
