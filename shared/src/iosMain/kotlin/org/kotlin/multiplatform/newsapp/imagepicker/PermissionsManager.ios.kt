package org.kotlin.multiplatform.newsapp.imagepicker

import androidx.compose.runtime.Composable
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.AVFoundation.AVAuthorizationStatus
import platform.AVFoundation.AVAuthorizationStatusAuthorized
import platform.AVFoundation.AVAuthorizationStatusDenied
import platform.AVFoundation.AVAuthorizationStatusNotDetermined
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.authorizationStatusForMediaType
import platform.AVFoundation.requestAccessForMediaType
import platform.Foundation.NSURL
import platform.Photos.PHAuthorizationStatus
import platform.Photos.PHAuthorizationStatusAuthorized
import platform.Photos.PHAuthorizationStatusDenied
import platform.Photos.PHAuthorizationStatusNotDetermined
import platform.Photos.PHPhotoLibrary
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString

actual class PermissionsManager {

    actual suspend fun checkPermission(permission: PermissionType): PermissionStatus {
        return when (permission) {
            PermissionType.CAMERA -> checkCameraPermission()
            PermissionType.GALLERY, PermissionType.STORAGE -> checkPhotoLibraryPermission()
        }
    }

    actual suspend fun requestPermission(permission: PermissionType): PermissionStatus {
        return when (permission) {
            PermissionType.CAMERA -> requestCameraPermission()
            PermissionType.GALLERY, PermissionType.STORAGE -> requestPhotoLibraryPermission()
        }
    }

    actual suspend fun requestMultiplePermissions(permissions: List<PermissionType>): Map<PermissionType, PermissionStatus> {
        val result = mutableMapOf<PermissionType, PermissionStatus>()

        permissions.forEach { permission ->
            result[permission] = requestPermission(permission)
        }

        return result
    }

    actual fun openAppSettings() {
        val settingsUrl = NSURL.URLWithString(UIApplicationOpenSettingsURLString)
        settingsUrl?.let {
            UIApplication.sharedApplication.openURL(it)
        }
    }

    private fun checkCameraPermission(): PermissionStatus {
        return when (AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)) {
//            AVAuthorizationStatus.AVAuthorizationStatusAuthorized -> PermissionStatus.GRANTED
//            AVAuthorizationStatus.AVAuthorizationStatusDenied -> PermissionStatus.PERMANENTLY_DENIED
//            AVAuthorizationStatus.AVAuthorizationStatusRestricted -> PermissionStatus.DENIED
//            AVAuthorizationStatus.AVAuthorizationStatusNotDetermined -> PermissionStatus.NOT_DETERMINED
            else -> PermissionStatus.NOT_DETERMINED
        }
    }

    private suspend fun requestCameraPermission(): PermissionStatus {
        if (checkCameraPermission() == PermissionStatus.GRANTED) {
            return PermissionStatus.GRANTED
        }

        return suspendCancellableCoroutine { continuation ->
            AVCaptureDevice.requestAccessForMediaType(AVMediaTypeVideo) { granted ->
                val status = if (granted) {
                    PermissionStatus.GRANTED
                } else {
                    when (AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)) {
//                        AVAuthorizationStatus.AVAuthorizationStatusDenied -> PermissionStatus.PERMANENTLY_DENIED
                        else -> PermissionStatus.DENIED
                    }
                }
//                continuation.resume(status)
            }
        }
    }

    private fun checkPhotoLibraryPermission(): PermissionStatus {
        return when (PHPhotoLibrary.authorizationStatus()) {
//            PHAuthorizationStatus.PHAuthorizationStatusAuthorized -> PermissionStatus.GRANTED
//            PHAuthorizationStatus.PHAuthorizationStatusLimited -> PermissionStatus.GRANTED
//            PHAuthorizationStatus.PHAuthorizationStatusDenied -> PermissionStatus.PERMANENTLY_DENIED
//            PHAuthorizationStatus.PHAuthorizationStatusRestricted -> PermissionStatus.DENIED
//            PHAuthorizationStatus.PHAuthorizationStatusNotDetermined -> PermissionStatus.NOT_DETERMINED
            else -> PermissionStatus.NOT_DETERMINED
        }
    }

    private suspend fun requestPhotoLibraryPermission(): PermissionStatus {
        if (checkPhotoLibraryPermission() == PermissionStatus.GRANTED) {
            return PermissionStatus.GRANTED
        }

        return suspendCancellableCoroutine { continuation ->
            PHPhotoLibrary.requestAuthorization { status ->
                val permissionStatus = when (status) {
//                    PHAuthorizationStatus.PHAuthorizationStatusAuthorized,
//                    PHAuthorizationStatus.PHAuthorizationStatusLimited -> PermissionStatus.GRANTED
//                    PHAuthorizationStatus.PHAuthorizationStatusDenied -> PermissionStatus.PERMANENTLY_DENIED
                    else -> PermissionStatus.DENIED
                }
//                continuation.resume(permissionStatus)
            }
        }
    }
}

actual fun createPermissionsManager(): PermissionsManager {
    return PermissionsManager()
}