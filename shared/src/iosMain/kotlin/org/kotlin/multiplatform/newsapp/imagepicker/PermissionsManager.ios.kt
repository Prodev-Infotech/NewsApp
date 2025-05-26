package org.kotlin.multiplatform.newsapp.imagepicker

import androidx.compose.runtime.Composable
import platform.AVFoundation.AVAuthorizationStatusAuthorized
import platform.AVFoundation.AVAuthorizationStatusDenied
import platform.AVFoundation.AVAuthorizationStatusNotDetermined
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.authorizationStatusForMediaType
import platform.AVFoundation.requestAccessForMediaType
import platform.Foundation.NSURL
import platform.Photos.PHAuthorizationStatusAuthorized
import platform.Photos.PHAuthorizationStatusDenied
import platform.Photos.PHAuthorizationStatusNotDetermined
import platform.Photos.PHPhotoLibrary
import platform.UIKit.UIApplicationOpenSettingsURLString

actual class PermissionsManager actual constructor(callback: PermissionCallback) :
    PermissionHandler {
    private val callback = callback

    @Composable
    override fun askPermission(permission: PermissionType) {
        when (permission) {
            PermissionType.CAMERA -> {
                val status = AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)
                when (status) {
                    AVAuthorizationStatusAuthorized -> callback.onPermissionStatus(permission, PermissionStatus.GRANTED)
                    AVAuthorizationStatusNotDetermined -> AVCaptureDevice.requestAccessForMediaType(AVMediaTypeVideo) { granted ->
                        if (granted) callback.onPermissionStatus(permission, PermissionStatus.GRANTED)
                        else callback.onPermissionStatus(permission, PermissionStatus.DENIED)
                    }
                    AVAuthorizationStatusDenied -> callback.onPermissionStatus(permission, PermissionStatus.DENIED)
                    else -> callback.onPermissionStatus(permission, PermissionStatus.DENIED)
                }
            }
            PermissionType.GALLERY -> {
                val status = PHPhotoLibrary.authorizationStatus()
                when (status) {
                    PHAuthorizationStatusAuthorized -> callback.onPermissionStatus(permission, PermissionStatus.GRANTED)
                    PHAuthorizationStatusNotDetermined -> PHPhotoLibrary.requestAuthorization { newStatus ->
                        // Recursive call to update after user decision
//                        askPermission(permission)
                    }
                    PHAuthorizationStatusDenied -> callback.onPermissionStatus(permission, PermissionStatus.DENIED)
                    else -> callback.onPermissionStatus(permission, PermissionStatus.DENIED)
                }
            }
        }
    }

    @Composable
    override fun isPermissionGranted(permission: PermissionType): Boolean {
        return when (permission) {
            PermissionType.CAMERA -> AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo) == AVAuthorizationStatusAuthorized
            PermissionType.GALLERY -> PHPhotoLibrary.authorizationStatus() == PHAuthorizationStatusAuthorized
        }
    }

    @Composable
    override fun launchSettings() {
        NSURL.URLWithString(UIApplicationOpenSettingsURLString)?.let {
//            UIApplication.sharedApplication.openURL(it)
        }
    }
}

@Composable
actual fun createPermissionsManager(callback: PermissionCallback): PermissionsManager {
    return PermissionsManager(callback)
}