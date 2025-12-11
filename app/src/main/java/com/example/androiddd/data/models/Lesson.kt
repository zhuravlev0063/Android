// File: app/src/main/java/com/example/androiddd/data/models/Lesson.kt
package com.example.androiddd.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// Используем @Parcelize для автоматической генерации кода Parcelable
@Parcelize
data class Lesson(
    val time: String,
    val name: String,
    val room: String,
    val teacher: String,
    val type: String,
    val typeColor: Int
) : Parcelable