// File: app/src/main/java/com/example/androiddd/MyApplication.kt
package com.example.androiddd

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.util.Log // <-- Добавьте импорт
import com.example.androiddd.utils.LocaleManager
import com.example.androiddd.utils.ThemeManager

class MyApplication : Application() {

    companion object {
        private const val TAG = "MyApplication"
    }

    override fun onCreate() {
        Log.d(TAG, "onCreate called")
        super.onCreate()
        ThemeManager.init(this)
        // Больше НЕ вызываем ничего связанного с LocaleManager здесь, если только не хотим устанавливать локаль на уровне приложения при старте
    }

    // Возвращаем attachBaseContext для установки локали приложения
    override fun attachBaseContext(base: Context?) {
        Log.d(TAG, "attachBaseContext called with base context: ${base?.resources?.configuration?.locales?.get(0)?.language ?: "null"}")
        var updatedContext = base
        if (base != null) {
            val preferredLanguage = LocaleManager.getLanguage(base) // Получаем сохранённый язык
            Log.d(TAG, "LocaleManager.getLanguage returned: $preferredLanguage")
            if (preferredLanguage != null) {
                // Применяем сохранённый язык, если он есть
                updatedContext = LocaleManager.updateContext(base, preferredLanguage)
            }else {
                Log.d(TAG, "No saved language, using system default")
                // Используем системный язык (base)
            }
        }
        Log.d(TAG, "Calling super.attachBaseContext with context locale: ${updatedContext?.resources?.configuration?.locales?.get(0)?.language ?: "null"}")
        // Вызываем super.attachBaseContext() ТОЛЬКО ОДИН раз с итоговым контекстом
        super.attachBaseContext(updatedContext)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.d(TAG, "onConfigurationChanged called. New locale: ${newConfig.locales.get(0).language}")
    }
}