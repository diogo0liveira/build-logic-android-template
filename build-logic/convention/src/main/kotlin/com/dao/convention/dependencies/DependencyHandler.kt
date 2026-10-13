package com.dao.convention.dependencies

import org.gradle.kotlin.dsl.DependencyHandlerScope

/**
 * Adiciona uma dependência de implementação ao projeto.
 *
 * @receiver O escopo do manipulador de dependências ao qual adicionar a dependência.
 * @param dependency O item de dependência a ser adicionado.
 */
fun DependencyHandlerScope.implementation(dependency: Any) {
    add("implementation", dependency)
}

/**
 * Adiciona uma dependência de implementação de teste ao projeto.
 *
 * @receiver O escopo do manipulador de dependências ao qual adicionar a dependência.
 * @param dependency O item de dependência a ser adicionado.
 */
fun DependencyHandlerScope.testImplementation(dependency: Any) {
    add("testImplementation", dependency)
}

/**
 * Adiciona uma dependência de implementação de teste do Android ao projeto.
 *
 * @receiver O escopo do manipulador de dependências ao qual adicionar a dependência.
 * @param dependency O item de dependência a ser adicionado.
 */
fun DependencyHandlerScope.androidTestImplementation(dependency: Any) {
    add("androidTestImplementation", dependency)
}

/**
 * Adiciona uma dependência de implementação de depuração ao projeto.
 *
 * @receiver O escopo do manipulador de dependências ao qual adicionar a dependência.
 * @param dependency O item de dependência a ser adicionado.
 */
fun DependencyHandlerScope.debugImplementation(dependency: Any) {
    add("debugImplementation", dependency)
}

/**
 * Adiciona uma dependência de plug-in do Detekt ao projeto.
 *
 * @receiver O escopo do manipulador de dependências ao qual adicionar a dependência.
 * @param dependency O item de dependência a ser adicionado.
 */
fun DependencyHandlerScope.detektPlugins(dependency: Any) {
    add("detektPlugins", dependency)
}

/**
 * Adiciona uma dependência de verificação do Lint ao projeto.
 *
 * @receiver O escopo do manipulador de dependências ao qual adicionar a dependência.
 * @param dependency O item de dependência a ser adicionado.
 */
fun DependencyHandlerScope.lintChecks(dependency: Any) {
    add("lintChecks", dependency)
}

/**
 * Adiciona uma dependência de processador KSP ao projeto.
 *
 * @receiver O escopo do manipulador de dependências ao qual adicionar a dependência.
 * @param dependency O item de dependência a ser adicionado.
 */
fun DependencyHandlerScope.ksp(dependency: Any) {
    add("ksp", dependency)
}
