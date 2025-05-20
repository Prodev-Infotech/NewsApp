package org.kotlin.multiplatform.newsapp

import android.content.Intent
import android.net.Uri
import android.widget.Toast


actual fun openLink(url: String) {
    val context = ContextProvider.context
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    } catch (e: Exception) {
        Toast.makeText(context, "Invalid link", Toast.LENGTH_SHORT).show()
    }
}