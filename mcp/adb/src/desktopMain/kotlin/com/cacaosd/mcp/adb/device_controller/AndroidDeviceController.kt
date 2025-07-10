package com.cacaosd.mcp.adb.device_controller

import com.android.ddmlib.AndroidDebugBridge
import com.android.ddmlib.IDevice
import com.cacaosd.mcp.adb.AppConfigManager
import com.cacaosd.mcp.adb.layout_optimizer.LayoutOptimiser
import com.cacaosd.mcp.adb.layout_optimizer.getLayoutOptimiser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

actual fun getAndroidDeviceController(appConfigManager: AppConfigManager): DeviceController =
    AndroidDeviceController(
        adb = getAdb(),
        layoutOptimiser = getLayoutOptimiser(),
        appConfigManager = appConfigManager
    )

class AndroidDeviceController(
    private val adb: AndroidDebugBridge,
    private val layoutOptimiser: LayoutOptimiser,
    private val appConfigManager: AppConfigManager
) : DeviceController {

    override suspend fun listConnectedDevices(): List<String> = withContext(Dispatchers.IO) {
        adb.devices.map { "Name: ${it.name}, Serial: ${it.serialNumber}" }
    }

    override suspend fun listInstalledPackages(serial: String?): List<String> = withContext(Dispatchers.IO) {
        val device = getDevice(serial) ?: return@withContext emptyList()
        val shellOutputReceiver = CollectingReceiver()
        device.executeShellCommand("pm list packages", shellOutputReceiver)
        shellOutputReceiver.resultLines.map { it.removePrefix("package:") }
    }

    override suspend fun launchApp(packageName: String, serial: String?): String = withContext(Dispatchers.IO) {
        val device = getDevice(serial) ?: return@withContext "Device not found"
        val cmd = "monkey -p $packageName -c android.intent.category.LAUNCHER 1"
        val receiver = CollectingReceiver()
        device.executeShellCommand(cmd, receiver)
        "Launched $packageName"
    }

    override suspend fun getUiDump(serial: String?): String = withContext(Dispatchers.IO) {
        val device = getDevice(serial) ?: return@withContext "Device not found"

        val timestamp = Clock.System.now().epochSeconds
        val xmlName = "uidump_$timestamp.xml"
        val remotePath = "/sdcard/$xmlName"

        device.executeShellCommand("uiautomator dump $remotePath", CollectingReceiver())

        val localDumpFile = appConfigManager.getUiDumpFile(filename = xmlName).toFile()
        device.pullFile(remotePath, localDumpFile.absolutePath)
        device.executeShellCommand("rm $remotePath", CollectingReceiver())

        // TODO: Return structured format
        layoutOptimiser.optimise(localDumpFile).toString()
    }

    override suspend fun inputText(text: String, serial: String?): String = withContext(Dispatchers.IO) {
        val device = getDevice(serial) ?: return@withContext "Device not found"
        val cmd = "input text '${text.replace(" ", "%s")}'"
        device.executeShellCommand(cmd, CollectingReceiver())
        "Input sent: $text"
    }

    override suspend fun tap(x: Int, y: Int, serial: String?): String = withContext(Dispatchers.IO) {
        val device = getDevice(serial) ?: return@withContext "Device not found"
        device.executeShellCommand("input tap $x $y", CollectingReceiver())
        "Tapped at ($x, $y)"
    }

    override suspend fun sendKeyEvent(key: String, serial: String?): String = withContext(Dispatchers.IO) {
        val device = getDevice(serial) ?: return@withContext "Device not found"
        val keyCode = keyEventMap[key.lowercase()] ?: return@withContext "Unsupported key: $key"
        device.executeShellCommand("input keyevent $keyCode", CollectingReceiver())
        "Sent key event: $key"
    }

    override suspend fun deviceSize(serial: String?): String = withContext(Dispatchers.IO) {
        val device = getDevice(serial) ?: return@withContext "Device not found"
        val receiver = CollectingReceiver()
        device.executeShellCommand("wm size", receiver)
        receiver.result
    }

    override suspend fun screenshot(serial: String?): String = withContext(Dispatchers.IO) {
        val device = getDevice(serial) ?: return@withContext "Device not found"
        val screenshotsPath = appConfigManager.screenshotsDir.toAbsolutePath().toString()
        val timestamp = Clock.System.now().epochSeconds

        device.executeShellCommand(
            "screencap -p /sdcard/Pictures/${timestamp}.png",
            CollectingReceiver()
        )
        delay(200)
        device.pullFile("/sdcard/Pictures/${timestamp}.png", "$screenshotsPath/${timestamp}.png")
        delay(200)
        device.executeShellCommand(
            "rm /sdcard/Pictures/${timestamp}.png",
            CollectingReceiver()
        )

        "Screenshot file name is ${timestamp}.png"
    }

    override suspend fun swipe(
        startX: Int,
        startY: Int,
        endX: Int,
        endY: Int,
        durationMs: Long,
        serial: String?
    ): String = withContext(Dispatchers.IO) {
        val device = getDevice(serial) ?: return@withContext "Device not found"
        device.executeShellCommand("input swipe $startX $startY $endX $endY $durationMs", CollectingReceiver())
        "Scroll"
    }

    private val keyEventMap = mapOf(
        "home" to 3,
        "back" to 4,
        "menu" to 82,
        "search" to 84,
        "enter" to 66,
        "done" to 66,
        "next" to 61,
        "del" to 67,
        "space" to 62,
        "tab" to 61,
        "up" to 19,
        "down" to 20,
        "left" to 21,
        "right" to 22
    )

    private fun getDevice(serial: String?): IDevice? {
        val devices = adb.devices
        return when {
            serial == null && devices.size > 1 ->
                error("Multiple devices connected. Please specify a serial number.")

            serial == null -> devices.firstOrNull()
            else -> devices.find { it.serialNumber == serial }
        }
    }
}
