// File: app/src/main/java/com/example/androiddd/LoginActivity.kt
package com.example.androiddd

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.androiddd.data.repository.AuthRepository
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.androiddd.utils.LocaleManager

class LoginActivity : AppCompatActivity()  {

    private lateinit var authRepository: AuthRepository
    private lateinit var buttonBack: ImageButton
    private lateinit var textInputEmail: TextInputLayout
    private lateinit var textInputPassword: TextInputLayout
    private lateinit var editTextEmail: TextInputEditText
    private lateinit var editTextPassword: TextInputEditText
    private lateinit var buttonLogin: Button
    private lateinit var buttonGoToRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        authRepository = AuthRepository(this)

        buttonBack = findViewById(R.id.buttonBack) // <-- Предполагается, что у тебя есть ImageButton с id 'buttonBack' в layout_activity_login.xml
        buttonBack.setOnClickListener {
            // При нажатии на "назад" из LoginActivity, возвращаемся в MainActivity
            val intent = Intent(this, MainActivity::class.java)
            // Не используем CLEAR_TASK или NEW_TASK, просто стартуем MainActivity
            startActivity(intent)
            finish() // Закрываем LoginActivity
        }

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
    override fun attachBaseContext(base: Context?) {
        val updatedContext = if (base != null) {
            val savedLanguage = LocaleManager.getLanguage(base) // Получаем сохранённый язык
            if (savedLanguage != null) {
                // Если язык сохранён, применяем его к контексту Activity
                LocaleManager.updateContext(base, savedLanguage)
            } else {
                // Иначе используем системный
                base
            }
        } else {
            base
        }
        super.attachBaseContext(updatedContext)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Обработка нажатия на "стрелку назад" в ActionBar
                onBackPressed() // Вызывает стандартное поведение "назад", закрывает Activity
                true
            }
            else -> super.onOptionsItemSelected(item)
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
            textInputEmail.error = getString(R.string.error_email_required)
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            textInputEmail.error = getString(R.string.error_invalid_email)
            isValid = false
        }

        // Валидация пароля
        if (password.isEmpty()) {
            textInputPassword.error = getString(R.string.error_password_required)
            isValid = false
        } else if (password.length < 6) {
            textInputPassword.error = getString(R.string.error_password_too_short)
            isValid = false
        }

        if (isValid) {
            // Проверяем данные в AuthRepository
            val storedPassword = authRepository.getUserPassword()
            val storedEmail = authRepository.getUserEmail()

            // Теперь storedEmail и storedPassword могут быть null, если пользователь ещё не регистрировался
            if (storedEmail != null && storedPassword != null && email == storedEmail && password == storedPassword) {
                // Успешный вход
                Toast.makeText(this, getString(R.string.success_login), Toast.LENGTH_SHORT).show()
                // Устанавливаем статус входа
                authRepository.setLoggedIn(true) // <-- Добавлено
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this,getString(R.string.error_login_failed), Toast.LENGTH_SHORT).show()
            }
        }
    }
}