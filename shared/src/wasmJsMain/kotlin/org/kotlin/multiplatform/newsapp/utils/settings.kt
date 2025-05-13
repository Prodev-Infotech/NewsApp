package org.kotlin.multiplatform.newsapp.utils

import com.russhwolf.settings.Settings
import com.russhwolf.settings.StorageSettings

actual val settings: Settings by lazy {
    StorageSettings() // uses window.localStorage
}