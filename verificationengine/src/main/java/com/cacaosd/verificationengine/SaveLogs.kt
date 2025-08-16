package com.cacaosd.verificationengine

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.IOException

fun savePublicFile(context: Context, fileName: String, content: String) {
    val values = ContentValues().apply {
        put(MediaStore.Downloads.DISPLAY_NAME, fileName)
        put(MediaStore.Downloads.MIME_TYPE, "text/plain")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }
    }

    val resolver = context.contentResolver
    val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
    } else {
        TODO("VERSION.SDK_INT < Q")
    }

    if (uri != null) {
        try {
            resolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(content.toByteArray())
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}