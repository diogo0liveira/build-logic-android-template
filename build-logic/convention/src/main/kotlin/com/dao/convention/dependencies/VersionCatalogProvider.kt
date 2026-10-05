package com.dao.convention.dependencies

import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.artifacts.VersionConstraint
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.getByType

/**
 * Obtém o catálogo de versões principal (`libs`) do projeto.
 */
val Project.catalog: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>()
        .named("libs")

/**
 * Fornece acesso tipado às bibliotecas geradas pelo catálogo de versões `libs`.
 */
val Project.libs: LibrariesForLibs
    get() = extensions.getByType<LibrariesForLibs>()

/**
 * Busca uma biblioteca específica do catálogo de versões através do seu [alias].
 *
 * @param alias O apelido da biblioteca configurado no Version Catalog.
 * @return Um provedor da dependência externa correspondente.
 */
fun VersionCatalog.libraryOf(alias: String): Provider<MinimalExternalModuleDependency> {
    return findLibrary(alias).get()
}

/**
 * Busca uma restrição de versão específica do catálogo de versões através do seu [alias].
 *
 * @param alias O apelido da versão configurado no Version Catalog.
 * @return A restrição de versão correspondente.
 */
fun VersionCatalog.versionOf(alias: String): VersionConstraint {
    return findVersion(alias).get()
}

/**
 * Obtém o valor textual (String) da versão a partir de um provedor de versão.
 */
val Provider<String>.version: String
    get() = get()
