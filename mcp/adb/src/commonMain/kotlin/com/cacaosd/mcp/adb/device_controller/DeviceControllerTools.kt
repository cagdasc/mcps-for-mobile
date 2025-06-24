package com.cacaosd.mcp.adb.device_controller

import ai.koog.agents.core.tools.reflect.ToolSet

interface DeviceControllerTools : ToolSet {

    suspend fun listConnectedDevices(): List<String>

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

expect fun getAdbDeviceControllerTools(): DeviceControllerTools
