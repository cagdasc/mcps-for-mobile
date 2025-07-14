package com.cacaosd.mcp.adb.device_controller

import com.cacaosd.mcp.adb.AppConfigManager

interface DeviceController {

    suspend fun getDevices(): List<DeviceInfo>

    suspend fun listInstalledPackages(serial: String?): List<String>

    suspend fun launchApp(packageName: String, serial: String?): String

    suspend fun getUiDump(serial: String?): String

    suspend fun inputText(text: String, serial: String?): String

    suspend fun tap(x: Int, y: Int, serial: String?): String

    suspend fun sendKeyEvent(key: String, serial: String?): String

    suspend fun deviceSize(serial: String?): String

    suspend fun screenshot(serial: String?): String

    suspend fun swipe(startX: Int, startY: Int, endX: Int, endY: Int, durationMs: Long = 300, serial: String?): String
}

expect fun getAndroidDeviceController(appConfigManager: AppConfigManager): DeviceController
