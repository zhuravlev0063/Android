// File: app/src/main/java/com/example/androiddd/RegisterActivity.kt
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
import com.example.androiddd.utils.LocaleManager
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class RegisterActivity : AppCompatActivity()  {

    private lateinit var authRepository: AuthRepository
    private lateinit var buttonBack: ImageButton
    private lateinit var textInputName: TextInputLayout
    private lateinit var textInputEmail: TextInputLayout
    private lateinit var textInputPassword: TextInputLayout
    private lateinit var editTextName: TextInputEditText
    private lateinit var editTextEmail: TextInputEditText
    private lateinit var editTextPassword: TextInputEditText
    private lateinit var buttonRegister: Button
    private lateinit var buttonGoToLogin: Button

    private val TAG = "RegisterActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        authRepository = AuthRepository(this)

        buttonBack = findViewById(R.id.buttonBack) // <-- Предполагается, что у тебя есть ImageButton с id 'buttonBack' в layout_activity_register.xml
        buttonBack.setOnClickListener {
            // При нажатии на "назад" из RegisterActivity, возвращаемся в LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Закрываем RegisterActivity
        }

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
    override fun attachBaseContext(base: Context?) {
        Log.d(TAG, "RegisterActivity.attachBaseContext called with base context locale: ${base?.resources?.configuration?.locales?.get(0)?.language ?: "null"}")
        val updatedContext = if (base != null) {
            val savedLanguage = LocaleManager.getLanguage(base) // Получаем сохранённый язык
            Log.d(TAG, "LocaleManager.getLanguage returned: $savedLanguage")
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
            textInputName.error = getString(R.string.error_name_required)
            isValid = false
        }

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
            // Сохраняем данные в AuthRepository
            authRepository.saveUserData(email, name, password)
            // Показываем сообщение об успехе
            Toast.makeText(this, getString(R.string.success_registration), Toast.LENGTH_LONG).show()
            // Перенаправляем на LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}