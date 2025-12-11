// File: app/src/main/java/com/example/androiddd/utils/ThemeManager.kt
package com.example.androiddd.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES

object ThemeManager {

    private const val PREFS_NAME = "ThemePrefs"
    private const val KEY_THEME_MODE = "theme_mode"
    private const val TAG = "ThemeManager"
    const val DARK_THEME = AppCompatDelegate.MODE_NIGHT_YES
    const val LIGHT_THEME = AppCompatDelegate.MODE_NIGHT_NO
    const val SYSTEM_THEME = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM

    private var prefs: SharedPreferences? = null

    private fun getPrefs(context: Context): SharedPreferences {
        if (prefs == null) {
            prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            Log.d(TAG, "Theme preferences initialized.")
        }
        return prefs!!
    }

    // --- УБЕДИСЬ, ЧТО ЭТОТ МЕТОД СУЩЕСТВУЕТ ---
    fun isDarkTheme(context: Context): Boolean {
        val currentMode = getPrefs(context).getInt(KEY_THEME_MODE, MODE_NIGHT_FOLLOW_SYSTEM)
        Log.d(TAG, "Current theme mode: $currentMode")
        return currentMode == MODE_NIGHT_YES
    }
    // --- КОНЕЦ ПРОВЕРКИ МЕТОДА ---

    fun setTheme(context: Context, theme: Int) {
        Log.d(TAG, "Setting theme: $theme")
        getPrefs(context).edit().putInt(KEY_THEME_MODE, theme).apply()
        AppCompatDelegate.setDefaultNightMode(theme)
    }

    fun toggleTheme() {
        val currentMode = AppCompatDelegate.getDefaultNightMode()
        val newMode = if (currentMode == MODE_NIGHT_YES) MODE_NIGHT_NO else MODE_NIGHT_YES
        AppCompatDelegate.setDefaultNightMode(newMode)
    }

    fun init(context: Context) {
        val savedThemeMode = getPrefs(context).getInt(KEY_THEME_MODE, MODE_NIGHT_FOLLOW_SYSTEM)
        AppCompatDelegate.setDefaultNightMode(savedThemeMode)
    }
}