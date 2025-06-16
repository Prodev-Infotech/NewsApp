package org.kotlin.multiplatform.newsapp.utils

import org.kotlin.multiplatform.newsapp.camera.CameraManager
import org.kotlin.multiplatform.newsapp.camera.CameraResult
import org.kotlin.multiplatform.newsapp.imagepicker.PermissionStatus
import org.kotlin.multiplatform.newsapp.imagepicker.PermissionType
import org.kotlin.multiplatform.newsapp.imagepicker.PermissionsManager

data class VideoFile(
    val path: String,
    val name: String,
    val size: Long,
    val mimeType: String,
    val duration: Long? = null
)

class ImagePickerManager(
    private val cameraManager: CameraManager,
    private val permissionsManager: PermissionsManager
) {


suspend fun takePhoto(): CameraResult {
    println("ImagePickerManager: Checking camera permission")

    val cameraPermission = permissionsManager.checkPermission(PermissionType.CAMERA)
    if (cameraPermission != PermissionStatus.GRANTED) {
        println("ImagePickerManager: Camera permission not granted, requesting permission...")
        val requestResult = permissionsManager.requestPermission(PermissionType.CAMERA)
        if (requestResult != PermissionStatus.GRANTED) {
            println("ImagePickerManager: Camera permission denied after request")
            return CameraResult(error = "Camera permission denied")
        }
    }

    println("ImagePickerManager: Permission granted, launching camera...")
    val result = cameraManager.takePicture()
    println("ImagePickerManager: Received result from camera: $result")

    return result
}
    suspend fun pickFromGallery(): CameraResult {
        // Check and request gallery permission
        val galleryPermission = permissionsManager.checkPermission(PermissionType.GALLERY)

        if (galleryPermission != PermissionStatus.GRANTED) {
            val requestResult = permissionsManager.requestPermission(PermissionType.GALLERY)
            if (requestResult != PermissionStatus.GRANTED) {
                return CameraResult(error = "Gallery permission denied")
            }
        }

        return cameraManager.pickFromGallery()
    }

    suspend fun showImagePicker(): CameraResult {
        // Request both permissions
        val permissions = listOf(PermissionType.CAMERA, PermissionType.GALLERY)
        val results = permissionsManager.requestMultiplePermissions(permissions)

        val cameraGranted = results[PermissionType.CAMERA] == PermissionStatus.GRANTED
        val galleryGranted = results[PermissionType.GALLERY] == PermissionStatus.GRANTED

        if (!cameraGranted && !galleryGranted) {
            println("Picker Permissions denied")
            return CameraResult(error = "Camera and Gallery permissions denied")
        }

        return cameraManager.showImagePicker()
    }

}