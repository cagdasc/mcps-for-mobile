package com.cacaosd.droidmind.adb.device_controller

import com.android.ddmlib.AndroidDebugBridge
import com.android.ddmlib.CollectingOutputReceiver
import com.android.ddmlib.IDevice
import com.cacaosd.droidmind.adb.AppConfigManager
import com.cacaosd.droidmind.adb.layout_optimizer.LayoutOptimiser
import com.cacaosd.droidmind.adb.layout_optimizer.getLayoutOptimiser
import com.cacaosd.droidmind.shared.extension.asFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
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

    override suspend fun getDevices(): List<DeviceInfo> {
        return adb.devices.map { device ->
            val batteryLevel = device.battery.asFlow().catch { emit(-1) }.firstOrNull() ?: -1

            DeviceInfo(
                name = device.name,
                serial = device.serialNumber,
                batteryLevel = batteryLevel,
                osVersion = device.version.apiStringWithExtension,
                dimensions = getDimensions(device.serialNumber)
            )
        }
    }

    override suspend fun listInstalledPackages(serial: String?): List<String> = withContext(Dispatchers.IO) {
        val device = getDevice(serial) ?: return@withContext emptyList()
        val shellOutputReceiver = CollectingReceiver()
        device.executeShellCommand("pm list packages", shellOutputReceiver)
        shellOutputReceiver.resultLines.map { it.removePrefix("package:") }
    }

    fun getInstalledPackages(serial: String?): List<String> {
        val device = getDevice(serial) ?: return emptyList()

        val receiver = CollectingOutputReceiver()
        device.executeShellCommand("pm list packages", receiver)
        val output = receiver.output
        return output
            .split("\n")
            .filter { it.startsWith("package:") }
            .map { it.removePrefix("package:").trim() }
    }

    fun getAppLabel(serial: String?, packageName: String): String? {
        val device = getDevice(serial) ?: return null
        val receiver = CollectingOutputReceiver()
        device.executeShellCommand("dumpsys package $packageName", receiver)
        val output = receiver.output

        // This will try to find the app label (name) in the dumpsys output
        val labelLine = output.lines().find { it.contains("application-label:") }
        return labelLine?.substringAfter("application-label:")?.trim()
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

    private suspend fun getDimensions(serial: String?): String? {
        val deviceSize = deviceSize(serial)
        val regex = Regex("""\b(\d+x\d+)\b""")
        val match = regex.find(deviceSize)
        return match?.groups?.get(1)?.value
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

    override suspend fun enableAccessibilityService(serial: String?): Boolean = withContext(Dispatchers.IO) {
        val device = getDevice(serial) ?: return@withContext false
        val service = "com.cacaosd.interaction_engine/com.cacaosd.interaction_engine.service.InteractionTrackingService"
        device.executeShellCommand(
            "settings put secure enabled_accessibility_services $service",
            CollectingReceiver()
        )
        delay(100)
        val receiver = CollectingOutputReceiver()
        device.executeShellCommand("settings get secure enabled_accessibility_services", receiver)

        val result = receiver.output.trim()
        return@withContext result.isNotEmpty() || result == service
    }

    override suspend fun disableAccessibilityService(serial: String?): Boolean = withContext(Dispatchers.IO) {
        val device = getDevice(serial) ?: return@withContext false

        val service = "com.cacaosd.interaction_engine/com.cacaosd.interaction_engine.service.InteractionTrackingService"

        val cmd = """
            U=$(cmd activity get-current-user)
            CUR=$(settings get secure --user ${'$'}U enabled_accessibility_services | tr -d '\r')
            NEW=""
            for S in $(echo "${'$'}CUR" | tr ":" " "); do
            [ "${'$'}S" != "$service" ] && NEW="${'$'}{NEW:+${'$'}NEW:}${'$'}S"
            done
            settings put secure --user ${'$'}U enabled_accessibility_services "${'$'}NEW"
            if [ -z "${'$'}NEW" ] || [ "${'$'}NEW" = "null" ]; then
              settings put secure --user ${'$'}U accessibility_enabled 0
            fi
            """.trimIndent()

        device.executeShellCommand(cmd, CollectingReceiver())
        delay(100)
        val receiver = CollectingOutputReceiver()
        device.executeShellCommand("settings get secure enabled_accessibility_services", receiver)

        val result = receiver.output.trim()
        return@withContext result.isEmpty()

    }

    override suspend fun sendData(serial: String?, values: Map<String, String>) {
        val device = getDevice(serial) ?: error("Device not found")
        val destination = "com.cacaosd.interaction_engine.INTERACTION_EVENT"
        val parameters = buildString {
            values.forEach {
                append("--es ${it.key} ${it.value} ")
            }
        }

        device.executeShellCommand("am broadcast -a $destination $parameters", CollectingReceiver())
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
