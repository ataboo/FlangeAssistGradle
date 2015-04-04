package com.atasoft.flangeassist.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.atasoft.flangeassist.*;
import com.atasoft.helpers.TaxManager;

public class SettingsRegional extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_regional);
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        ListPreference provListPref = (ListPreference) findPreference("list_provWageNew");
        provListPref.setEntries(TaxManager.getActiveProvinceStrings());
        provListPref.setEntryValues(TaxManager.getActiveProvinceStrings());
        if(provListPref.getValue() == null) provListPref.setValueIndex(TaxManager.PROV_AB);
        
        ListPreference yearListPref = (ListPreference) findPreference("list_taxYearNew");
        yearListPref.setEntries(TaxManager.yearStrings);
        yearListPref.setEntryValues(TaxManager.yearStrings);
        if(yearListPref.getValue() == null) yearListPref.setValueIndex(TaxManager.yearStrings.length - 1);
    }
}


