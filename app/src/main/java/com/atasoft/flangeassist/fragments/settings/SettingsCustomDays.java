package com.atasoft.flangeassist.fragments.settings;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.atasoft.flangeassist.R;

public class SettingsCustomDays extends PreferenceFragment implements Preference.OnPreferenceChangeListener{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_custom_days);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return false;
    }
}


