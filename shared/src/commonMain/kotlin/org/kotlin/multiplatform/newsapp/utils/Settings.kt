package org.kotlin.multiplatform.newsapp.utils

import com.russhwolf.settings.Settings
import androidx.compose.ui.graphics.Color


expect val settings: Settings

expect class BookmarksUtil() {
    fun getBookmarks(): Set<String>
    fun saveBookmarks(bookmarks: Set<String>)
    fun toggleBookmark(newsId: String, isBookmarked: Boolean)
}

expect fun shareNews(title: String, details: String, link: String)

expect fun setStatusBarColor(color: Color)