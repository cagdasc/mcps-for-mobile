package com.cacaosd.mcps_for_mobile

import java.io.File
import java.io.FileInputStream
import java.util.*

private fun findFileUpwards(name: String, start: File = File("").absoluteFile): File? {
    var current: File? = start
    while (current != null) {
        val candidate = File(current, name)
        if (candidate.exists()) return candidate
        current = current.parentFile
    }
    return null
}

val localProperties: Properties
    get() {
        val properties = Properties()
        val localPropertiesFile = findFileUpwards("local.properties")
            ?: error("local.properties not found in any parent directory")

        try {
            FileInputStream(localPropertiesFile).use { inputStream ->
                properties.load(inputStream)
            }
        } catch (e: Exception) {
            error("Error loading local.properties: ${e.message}")
        }

        return properties
    }
