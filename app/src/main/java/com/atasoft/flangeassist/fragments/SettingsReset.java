package com.atasoft.flangeassist.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.atasoft.flangeassist.R;

public class SettingsReset extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_reset);
        

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addListener();
    }

    private void addListener(){
        final Preference resetPref = findPreference("reset_switch");
        if(resetPref == null){
            Log.e("SettingsReset", "reset_switch came back null");
            return;
        }
        resetPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference arg0) {
                resetPrefs();
                return true;
            }
        });
    }
    
    private void resetPrefs(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();
        Toast.makeText(getActivity().getApplicationContext(), "Settings Reset to Default", Toast.LENGTH_SHORT).show();
        //getActivity().finish();
    }
}


