package org.kotlin.multiplatform.newsapp.imagepicker

enum class PermissionStatus {
    GRANTED,
    DENIED,
    NOT_DETERMINED,
    PERMANENTLY_DENIED
}

enum class PermissionType {
    CAMERA,
    GALLERY,
    STORAGE
}