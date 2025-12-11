// File: app/src/main/java/com/example/androiddd/fragments/AboutAppFragment.kt
package com.example.androiddd.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.androiddd.R

class AboutAppFragment : Fragment() {

    companion object {
        fun newInstance(): AboutAppFragment {
            return AboutAppFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_about_app, container, false)

        val appNameText = view.findViewById<TextView>(R.id.appNameText)
        val versionText = view.findViewById<TextView>(R.id.versionText)

        appNameText.text = getString(R.string.app_name)
        versionText.text = getString(R.string.app_version)

        return view
    }
}