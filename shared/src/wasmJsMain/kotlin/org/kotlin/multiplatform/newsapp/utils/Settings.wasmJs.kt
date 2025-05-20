package org.kotlin.multiplatform.newsapp.utils

actual class BookmarksUtil {

    actual fun getBookmarks(): Set<String> {
        println("Not yet implemented")
        return emptySet()
    }

    actual fun saveBookmarks(bookmarks: Set<String>) {
    }

    actual fun toggleBookmark(newsId: String, isBookmarked: Boolean) {
    }
}

actual fun shareNews(title: String, details: String, link: String, imageUrl: String?) {
}