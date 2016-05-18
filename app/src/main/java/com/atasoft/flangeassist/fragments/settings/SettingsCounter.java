package com.atasoft.flangeassist.fragments.settings;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.atasoft.flangeassist.R;
import com.atasoft.flangeassist.fragments.FlangeFragment;
import com.atasoft.flangeassist.fragments.cashcounter.scenes.CounterScene;

import java.text.NumberFormat;

public class SettingsCounter extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    private String wageKey;
    private String sceneKey;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_counter);

        wageKey = getResources().getString(R.string.counter_wage_key);
        sceneKey = getResources().getString(R.string.counter_scene_key);
        getPreferenceScreen().findPreference(wageKey).setOnPreferenceChangeListener(this);
        getPreferenceScreen().findPreference(sceneKey).setOnPreferenceChangeListener(this);

        String[] sceneNames = CounterScene.Scene.getNames();

        ListPreference sceneListPref = (ListPreference) findPreference(getResources().getString(R.string.counter_scene_key));
        sceneListPref.setEntries(sceneNames);
        sceneListPref.setEntryValues(sceneNames);
        updateSceneTitle(sceneListPref);

        EditTextPreference wageEditPref = (EditTextPreference) findPreference(getResources().getString(R.string.counter_wage_key));

        updateWageSummary(wageEditPref, wageEditPref.getText());
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {


        if(preference.getKey().equals(wageKey) && preference instanceof EditTextPreference){
            EditTextPreference wagePref = (EditTextPreference) preference;
            if(validateWage(newValue)){
                updateWageSummary(wagePref, (String)newValue);
                return true;
            } else {
                return false;
            }
        }

        if(preference.getKey().equals(sceneKey) && preference instanceof ListPreference){
            ListPreference sceneListPref = (ListPreference) preference;
            updateSceneTitle(sceneListPref);
            return true;
        }

        return true;
    }

    private void updateSceneTitle(ListPreference sceneListPref){
        sceneListPref.setTitle(String.format("ActiveScene: %s", sceneListPref.getValue()));
        if(sceneListPref.getValue() == null) sceneListPref.setValueIndex(0);
    }

    private void updateWageSummary(EditTextPreference wageEditPref, String newVal){
        NumberFormat format = NumberFormat.getCurrencyInstance();
        float wageFloat;
        try {
            wageFloat = Float.parseFloat(newVal);
        } catch (NumberFormatException e){
            e.printStackTrace();
            wageFloat = 0f;
        }
        wageEditPref.setTitle(String.format("Wage Rate: %s", format.format(wageFloat)));
    }

    private boolean validateWage(Object wageObj){
        if(!(wageObj instanceof String)){
            return false;
        }

        String wageString = (String) wageObj;

        float val;

        try{
            val = Float.parseFloat(wageString);
            return val >= 0f && val <= 1000f;

        } catch (NumberFormatException e){
            e.printStackTrace();
            return false;
        }
    }
}


