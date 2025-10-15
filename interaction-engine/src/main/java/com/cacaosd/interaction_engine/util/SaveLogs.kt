package com.cacaosd.interaction_engine.util

import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import java.io.IOException
import java.io.OutputStream

fun Context.saveStringToDownloads(fileName: String, content: String) {
    val values = ContentValues().apply {
        put(MediaStore.Downloads.DISPLAY_NAME, fileName)
        put(MediaStore.Downloads.MIME_TYPE, "application/json")
        put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
    }

    val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
        ?: error("Failed to create new MediaStore record")

    try {
        contentResolver.openOutputStream(uri)?.use { outputStream ->
            outputStream.write(content.toByteArray())
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

fun Context.saveToDownloads(fileName: String, outputStreamBlock: (OutputStream) -> Unit) {
    val mime = when {
        fileName.endsWith(".json", ignoreCase = true) -> "application/json"
        fileName.endsWith(".xml", ignoreCase = true) -> "application/xml"
        else -> error("Unsupported file extension")
    }

    val values = ContentValues().apply {
        put(MediaStore.Downloads.DISPLAY_NAME, fileName)
        put(MediaStore.Downloads.MIME_TYPE, mime)
        put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
    }

    val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
        ?: error("Failed to create new MediaStore record")

    try {
        contentResolver.openOutputStream(uri)?.use { outputStream ->
            outputStreamBlock(outputStream)
        } ?: throw IOException("Failed to open output stream")
    } catch (e: IOException) {
        e.printStackTrace()
        try {
            contentResolver.delete(uri, null, null)
        } catch (_: Exception) { /* ignore cleanup errors */
        }
    }
}
