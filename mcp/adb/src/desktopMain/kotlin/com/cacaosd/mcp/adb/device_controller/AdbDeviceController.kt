package com.cacaosd.mcp.adb.device_controller

import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.agents.core.tools.annotations.Tool
import com.android.ddmlib.AndroidDebugBridge
import com.android.ddmlib.IDevice
import com.cacaosd.mcp.adb.AppConfigManager
import com.cacaosd.mcp.adb.layout_optimizer.LayoutOptimiser
import com.cacaosd.mcp.adb.layout_optimizer.getLayoutOptimiser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

actual fun getAdbDeviceControllerTools(): DeviceControllerTools =
    AdbDeviceController(
        adb = getAdb(), layoutOptimiser = getLayoutOptimiser(), appConfigManager = AppConfigManager(
            appName = "mcpformobile",
            appVersion = "0.0.1"
        )
    )

@LLMDescription(
    "Tools for control Android device via Android Device Bridge(adb) which is a " +
            "commandline executable to run commands for android devices"
)
class AdbDeviceController(
    private val adb: AndroidDebugBridge,
    private val layoutOptimiser: LayoutOptimiser,
    private val appConfigManager: AppConfigManager
) :
    DeviceControllerTools {

    init {
        // Initialize configuration
        if (appConfigManager.initialize()) {
            println("✅ Configuration initialized successfully")

            // Check if first run
            if (appConfigManager.isFirstRun()) {
                println("👋 Welcome! This is your first time running the app.")
                // Perform first-run setup
                appConfigManager.markFirstRunCompleted()
            }
        }
    }

    @Tool("list_connected_devices")
    @LLMDescription(
        "Retrieves the serial numbers of all Android devices and emulators currently connected " +
                "to the local development machine via ADB"
    )
    override suspend fun listConnectedDevices(): List<String> = withContext(Dispatchers.IO) {
        adb.devices.map { it.serialNumber }
    }

    @Tool("list_installed_packages")
    @LLMDescription("Retrieves all package names of applications installed on the specified device (defaults to the first connected device if omitted).")
    override suspend fun listInstalledPackages(serial: String?): List<String> = withContext(Dispatchers.IO) {
        val device = getDevice(serial) ?: return@withContext emptyList()
        val shellOutputReceiver = CollectingReceiver()
        device.executeShellCommand("pm list packages", shellOutputReceiver)
        shellOutputReceiver.resultLines.map { it.removePrefix("package:") }
    }

    @Tool("launch_app_by_package")
    @LLMDescription("Launches an Android app by its package name on the specified device.")
    override suspend fun launchApp(packageName: String, serial: String?): String = withContext(Dispatchers.IO) {
        val device = getDevice(serial) ?: return@withContext "Device not found"
        val cmd = "monkey -p $packageName -c android.intent.category.LAUNCHER 1"
        val receiver = CollectingReceiver()
        device.executeShellCommand(cmd, receiver)
        "Launched $packageName"
    }

    @Tool("get_ui_dump")
    @LLMDescription("Retrieves the current UI hierarchy (in XML) from the Android device.")
    override suspend fun getUiDump(serial: String?): String = withContext(Dispatchers.IO) {
        val device = getDevice(serial) ?: return@withContext "Device not found"

        val timestamp = Clock.System.now().epochSeconds
        val xmlName = "uidump_$timestamp.xml"
        val remotePath = "/sdcard/$xmlName"

        val dumpReceiver = CollectingReceiver()
        device.executeShellCommand("uiautomator dump $remotePath", dumpReceiver)

        val localDumpFile = appConfigManager.getUiDumpFile(filename = xmlName).toFile()
        device.pullFile(remotePath, localDumpFile.absolutePath)

        // TODO: Return structured format
        layoutOptimiser.optimise(localDumpFile).toString()
    }

    @Tool("input_text")
    @LLMDescription("Types and sends text input to the Android device using the ADB shell input command.")
    override suspend fun inputText(text: String, serial: String?): String = withContext(Dispatchers.IO) {
        val device = getDevice(serial) ?: return@withContext "Device not found"
        val cmd = "input text '${text.replace(" ", "%s")}'"
        device.executeShellCommand(cmd, CollectingReceiver())
        "Input sent: $text"
    }

    @Tool("tap")
    @LLMDescription("Simulates a tap at the specified (x, y) screen coordinates on the Android device.")
    override suspend fun tap(x: Int, y: Int, serial: String?): String = withContext(Dispatchers.IO) {
        val device = getDevice(serial) ?: return@withContext "Device not found"
        device.executeShellCommand("input tap $x $y", CollectingReceiver())
        "Tapped at ($x, $y)"
    }

    @Tool("send_key_event")
    @LLMDescription(
        "Sends a key event to the Android device. Supported keys include: home, back, menu, search, enter, done, next, del, space, tab, up, down, left, right."
    )
    override suspend fun sendKeyEvent(key: String, serial: String?): String = withContext(Dispatchers.IO) {
        val device = getDevice(serial) ?: return@withContext "Device not found"
        val keyCode = keyEventMap[key.lowercase()] ?: return@withContext "Unsupported key: $key"
        device.executeShellCommand("input keyevent $keyCode", CollectingReceiver())
        "Sent key event: $key"
    }

    @Tool("device_size")
    @LLMDescription("Get device size on the specified device.")
    override suspend fun deviceSize(serial: String?): String = withContext(Dispatchers.IO) {
        val device = getDevice(serial) ?: return@withContext "Device not found"
        val receiver = CollectingReceiver()
        device.executeShellCommand("wm size", receiver)
        receiver.result
    }

    @Tool("device_screenshot")
    @LLMDescription("It captures screenshot of current screen on Android device and save it to local development machine.")
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

    @Tool("swipe")
    @LLMDescription(
        " Simulates a continuous swipe (drag) gesture from the start coordinates to the end coordinates over the specified duration " +
                "if startX and startY difference is lesser than endX and endY which means you are vertıcally scrolling otherwıse horizontal scrolling. " +
                "You can use device_size to to make sure you are in bounds. Also you can use get_ui_dump to check are you in given scroll condition."
    )
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
        return if (serial == null && devices.isNotEmpty()) devices.first()
        else devices.find { it.serialNumber == serial }
    }
}
