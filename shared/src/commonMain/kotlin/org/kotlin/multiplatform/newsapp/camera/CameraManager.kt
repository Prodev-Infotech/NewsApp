package org.kotlin.multiplatform.newsapp.camera

data class CameraResult(
    val imageUri: String? = null,
    val error: String? = null,
    val isCancelled: Boolean = false
)

enum class CameraSource {
    CAMERA,
    GALLERY
}

expect class CameraManager {
    suspend fun takePicture(): CameraResult
    suspend fun pickFromGallery(): CameraResult
    suspend fun showImagePicker(): CameraResult // Shows options for camera or gallery
    fun isAvailable(): Boolean
}

expect fun createCameraManager(): CameraManager