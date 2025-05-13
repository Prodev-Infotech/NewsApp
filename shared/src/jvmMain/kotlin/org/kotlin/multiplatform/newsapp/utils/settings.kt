package org.kotlin.multiplatform.newsapp.utils

import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import java.util.prefs.Preferences

actual val settings: Settings by lazy {
    PreferencesSettings(Preferences.userRoot().node("MyApp"))
}

actual class BookmarksUtil actual constructor() {
    actual fun getBookmarks(): Set<String> {
        println("Not yet implemented")
        return emptySet()
    }

    actual fun saveBookmarks(bookmarks: Set<String>) {
    }

    actual fun toggleBookmark(newsId: String, isBookmarked: Boolean) {
    }
}