package com.confessionsearch.release1

import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceActivity

class ThemePreferenceActivity : PreferenceActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(MainActivity.themeID)
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preferences)
        findPreference("darkMode").onPreferenceChangeListener = RefreshActivityOnPreferenceChangeListener(RESULT_CODE_THEME_UPDATED)
        //  findPreference("system_theme").setOnPreferenceChangeListener(new RefreshActivityOnPreferenceChangeListener(RESULT_SYSTEM_DARK_MODE));
    }

    private inner class RefreshActivityOnPreferenceChangeListener(private val resultCode: Int) : Preference.OnPreferenceChangeListener {
        override fun onPreferenceChange(p: Preference, newValue: Any): Boolean {
            setResult(resultCode)
            return true
        }
    }

    companion object {
        const val RESULT_CODE_THEME_UPDATED = 1
        const val RESULT_SYSTEM_DARK_MODE = 1
    }
}