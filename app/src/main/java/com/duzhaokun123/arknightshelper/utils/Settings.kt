package com.duzhaokun123.arknightshelper.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager


object Settings {
    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) {
        sharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
    }

    object Layout {
        val uiMode: Int
            get() {
                return when (sharedPreferences.getString("ui_mod", "0")) {
                    "2" -> AppCompatDelegate.MODE_NIGHT_YES
                    "1" -> AppCompatDelegate.MODE_NIGHT_NO
                    else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                }
            }
    }
}