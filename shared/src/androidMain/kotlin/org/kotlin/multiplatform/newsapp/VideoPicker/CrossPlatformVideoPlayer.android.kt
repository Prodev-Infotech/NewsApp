package org.kotlin.multiplatform.newsapp.VideoPicker

import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView

@OptIn(UnstableApi::class)
@Composable
actual fun VideoPlayer(modifier: Modifier, url: String) {

    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_ONE // ✅ Loop the current video

            val mediaItem = if (url.startsWith("content://")) {
                MediaItem.Builder()
                    .setUri(Uri.parse(url))
                    .setMimeType(MimeTypes.VIDEO_MP4) // Adjust mime type accordingly
                    .build()
            } else {
                MediaItem.fromUri(url)
            }
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(9f / 16f), // Adjust this to match your video ratio
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
                useController = true
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                setShowFastForwardButton(false)
                setShowRewindButton(false)
                setShowNextButton(false)
                setShowPreviousButton(false)
                setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING)
                controllerAutoShow = true
                controllerShowTimeoutMs = 3000
            }
        }
    )
}