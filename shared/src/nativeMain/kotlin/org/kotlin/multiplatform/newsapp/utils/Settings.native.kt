package org.kotlin.multiplatform.newsapp.utils

import platform.Foundation.NSUserDefaults

actual class BookmarksUtil actual constructor() {

    private val userDefaults = NSUserDefaults.standardUserDefaults()

    // Get the list of bookmarked news items from UserDefaults
    actual fun getBookmarks(): Set<String> {
        val bookmarksList = userDefaults.stringArrayForKey("bookmarks")?.filterIsInstance<String>() ?: emptyList()
        return bookmarksList.toSet()

    }

    // Save the set of bookmarked items to UserDefaults
    actual fun saveBookmarks(bookmarks: Set<String>) {
        userDefaults.setObject(bookmarks.toList(), forKey = "bookmarks")
    }

    // Toggle bookmark status for a specific news item
    actual fun toggleBookmark(newsId: String, isBookmarked: Boolean) {
        val bookmarks = getBookmarks().toMutableSet()
        if (isBookmarked) {
            bookmarks.remove(newsId) // Remove bookmark if it's already bookmarked
        } else {
            bookmarks.add(newsId) // Add bookmark if not already bookmarked
        }
        saveBookmarks(bookmarks)
    }
}

actual fun shareNews(title: String, details: String, link: String) {
}