package org.kotlin.multiplatform.newsapp.camera

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitType
import java.io.File

actual fun uriToPlatformFile(uri: String): PlatformFile? {
    TODO("Not yet implemented")
}