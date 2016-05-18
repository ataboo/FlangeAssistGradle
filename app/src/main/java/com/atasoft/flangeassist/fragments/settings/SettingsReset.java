package com.atasoft.flangeassist.fragments.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
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

    private void addListener() {
        final Preference resetPref = findPreference("reset_switch");
        if (resetPref != null) {
            resetPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference arg0) {
                    showDialog();
                    return true;
                }
            });
        }
    }

    private void showDialog() {
        int version = Build.VERSION.SDK_INT;
        int verM = Build.VERSION_CODES.M;
        new AlertDialog.Builder(getPreferenceScreen().getContext())
                .setTitle("Reset All Settings")
                .setMessage("Are you sure you want to reset all settings?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        resetPrefs();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void resetPrefs() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();
        Toast.makeText(getActivity().getApplicationContext(), "Settings Reset to Default", Toast.LENGTH_SHORT).show();
        //getActivity().finish();
    }
}


