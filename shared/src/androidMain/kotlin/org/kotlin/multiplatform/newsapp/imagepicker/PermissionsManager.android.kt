package org.kotlin.multiplatform.newsapp.imagepicker

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

actual class PermissionsManager(private val activity: ComponentActivity) {

    private val permissionMap = mapOf(
        PermissionType.CAMERA to Manifest.permission.CAMERA,
        PermissionType.GALLERY to if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        },
        PermissionType.STORAGE to if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    )

    private var singlePermissionContinuation: CancellableContinuation<PermissionStatus>? = null
    private var multiplePermissionsContinuation: CancellableContinuation<Map<PermissionType, PermissionStatus>>? = null

    private val requestSinglePermissionLauncher =
        activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            val continuation = singlePermissionContinuation ?: return@registerForActivityResult
            val permission = currentRequestedPermission ?: return@registerForActivityResult

            val status = when {
                isGranted -> PermissionStatus.GRANTED
                ActivityCompat.shouldShowRequestPermissionRationale(activity, permissionMap[permission]!!) -> PermissionStatus.DENIED
                else -> PermissionStatus.PERMANENTLY_DENIED
            }

            continuation.resume(status)
            singlePermissionContinuation = null
            currentRequestedPermission = null
        }

    private val requestMultiplePermissionsLauncher =
        activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
            val continuation = multiplePermissionsContinuation ?: return@registerForActivityResult
            val statusMap = mutableMapOf<PermissionType, PermissionStatus>()

            results.forEach { (androidPermission, isGranted) ->
                val permissionType = permissionMap.entries.find { it.value == androidPermission }?.key ?: return@forEach
                val status = when {
                    isGranted -> PermissionStatus.GRANTED
                    ActivityCompat.shouldShowRequestPermissionRationale(activity, androidPermission) -> PermissionStatus.DENIED
                    else -> PermissionStatus.PERMANENTLY_DENIED
                }
                statusMap[permissionType] = status
            }

            continuation.resume(statusMap)
            multiplePermissionsContinuation = null
        }

    private var currentRequestedPermission: PermissionType? = null

    actual suspend fun checkPermission(permission: PermissionType): PermissionStatus {
        val androidPermission = permissionMap[permission] ?: return PermissionStatus.DENIED

        return when {
            ContextCompat.checkSelfPermission(activity, androidPermission) == PackageManager.PERMISSION_GRANTED -> {
                PermissionStatus.GRANTED
            }
            ActivityCompat.shouldShowRequestPermissionRationale(activity, androidPermission) -> {
                PermissionStatus.DENIED
            }
            else -> {
                PermissionStatus.NOT_DETERMINED
            }
        }
    }

    actual suspend fun requestPermission(permission: PermissionType): PermissionStatus {
        val androidPermission = permissionMap[permission] ?: return PermissionStatus.DENIED

        if (ContextCompat.checkSelfPermission(activity, androidPermission) == PackageManager.PERMISSION_GRANTED) {
            return PermissionStatus.GRANTED
        }

        return suspendCancellableCoroutine { continuation ->
            currentRequestedPermission = permission
            singlePermissionContinuation = continuation
            requestSinglePermissionLauncher.launch(androidPermission)
        }
    }

    actual suspend fun requestMultiplePermissions(permissions: List<PermissionType>): Map<PermissionType, PermissionStatus> {
        val androidPermissions = permissions.mapNotNull { permissionMap[it] }.toTypedArray()

        if (androidPermissions.isEmpty()) {
            return permissions.associateWith { PermissionStatus.DENIED }
        }

        return suspendCancellableCoroutine { continuation ->
            multiplePermissionsContinuation = continuation
            requestMultiplePermissionsLauncher.launch(androidPermissions)
        }
    }

    actual fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", activity.packageName, null)
        }
        activity.startActivity(intent)
    }
}


actual fun createPermissionsManager(): PermissionsManager {
    // This function should be called with the current activity
    // You'll need to pass the activity when calling this function
    throw IllegalStateException("Use createPermissionsManager(activity: ComponentActivity) instead")
}

// Helper function for Android
fun createPermissionsManager(activity: ComponentActivity): PermissionsManager {
    return PermissionsManager(activity)
}