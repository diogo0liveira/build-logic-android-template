package com.dao.convention.dependencies

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.Test
import kotlin.test.assertEquals
import org.gradle.api.Action
import org.gradle.api.plugins.AppliedPlugin
import org.gradle.api.plugins.PluginManager
import org.gradle.api.provider.Provider
import org.gradle.plugin.use.PluginDependency

class PluginDependencyHandlerTest {
    @Test
    fun `id property should return pluginId from PluginDependency`() {
        val pluginDependency = mockk<PluginDependency>()
        every { pluginDependency.pluginId } returns "com.example.test-plugin"

        val provider = mockk<Provider<PluginDependency>>()
        every { provider.get() } returns pluginDependency

        assertEquals("com.example.test-plugin", provider.id)
    }

    @Test
    fun `apply should call PluginManager_apply with pluginId`() {
        val pluginDependency = mockk<PluginDependency>()
        every { pluginDependency.pluginId } returns "com.example.apply-plugin"

        val provider = mockk<Provider<PluginDependency>>()
        every { provider.get() } returns pluginDependency

        val pluginManager = mockk<PluginManager>(relaxed = true)
        pluginManager.apply(provider)

        verify { pluginManager.apply("com.example.apply-plugin") }
    }

    @Test
    fun `withPlugin should call PluginManager_withPlugin with pluginId and action`() {
        val pluginDependency = mockk<PluginDependency>()
        every { pluginDependency.pluginId } returns "com.example.with-plugin"

        val provider = mockk<Provider<PluginDependency>>()
        every { provider.get() } returns pluginDependency

        val pluginManager = mockk<PluginManager>(relaxed = true)
        val action = mockk<Action<in AppliedPlugin>>()
        pluginManager.withPlugin(provider, action)

        verify { pluginManager.withPlugin("com.example.with-plugin", action) }
    }
}
