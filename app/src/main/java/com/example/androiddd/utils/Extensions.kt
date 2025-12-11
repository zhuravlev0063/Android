package com.example.androiddd.utils

import android.content.res.Resources
import android.content.Context

fun Int.dpToPx(context: Context): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
fun Float.dpToPx(context: Context): Float = (this * Resources.getSystem().displayMetrics.density)