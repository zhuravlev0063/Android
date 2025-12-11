// File: app/src/main/java/com/example/androiddd/SettingsActivity.kt
package com.example.androiddd

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.androiddd.fragments.AboutAppFragment
import com.example.androiddd.fragments.LanguageSettingsFragment
import com.example.androiddd.fragments.ThemeSettingsFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class SettingsActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: androidx.viewpager2.widget.ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)

        val adapter = SettingsPagerAdapter(supportFragmentManager, lifecycle)
        viewPager.adapter = adapter

        // Связываем TabLayout и ViewPager2
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.tab_language)
                1 -> getString(R.string.tab_theme)
                2 -> getString(R.string.tab_about)
                else -> getString(R.string.tab_language) // fallback
            }
        }.attach()
    }

    // Адаптер для ViewPager2
    inner class SettingsPagerAdapter(fm: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fm, lifecycle) {
        override fun getItemCount(): Int = 3 // Три вкладки

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> LanguageSettingsFragment.newInstance()
                1 -> ThemeSettingsFragment.newInstance()
                2 -> AboutAppFragment.newInstance()
                else -> LanguageSettingsFragment.newInstance() // fallback
            }
        }
    }
}