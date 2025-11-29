// File: app/src/main/java/com/example/androiddd/RegisterActivity.kt
package com.example.androiddd

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.androiddd.data.repository.AuthRepository
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class RegisterActivity : AppCompatActivity() {

    private lateinit var authRepository: AuthRepository
    private lateinit var textInputName: TextInputLayout
    private lateinit var textInputEmail: TextInputLayout
    private lateinit var textInputPassword: TextInputLayout
    private lateinit var editTextName: TextInputEditText
    private lateinit var editTextEmail: TextInputEditText
    private lateinit var editTextPassword: TextInputEditText
    private lateinit var buttonRegister: Button
    private lateinit var buttonGoToLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        authRepository = AuthRepository(this)

        textInputName = findViewById(R.id.textInputName)
        textInputEmail = findViewById(R.id.textInputEmail)
        textInputPassword = findViewById(R.id.textInputPassword)
        editTextName = findViewById(R.id.editTextName)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        buttonRegister = findViewById(R.id.buttonRegister)
        buttonGoToLogin = findViewById(R.id.buttonGoToLogin)

        buttonRegister.setOnClickListener {
            attemptRegister()
        }

        buttonGoToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun attemptRegister() {
        val name = editTextName.text.toString().trim()
        val email = editTextEmail.text.toString().trim()
        val password = editTextPassword.text.toString()

        // Сброс ошибок
        textInputName.error = null
        textInputEmail.error = null
        textInputPassword.error = null

        var isValid = true

        // Валидация имени
        if (name.isEmpty()) {
            textInputName.error = "Имя обязательно"
            isValid = false
        }

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
            // Сохраняем данные в AuthRepository
            authRepository.saveUserData(email, name, password)
            // Показываем сообщение об успехе
            Toast.makeText(this, "Регистрация успешна! Пожалуйста, войдите.", Toast.LENGTH_LONG).show()
            // Перенаправляем на LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}