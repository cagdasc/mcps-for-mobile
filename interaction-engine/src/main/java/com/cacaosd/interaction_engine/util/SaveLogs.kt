package com.cacaosd.interaction_engine.util

import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import java.io.IOException

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
