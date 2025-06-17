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
}

expect fun getDeviceControllerTools(): DeviceControllerTools
