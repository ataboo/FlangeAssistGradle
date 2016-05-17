package com.atasoft.flangeassist.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.atasoft.flangeassist.R;

public class SettingsCounter extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_counter);
    }
}


