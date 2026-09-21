package com.dao.convention

internal const val BUILD_LOGIC = "build-logic"
internal const val GMD_GROUP_NAME = "devices"

/* sources */
internal const val SOURCE = "src"
internal const val SRC_MAIN_DIR = "$SOURCE/main/kotlin"
internal const val SRC_TEST_DIR = "$SOURCE/test/kotlin"
internal const val SRC_ANDROID_TEST_DIR = "$SOURCE/androidTest/kotlin"
internal val ANDROID_SOURCE_SETS = arrayOf(SRC_MAIN_DIR, SRC_TEST_DIR, SRC_ANDROID_TEST_DIR)
internal val JVM_SOURCE_SETS = arrayOf(SRC_MAIN_DIR, SRC_TEST_DIR)

/* JaCoco */
internal const val COVERAGE_TASK_GROUP = "Coverage Report Convention"
internal const val COVERAGE_OUTPUT = "coverage_aggregation/"
