package com.example.androiddd.data.repository

import android.content.Context
import android.content.SharedPreferences

class AuthRepository(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_NAME = "user_name" // Не используется в проверке входа, но может быть полезен
        private const val KEY_USER_PASSWORD = "user_password"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    fun saveUserData(email: String, name: String, password: String) {
        sharedPreferences.edit()
            .putString(KEY_USER_EMAIL, email)
            .putString(KEY_USER_NAME, name) // Можно хранить, если нужно отображать имя
            .putString(KEY_USER_PASSWORD, password)
            // Не устанавливаем KEY_IS_LOGGED_IN здесь при регистрации, а только при успешном входе
            .apply()
    }

    fun getUserEmail(): String? = sharedPreferences.getString(KEY_USER_EMAIL, null)
    fun getUserName(): String? = sharedPreferences.getString(KEY_USER_NAME, null)
    fun getUserPassword(): String? = sharedPreferences.getString(KEY_USER_PASSWORD, null)
    fun isLoggedIn(): Boolean = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)

    // Изменяем logout: удаляем только статус входа, не удаляя email и пароль
    fun logout() {
        sharedPreferences.edit()
            .remove(KEY_IS_LOGGED_IN) // <-- Удаляем только статус
            .apply()
    }

    // Новый метод для установки статуса входа
    fun setLoggedIn(loggedIn: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_IS_LOGGED_IN, loggedIn)
            .apply()
    }
}