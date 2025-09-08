package com.cacaosd.droidmind.adb.device_controller

data class DeviceInfo(
    val name: String,
    val serial: String,
    val batteryLevel: Int,
    val osVersion: String,
    val dimensions: String? = null
)
