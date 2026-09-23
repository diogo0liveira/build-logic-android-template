package com.dao.convention.dependencies

import org.gradle.api.Action
import org.gradle.api.plugins.AppliedPlugin
import org.gradle.api.plugins.PluginManager
import org.gradle.api.provider.Provider
import org.gradle.plugin.use.PluginDependency

/**
 * Obtém o ID do plugin a partir de um provedor de dependência de plugin.
 */
val Provider<PluginDependency>.id: String
    get() = get().pluginId

/**
 * Aplica um plugin a partir de um provedor de dependência de plugin.
 */
fun PluginManager.apply(notation: Provider<PluginDependency>) {
    return apply(notation.id)
}

/**
 * Executa uma ação quando um plugin específico, fornecido por um provedor de dependência, for aplicado.
 */
fun PluginManager.withPlugin(
    notation: Provider<PluginDependency>,
    action: Action<in AppliedPlugin>,
) {
    withPlugin(notation.id, action)
}
