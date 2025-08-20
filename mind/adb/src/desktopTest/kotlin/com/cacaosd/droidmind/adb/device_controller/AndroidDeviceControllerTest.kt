package com.cacaosd.droidmind.adb.device_controller

import com.cacaosd.droidmind.adb.AppConfigManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds

class AndroidDeviceControllerTest {

    private val androidDeviceController = getAndroidDeviceController(
        AppConfigManager(
            appName = "mcpformobile",
            appVersion = "0.0.1"
        )
    ) as AndroidDeviceController

    @Test
    fun testListInstalledPackagesReturnsPackages(): Unit = runBlocking {
        val listInstalledPackages = androidDeviceController.getInstalledPackages("emulator-5554")
        listInstalledPackages.forEach {
            val appLabel = androidDeviceController.getAppLabel("emulator-5554", it)
            println(appLabel)
        }
    }

    @Test
    fun testEnableAccessibilityService(): Unit = runBlocking {
        val enableAccessibilityService = androidDeviceController.enableAccessibilityService("emulator-5554")
        println(enableAccessibilityService)
    }

    @Test
    fun testDisableAccessibilityService(): Unit = runBlocking {
        val disableAccessibilityService = androidDeviceController.disableAccessibilityService("emulator-5554")
        println(disableAccessibilityService)
    }

    @Test
    fun startInteractionFor20Seconds() = runBlocking {
        val enableAccessibilityService = androidDeviceController.enableAccessibilityService("emulator-5554")
        println("Accessibility Service Enabled: $enableAccessibilityService")

        androidDeviceController.sendData(
            "emulator-5554", mapOf(
                "INTERACTION_EVENT" to "start_recording",
                "APP_PACKAGE" to "com.google.android.youtube"
            )
        )

        delay(20.seconds)

        androidDeviceController.sendData(
            "emulator-5554", mapOf(
                "INTERACTION_EVENT" to "stop_recording",
                "APP_PACKAGE" to "com.google.android.youtube"
            )
        )

        val disableAccessibilityService = androidDeviceController.disableAccessibilityService("emulator-5554")
        println("Accessibility Service Disabled: $disableAccessibilityService")
    }
}
