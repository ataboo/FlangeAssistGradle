package com.atasoft.flangeassist.fragments.settings;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;

import com.atasoft.flangeassist.*;
import com.atasoft.flangeassist.fragments.paycalc.TaxManager;

public class SettingsRegional extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_regional);
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        ListPreference provListPref = (ListPreference) findPreference("list_provWageNew");
        provListPref.setEntries(TaxManager.Prov.getActiveProvinceNames());
        provListPref.setEntryValues(TaxManager.Prov.getActiveProvinceNames());
        if(provListPref.getValue() == null) provListPref.setValueIndex(TaxManager.Prov.AB.getIndex());
        
        ListPreference yearListPref = (ListPreference) findPreference("list_taxYearNew");
        yearListPref.setEntries(TaxManager.yearStrings);
        yearListPref.setEntryValues(TaxManager.yearStrings);
        if(yearListPref.getValue() == null) yearListPref.setValueIndex(TaxManager.yearStrings.length - 1);
    }
}


