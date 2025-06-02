package org.kotlin.multiplatform.newsapp.imagepicker

actual class PermissionsManager {
    actual suspend fun checkPermission(permission: PermissionType): PermissionStatus {
        TODO("Not yet implemented")
    }

    actual suspend fun requestPermission(permission: PermissionType): PermissionStatus {
        TODO("Not yet implemented")
    }

    actual suspend fun requestMultiplePermissions(permissions: List<PermissionType>): Map<PermissionType, PermissionStatus> {
        TODO("Not yet implemented")
    }

    actual fun openAppSettings() {
    }
}

actual fun createPermissionsManager(): PermissionsManager {
    TODO("Not yet implemented")
}