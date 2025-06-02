package org.kotlin.multiplatform.newsapp.camera

import androidx.compose.runtime.Composable
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitType

expect fun uriToPlatformFile(uri: String): PlatformFile?

