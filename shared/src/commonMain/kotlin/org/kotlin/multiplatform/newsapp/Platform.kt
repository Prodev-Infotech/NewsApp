package org.kotlin.multiplatform.newsapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform