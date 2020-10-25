package com.atasoft.flangeassist.fragments.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;

import com.atasoft.flangeassist.*;
import com.atasoft.flangeassist.fragments.paycalc.Province;
import com.atasoft.flangeassist.fragments.paycalc.TaxManager;

public class SettingsPayCalc extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    ListPreference provListPref;
    ListPreference yearListPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_paycalc);

        setupRegionalLists();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        //
    }

    private void setupRegionalLists(){
        provListPref = (ListPreference) findPreference("list_provWageNew");
        provListPref.setEntries(Province.getActiveProvinceNames());
        provListPref.setEntryValues(Province.getActiveProvinceNames());
        if(provListPref.getValue() == null) provListPref.setValueIndex(Province.AB.getIndex());

        yearListPref = (ListPreference) findPreference("list_taxYearNew");
        yearListPref.setEntries(TaxManager.yearStrings);
        yearListPref.setEntryValues(TaxManager.yearStrings);
        if(yearListPref.getValue() == null) yearListPref.setValueIndex(TaxManager.yearStrings.length - 1);

        updateRegionTitles();
    }

    private void updateRegionTitles(){
        provListPref.setTitle(String.format("Active Province: %s", provListPref.getValue()));
        yearListPref.setTitle(String.format("Tax Year: %s", yearListPref.getValue()));
    }

    @Override
    public void onResume() {
        super.onResume();

        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updateRegionTitles();
    }
}


