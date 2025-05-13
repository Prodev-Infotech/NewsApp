package org.kotlin.multiplatform.newsapp

import android.app.Application
import android.content.Context

class MyApp: Application() {

    companion object {
        private lateinit var appContext: Context

        fun getContext(): Context {
            return appContext
        }
    }
    override fun onCreate() {
        super.onCreate()
        ContextProvider.context = this
        appContext = applicationContext // Initialize context here

    }
}
