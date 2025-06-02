package org.kotlin.multiplatform.newsapp.camera

import androidx.compose.runtime.Composable


expect suspend fun getImageDetailsFromUri(uri: String): Pair<String?, ByteArray?>
