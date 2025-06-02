package org.kotlin.multiplatform.newsapp.camera

actual class CameraManager {
    actual suspend fun takePicture(): CameraResult {
        TODO("Not yet implemented")
    }

    actual suspend fun pickFromGallery(): CameraResult {
        TODO("Not yet implemented")
    }

    actual suspend fun showImagePicker(): CameraResult {
        TODO("Not yet implemented")
    }

    actual fun isAvailable(): Boolean {
        TODO("Not yet implemented")
    }
}

actual fun createCameraManager(): CameraManager {
    TODO("Not yet implemented")
}