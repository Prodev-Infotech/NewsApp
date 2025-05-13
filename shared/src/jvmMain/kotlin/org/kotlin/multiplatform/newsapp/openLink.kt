package org.kotlin.multiplatform.newsapp

import java.awt.Desktop
import java.net.URI

actual fun openLink(url: String) {
    try {
        Desktop.getDesktop().browse(URI(url))
    } catch (e: Exception) {
        println("Invalid URL: $url")
    }
}