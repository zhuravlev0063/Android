// File: app/src/main/java/com/example/androiddd/LoginActivity.kt
package com.example.androiddd

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.androiddd.data.repository.AuthRepository
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class LoginActivity : AppCompatActivity() {

    private lateinit var authRepository: AuthRepository
    private lateinit var textInputEmail: TextInputLayout
    private lateinit var textInputPassword: TextInputLayout
    private lateinit var editTextEmail: TextInputEditText
    private lateinit var editTextPassword: TextInputEditText
    private lateinit var buttonLogin: Button
    private lateinit var buttonGoToRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { false } // Показываем splash до тех пор, пока не выполнится условие
        // Установим тему для дальнейшего использования (после splash)
        setTheme(R.style.Theme_Androiddd) // Убедитесь, что это имя соответствует вашей теме AppCompat

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        authRepository = AuthRepository(this)

        // Проверяем, аутентифицирован ли пользователь
        if (authRepository.isLoggedIn()) {
            // Если да, перенаправляем на MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Закрываем LoginActivity
            return
        }

        textInputEmail = findViewById(R.id.textInputEmail)
        textInputPassword = findViewById(R.id.textInputPassword)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        buttonLogin = findViewById(R.id.buttonLogin)
        buttonGoToRegister = findViewById(R.id.buttonGoToRegister)

        buttonLogin.setOnClickListener {
            attemptLogin()
        }

        buttonGoToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
    private fun attemptLogin() {
        val email = editTextEmail.text.toString().trim()
        val password = editTextPassword.text.toString()

        // Сброс ошибок
        textInputEmail.error = null
        textInputPassword.error = null

        var isValid = true

        // Валидация email
        if (email.isEmpty()) {
            textInputEmail.error = "Email обязателен"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            textInputEmail.error = "Введите корректный email"
            isValid = false
        }

        // Валидация пароля
        if (password.isEmpty()) {
            textInputPassword.error = "Пароль обязателен"
            isValid = false
        } else if (password.length < 6) {
            textInputPassword.error = "Пароль должен быть не менее 6 символов"
            isValid = false
        }

        if (isValid) {
            // Проверяем данные в AuthRepository
            val storedPassword = authRepository.getUserPassword()
            val storedEmail = authRepository.getUserEmail()

            // Теперь storedEmail и storedPassword могут быть null, если пользователь ещё не регистрировался
            if (storedEmail != null && storedPassword != null && email == storedEmail && password == storedPassword) {
                // Успешный вход
                Toast.makeText(this, "Вход успешен!", Toast.LENGTH_SHORT).show()
                // Устанавливаем статус входа
                authRepository.setLoggedIn(true) // <-- Добавлено
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                // Ошибка входа
                Toast.makeText(this, "Неверный email или пароль", Toast.LENGTH_SHORT).show()
            }
        }
    }
}