package com.confessionsearch.release1.ui.settings

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import com.confessionsearch.release1.MainActivity
import com.confessionsearch.release1.R

class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    lateinit var notesLayoutSelector: ListPreference
    lateinit var gridSizePreference: SeekBarPreference
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        gridSizePreference = findPreference<SeekBarPreference>("gridSize")!!

        notesLayoutSelector = findPreference("noteLayoutSelection")!!
        var versionInfo: Preference = findPreference<Preference>("version")!!

        versionInfo.summary = "Version info: ${MainActivity.versionName}"
        versionInfo.setOnPreferenceClickListener {
            it.summary = "Version Info: ${MainActivity.versionName}"
            true
        }

        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }


    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {

    }
}