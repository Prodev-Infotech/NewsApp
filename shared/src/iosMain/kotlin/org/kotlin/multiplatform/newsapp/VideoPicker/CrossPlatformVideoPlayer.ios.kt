package org.kotlin.multiplatform.newsapp.VideoPicker

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun CrossPlatformVideo() {
}

@Composable
actual fun VideoPlayer(modifier: Modifier, url: String) {
    UIViewPlayerView(url, modifier) // You define this in UIKit via interop
}