// File: app/src/main/java/com/example/androiddd/fragments/LanguageSettingsFragment.kt
package com.example.androiddd.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.androiddd.R
import com.example.androiddd.utils.LocaleManager
import java.util.*

class LanguageSettingsFragment : Fragment() {

    private lateinit var radioGroup: RadioGroup

    companion object {
        fun newInstance(): LanguageSettingsFragment {
            return LanguageSettingsFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_language_settings, container, false)
        radioGroup = view.findViewById(R.id.radioGroupLanguage)

        setupRadioGroup()

        return view
    }

    private fun setupRadioGroup() {
        // --- ИСПРАВЛЕНИЕ: Используем язык системы, если сохранённый null ---
        val currentLang = LocaleManager.getLanguage(requireContext()) ?: Locale.getDefault().language
        Log.d("LanguageSettingsFragment", "Current language code: $currentLang") // Добавим лог
        // --- КОНЕЦ ИСПРАВЛЕНИЯ ---

        val russianRadioButton = radioGroup.findViewById<RadioButton>(R.id.radioButtonRussian)
        val englishRadioButton = radioGroup.findViewById<RadioButton>(R.id.radioButtonEnglish)

        when (currentLang) {
            "ru" -> {
                Log.d("LanguageSettingsFragment", "Selecting Russian radio button")
                russianRadioButton.isChecked = true
            }
            "en" -> {
                Log.d("LanguageSettingsFragment", "Selecting English radio button")
                englishRadioButton.isChecked = true
            }
            else -> {
                Log.d("LanguageSettingsFragment", "Defaulting to Russian radio button for language: $currentLang")
                russianRadioButton.isChecked = true // default
            }
        }

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioButtonRussian -> {
                    Log.d("LanguageSettingsFragment", "Switching to Russian")
                    LocaleManager.setLocaleAndRestart(requireContext(), "ru")
                }
                R.id.radioButtonEnglish -> {
                    Log.d("LanguageSettingsFragment", "Switching to English")
                    LocaleManager.setLocaleAndRestart(requireContext(), "en")
                }
            }
        }
    }
}