package org.kotlin.multiplatform.newsapp.utils

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.compose.ui.graphics.Color
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import org.kotlin.multiplatform.newsapp.MyApp

actual val settings: Settings by lazy {
    val appContext: Context = MyApp.getContext()  // Get the context from your custom Application class
    val sharedPreferences: SharedPreferences = appContext.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
    SharedPreferencesSettings(sharedPreferences)
}

actual class BookmarksUtil actual constructor() {
    val appContext: Context = MyApp.getContext()  // Get the context from your custom Application class

    private val sharedPreferences = appContext.getSharedPreferences("bookmarks_prefs", Context.MODE_PRIVATE)

    // Get the list of bookmarked news items from SharedPreferences
    actual fun getBookmarks(): Set<String> {
        return sharedPreferences.getStringSet("bookmarks", emptySet()) ?: emptySet()
    }

    // Save the set of bookmarked items to SharedPreferences
    actual fun saveBookmarks(bookmarks: Set<String>) {
        sharedPreferences.edit().putStringSet("bookmarks", bookmarks).apply()
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
    val appContext: Context = MyApp.getContext()  // Get the context from your custom Application class
    val context = appContext // Define this from Application or DI
    val shareText = """
        $title

        $details

        🔗 Read more: $link
    """.trimIndent()

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareText)
    }
    val chooser = Intent.createChooser(intent, "Share News via")
    chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(chooser)

}
