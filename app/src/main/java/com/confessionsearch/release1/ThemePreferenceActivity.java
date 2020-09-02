package com.confessionsearch.release1;

import android.os.Bundle;
import com.confessionsearch.release1.MainActivity;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;

import androidx.appcompat.widget.Toolbar;

public class ThemePreferenceActivity extends PreferenceActivity {
    public static final int RESULT_CODE_THEME_UPDATED = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
setTheme(MainActivity.themeID);
            super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
        findPreference("darkMode").setOnPreferenceChangeListener(new RefreshActivityOnPreferenceChangeListener(RESULT_CODE_THEME_UPDATED));
    }

    private class RefreshActivityOnPreferenceChangeListener implements OnPreferenceChangeListener {
        private final int resultCode;
        public RefreshActivityOnPreferenceChangeListener(int resultCode) {
            this.resultCode = resultCode;


        }
        @Override
        public boolean onPreferenceChange(Preference p, Object newValue) {
           setResult(resultCode);
            return true;
        }

    }


}