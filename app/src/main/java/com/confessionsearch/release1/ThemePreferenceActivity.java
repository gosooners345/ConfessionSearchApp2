package com.confessionsearch.release1;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;

public class ThemePreferenceActivity extends PreferenceActivity {
    public static final int RESULT_CODE_THEME_UPDATED = 1;
    public static final int RESULT_SYSTEM_DARK_MODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(MainActivity.themeID);
      /*  switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
        {
            case Configuration.UI_MODE_NIGHT_NO:
               // themeID = R.style.LightMode;
                setTheme(R.style.LightMode);break;
            case Configuration.UI_MODE_NIGHT_YES:
             //   themeID=R.style.DarkMode;
                setTheme(R.style.DarkMode);break;
        }*/
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
        findPreference("darkMode").setOnPreferenceChangeListener(new RefreshActivityOnPreferenceChangeListener(RESULT_CODE_THEME_UPDATED));
        //  findPreference("system_theme").setOnPreferenceChangeListener(new RefreshActivityOnPreferenceChangeListener(RESULT_SYSTEM_DARK_MODE));
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