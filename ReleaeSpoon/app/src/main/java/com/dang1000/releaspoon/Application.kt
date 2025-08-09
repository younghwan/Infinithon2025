package com.dang1000.releaspoon

import com.dang1000.releaspoon.pref.SharedPreferencesManager

class SpoonApplication: android.app.Application() {
    companion object {
        lateinit var globalApplication: SpoonApplication
        lateinit var instance: SpoonApplication
        lateinit var prefManager: SharedPreferencesManager

        fun getGlobalApplicationContext(): SpoonApplication {
            return instance
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        globalApplication = this
        prefManager = SharedPreferencesManager(applicationContext)
    }
}