package com.cacaosd.mcp.adb

import java.io.File

// Just support for linux for now
internal fun getMcpAdbPath(): String {
    val homeDir = System.getProperty("user.home")
    return "$homeDir/.mcp/adb/"
}

fun initConfigDirectory() {
    val configDir = getMcpAdbPath()
    val configDirFile = File(configDir)
    if (!configDirFile.exists()) {
        configDirFile.mkdirs()
    }
}