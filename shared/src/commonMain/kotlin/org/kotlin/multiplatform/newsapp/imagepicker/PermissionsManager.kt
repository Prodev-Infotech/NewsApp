package org.kotlin.multiplatform.newsapp.imagepicker

expect class PermissionsManager {
    suspend fun checkPermission(permission: PermissionType): PermissionStatus
    suspend fun requestPermission(permission: PermissionType): PermissionStatus
    suspend fun requestMultiplePermissions(permissions: List<PermissionType>): Map<PermissionType, PermissionStatus>
    fun openAppSettings()
}

expect fun createPermissionsManager(): PermissionsManager