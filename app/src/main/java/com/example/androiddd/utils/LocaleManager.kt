// File: app/src/main/java/com/example/androiddd/utils/LocaleManager.kt
package com.example.androiddd.utils

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import java.util.Locale

object LocaleManager {

    private const val PREFS_NAME = "LanguagePrefs"
    private const val KEY_LANGUAGE = "language"
    private const val LANG_RU = "ru"
    private const val LANG_EN = "en"
    private const val TAG = "LocaleManager"

    private var prefs: SharedPreferences? = null

    private fun getPrefs(context: Context): SharedPreferences {
        if (prefs == null) {
            prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            Log.d(TAG, "SharedPreferences initialized.")
            }
        return prefs!!
    }

    // НОВЫЙ МЕТОД: Применяет локаль к Context
    fun updateContext(context: Context, language: String): Context {
        Log.d(TAG, "updateContext called with language: $language")
        val locale = Locale(language)
        Locale.setDefault(locale)

        val resources: Resources = context.resources
        val configuration: Configuration = resources.configuration
        configuration.setLocale(locale)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Log.d(TAG, "Creating new Configuration Context for API >= N")
            context.createConfigurationContext(configuration)
        } else {
            @Suppress("DEPRECATION")
            Log.d(TAG, "Updating resources configuration for API < N")
            resources.updateConfiguration(configuration, resources.displayMetrics)
            context
        }
    }

    fun setLocale(context: Context, language: String) {
        Log.d(TAG, "setLocale called, saving language: $language")
        getPrefs(context).edit().putString(KEY_LANGUAGE, language).apply() // <-- Вернулись к apply()
        // НЕ вызываем updateResources(context, language) здесь, это делает attachBaseContext при следующем запуске или recreate()
    }
    fun setLocaleAndRestart(context: Context, language: String) {
        Log.d(TAG, "setLocaleAndRestart called, saving language: $language")
        // ВАЖНО: Используем getPrefs(context) для получения экземпляра SharedPreferences
        val editor = getPrefs(context).edit()
        editor.putString(KEY_LANGUAGE, language)
        val success = editor.commit() // <-- Используем commit() для синхронной записи
        if (!success) {
            Log.e(TAG, "Failed to commit language change to SharedPreferences!")
        } else {
            Log.d(TAG, "Language change committed successfully.")
        }

        // Перезапустить приложение для применения нового языка
        val packageManager = context.packageManager
        val launchIntent = packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        context.startActivity(launchIntent)
        // Завершить текущий процесс, чтобы он перезапустился с новой локалью
        android.os.Process.killProcess(android.os.Process.myPid())
    }

    fun getLanguage(context: Context): String? { // Принимаем Context
        val savedLang = getPrefs(context).getString(KEY_LANGUAGE, null)
        Log.d(TAG, "getLanguage called, returning: $savedLang")
        return savedLang
    }

    fun toggleLanguage(context: Context) { // Принимаем Context
        val savedLang = getLanguage(context)
        val systemLang = Locale.getDefault().language
        Log.d(TAG, "toggleLanguage called. Current savedLang: $savedLang, systemLang: $systemLang")

        val currentLang = if (savedLang != null) {
            Log.d(TAG, "Using saved language: $savedLang")
            savedLang
        } else {
            Log.d(TAG, "No saved language, using system language: $systemLang")
            systemLang
        }

        val newLang = if (currentLang == LANG_RU) LANG_EN else LANG_RU
        Log.d(TAG, "Toggling from $currentLang to $newLang")
        setLocale(context, newLang) // <-- Сохраняем новый язык
        // НЕ вызываем killProcess, а вызываем recreate() для Activity
    }
}