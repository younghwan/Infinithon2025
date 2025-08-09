package com.dang1000.releaspoon.pref

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.CallSuper

class SharedPreferencesManager (context: Context){
    private val sharedPreferences : SharedPreferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE)

    var packageType: String
        get() = sharedPreferences.getString(PACKAGE_TYPE)
        set(value) = sharedPreferences.putString(PACKAGE_TYPE, value)

    var packageUrl: String
        get() = sharedPreferences.getString(PACKAGE_URL)
        set(value) = sharedPreferences.putString(PACKAGE_URL, value)

    @CallSuper
    fun clear() {
        sharedPreferences.edit()
            .remove(PACKAGE_TYPE)
            .remove(PACKAGE_URL)
            .apply()
    }

    companion object {
        const val PREFERENCES_KEY = "preferences"
        private const val PACKAGE_TYPE = "packageType"
        private const val PACKAGE_URL = "packageUrl"

        private fun SharedPreferences.getString(key: String) =
            getString(key, "").orEmpty()

        private fun SharedPreferences.putString(key: String, value: String) =
            edit().putString(key, value).apply()
    }
}