// File: app/src/main/java/com/example/androiddd/fragments/ThemeSettingsFragment.kt
package com.example.androiddd.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Switch
import androidx.fragment.app.Fragment
import com.example.androiddd.R
import com.example.androiddd.utils.ThemeManager

class ThemeSettingsFragment : Fragment() {

    private lateinit var switchTheme: Switch

    companion object {
        fun newInstance(): ThemeSettingsFragment {
            return ThemeSettingsFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_theme_settings, container, false)
        switchTheme = view.findViewById(R.id.switchTheme)

        setupSwitch()

        return view
    }

    private fun setupSwitch() {
        val isDarkTheme = ThemeManager.isDarkTheme(requireContext())
        switchTheme.isChecked = isDarkTheme

        switchTheme.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                ThemeManager.setTheme(requireContext(), ThemeManager.DARK_THEME)
            } else {
                ThemeManager.setTheme(requireContext(), ThemeManager.LIGHT_THEME)
            }
            // Перезапустить Activity для применения темы
            requireActivity().recreate()
        })
    }
}