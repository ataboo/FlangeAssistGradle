package com.atasoft.flangeassist.fragments.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;

import com.atasoft.flangeassist.R;
import com.atasoft.flangeassist.fragments.cashcounter.counterobjects.CounterScene;
import com.atasoft.flangeassist.fragments.paycalc.TaxManager;

public class SettingsCounter extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_counter);

        String[] sceneNames = CounterScene.Scene.getNames();

        ListPreference sceneListPref = (ListPreference) findPreference(getResources().getString(R.string.counter_scene_key));
        sceneListPref.setEntries(sceneNames);
        sceneListPref.setEntryValues(sceneNames);
        sceneListPref.setTitle(String.format("ActiveScene: %s", sceneListPref.getValue()));
        if(sceneListPref.getValue() == null) sceneListPref.setValueIndex(0);
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
        ListPreference sceneListPref = (ListPreference) findPreference(getResources().getString(R.string.counter_scene_key));

        sceneListPref.setTitle(String.format("ActiveScene: %s", sceneListPref.getValue()));
    }
}


