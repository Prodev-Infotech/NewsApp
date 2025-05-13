package org.kotlin.multiplatform.newsapp.utils

import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import platform.Foundation.NSUserDefaults

actual val settings: Settings by lazy {
    // iOS-specific implementation using NSUserDefaults
    NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults())
}
