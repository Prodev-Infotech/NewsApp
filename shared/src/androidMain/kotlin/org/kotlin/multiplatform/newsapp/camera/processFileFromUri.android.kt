package org.kotlin.multiplatform.newsapp.camera

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File

private lateinit var appContext: Context

fun initializeContext(context: Context) {
    appContext = context.applicationContext
}

actual suspend fun getImageDetailsFromUri(uri: String): Pair<String?, ByteArray?> {
    val parsedUri = Uri.parse(uri)

    val fileName = if (parsedUri.scheme == "content") {
        // For content URIs
        appContext.contentResolver.query(parsedUri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst()) cursor.getString(nameIndex) else null
        }
    } else {
        // For file URIs
        File(parsedUri.path ?: "").name
    }

    val byteArray = if (parsedUri.scheme == "content") {
        appContext.contentResolver.openInputStream(parsedUri)?.use { it.readBytes() }
    } else {
        File(parsedUri.path ?: "").takeIf { it.exists() }?.readBytes()
    }

    return fileName to byteArray
}