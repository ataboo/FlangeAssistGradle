package com.atasoft.flangeassist.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.atasoft.flangeassist.R;
import com.atasoft.helpers.TaxManager;

public class SettingsRates extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_rates);
    }
}


