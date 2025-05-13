package org.kotlin.multiplatform.newsapp

import kotlinx.browser.window

actual fun openLink(url: String) {
    window.open(url, "_blank")
}