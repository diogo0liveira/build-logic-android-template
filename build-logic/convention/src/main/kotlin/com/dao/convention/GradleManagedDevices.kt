@file:Suppress("UnstableApiUsage")

package com.dao.convention

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.ManagedVirtualDevice
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.invoke

internal fun configureGradleManagedDevices(commonExtension: CommonExtension) {
    commonExtension.apply {
        testOptions.animationsDisabled = true
    }

    val devices = listOf(
        DeviceConfig(
            device = "Pixel 4",
            apiLevel = 36,
            systemImageSource = "aosp-atd",
        ),
    )

    commonExtension.testOptions.apply {
        managedDevices {
            localDevices {
                devices.forEach(::create)
            }
            groups {
                maybeCreate(GMD_GROUP_NAME).apply {
                    devices.forEach { device ->
                        targetDevices.add(localDevices[device.name])
                    }
                }
            }
        }
    }
}

private fun NamedDomainObjectContainer<ManagedVirtualDevice>.create(config: DeviceConfig) {
    maybeCreate(config.name).apply {
        device = config.device
        apiLevel = config.apiLevel
        systemImageSource = config.systemImageSource
    }
}

private data class DeviceConfig(val device: String, val apiLevel: Int, val systemImageSource: String) {
    val name = buildString {
        append(device.lowercase().replace(" ", ""))
        append("api")
        append(apiLevel.toString())
        append(systemImageSource.split("-").first())
    }
}
