package org.kotlin.multiplatform.newsapp

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

actual fun openLink(url: String) {
    val nsUrl = NSURL.URLWithString(url)
    if (nsUrl != null) {
        UIApplication.sharedApplication.openURL(nsUrl)
    }
}