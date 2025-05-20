package org.kotlin.multiplatform.newsapp.utils

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.core.content.FileProvider
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.kotlin.multiplatform.newsapp.MyApp
import java.io.File
import java.io.FileOutputStream
import java.net.URL

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

//actual fun shareNews(title: String, details: String, link: String) {
//    val appContext: Context = MyApp.getContext()  // Get the context from your custom Application class
//    val context = appContext // Define this from Application or DI
//    val shareText = """
//        $title
//
//        $details
//
//        🔗 Read more: $link
//    """.trimIndent()
//
//    val intent = Intent(Intent.ACTION_SEND).apply {
//        type = "text/plain"
//        putExtra(Intent.EXTRA_TEXT, shareText)
//    }
//    val chooser = Intent.createChooser(intent, "Share News via")
//    chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//    context.startActivity(chooser)
//
//}

actual fun shareNews(title: String, details: String, link: String, imageUrl: String?) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val context = MyApp.getContext()
            val shareText = """
                $title

                $details

                🔗 Read more: $link
            """.trimIndent()

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = if (!imageUrl.isNullOrBlank()) "image/*" else "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
            }

            if (!imageUrl.isNullOrBlank()) {
                val bitmap = BitmapFactory.decodeStream(URL(imageUrl).openStream())
                val file = File(context.cacheDir, "shared_image.png")
                FileOutputStream(file).use {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                }

                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )

                intent.putExtra(Intent.EXTRA_STREAM, uri)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            withContext(Dispatchers.Main) {
                val chooser = Intent.createChooser(intent, "Share News via")
                chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(chooser)
            }

        } catch (e: Exception) {
            Log.e("ShareNews", "Error loading image", e)
        }
    }
}
