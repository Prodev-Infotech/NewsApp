package org.kotlin.multiplatform.newsapp

import android.content.Context

object ContextProvider {
    private lateinit var _context: Context

    fun  init(context: Context) {
        _context = context.applicationContext
    }

    val context: Context
        get() = _context
}