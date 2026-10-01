package com.dao.convention

import org.gradle.api.file.DirectoryProperty

private val JACOCO_HEADER = byteArrayOf(
    0x01,
    0xc0.toByte(),
    0xc0.toByte(),
    0x10,
    0x07,
)

internal fun DirectoryProperty.addReportFallbackIfNecessary() {
    with(asFileTree) {
        val classes = matching { include("classes/**/*.class") }
        val execs = matching { include("exec/unit/**/*.exec", "exec/android/**/*.ec") }

        if (!classes.isEmpty && execs.isEmpty) {
            file("exec/unit/empty.exec").get()
                .asFile.apply {
                    parentFile.mkdirs()
                    writeBytes(JACOCO_HEADER)
                }
        }
    }
}
