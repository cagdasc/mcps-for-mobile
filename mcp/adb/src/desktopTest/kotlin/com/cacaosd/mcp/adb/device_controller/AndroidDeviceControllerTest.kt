package com.cacaosd.mcp.adb.device_controller

import com.cacaosd.mcp.adb.AppConfigManager
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class AndroidDeviceControllerTest {

    @Test
    fun testListInstalledPackagesReturnsPackages(): Unit = runBlocking {
        val androidDeviceController =
            getAndroidDeviceController(
                AppConfigManager(
                    appName = "mcpformobile",
                    appVersion = "0.0.1"
                )
            ) as AndroidDeviceController
        val listInstalledPackages = androidDeviceController.getInstalledPackages("emulator-5554")
        listInstalledPackages.forEach {
            val appLabel = androidDeviceController.getAppLabel("emulator-5554", it)
            println(appLabel)
        }

    }

}
