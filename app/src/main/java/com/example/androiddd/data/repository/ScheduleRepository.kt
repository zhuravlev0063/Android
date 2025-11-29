// data/repository/ScheduleRepository.kt

package com.example.androiddd.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.androiddd.data.models.Lesson

class ScheduleRepository(private val context: Context) {

    private val sharedPreferences: SharedPreferences
        get() = context.getSharedPreferences("lesson_data", Context.MODE_PRIVATE)

    fun getSavedLessonData(originalName: String, originalTime: String, field: String, defaultValue: String): String {
        // Заменяем пробелы и двоеточия на подчеркивание
        val safeOriginalName = originalName.replace(" ", "_").replace(":", "_").replace("-", "_")
        val safeOriginalTime = originalTime.replace(" ", "_").replace(":", "_").replace("-", "_")
        val key = "${safeOriginalName}_${safeOriginalTime}_$field"
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }
    fun getSavedLessonColor(originalName: String, originalTime: String, field: String, defaultValue: Int): Int {
        val key = "${originalName}_${originalTime}_$field" // <-- Новый ключ
        return sharedPreferences.getInt(key, defaultValue)
    }

    fun saveLessonData(originalName: String, originalTime: String, lesson: Lesson) {
        // Заменяем пробелы и двоеточия на подчеркивание
        val safeOriginalName = originalName.replace(" ", "_").replace(":", "_").replace("-", "_")
        val safeOriginalTime = originalTime.replace(" ", "_").replace(":", "_").replace("-", "_")
        val editor = sharedPreferences.edit()
        val baseKey = "${safeOriginalName}_${safeOriginalTime}"
        editor.putString("${baseKey}_name", lesson.name)
        editor.putString("${baseKey}_time", lesson.time)
        editor.putString("${baseKey}_teacher", lesson.teacher)
        editor.putString("${baseKey}_room", lesson.room)
        editor.putString("${baseKey}_type", lesson.type)
        editor.putInt("${baseKey}_type_color", lesson.typeColor)
        editor.apply()
    }
}