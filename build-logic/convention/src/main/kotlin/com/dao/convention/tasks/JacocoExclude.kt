package com.dao.convention.tasks

internal val JACOCO_EXCLUDE_PATTERNS = listOf(
    "**/R.class",
    "**/R$*.class",
    "**/Manifest*.*",
    "**/BuildConfig.*",
    $$"**/*$ViewBinder*.*",
    $$"**/*$ViewInjector*.*",
    "android/**/*.*",
    // Data Binding
    "**/*Binding.*",
    "**/*BindingImpl.*",
    "**/databinding/*",
    "**/DataBinderMapperImpl*",
    // Compose
    "**/theme/**",
    "**/ui/theme/**",
    $$"**/*$Lambda$*.*",
    $$"**/*$default*.*",
    "**/*Preview*.*",
    "**/*PreviewKt.class",
    "**/ComposableSingletons$*",
    "**/*ComposableSingletons*.*",
    // Navigation
    "**/*Args*.*",
    "**/*NavGraph*.*",
    "**/*Directions*.*",
    "**/*Destinations*.*",
    $$"**/*Screen$Companion*.*",
    // Kotlin
    $$"**/*$WhenMappings.*",
    "**/*Kt$*.class", // top-level funções lambda internas
    "**/*JsonAdapter.*", // Moshi generated adapters
    "**/*_Impl.*", // Room DAOs/Database
    $$"**/*$serializer.*", // kotlinx.serialization
    // Hilt/Dagger
    "dagger/**",
    "**/*_Factory*",
    "**/*_GeneratedInjector*",
    "**/*_ComponentTreeDeps*",
    "**/*_MembersInjector*",
    "**/*_Provide*Factory*",
    "**/*Module_*Factory.*",
    "**/Dagger*Component*.*",
    "**/*_HiltComponents*.*",
    "**/hilt_aggregated_deps/**",
    "**/*_HiltModules*",
    "**/HiltWrapper_*",
    "**/*_Hilt*",
    "**/Hilt_*",
)
