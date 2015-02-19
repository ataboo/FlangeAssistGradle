package com.atasoft.flangeassist;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.atasoft.helpers.TaxManager;

public class PreferenceMenu extends PreferenceActivity {
    @Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);       
		getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();
		}
    
    public static class PrefsFragment extends PreferenceFragment {
    	 
        @SuppressLint("NewApi")
		@Override
        public void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            Preference resetPref = findPreference("reset_switch");

            resetPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference arg0) {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    Editor editor = prefs.edit();
                    editor.clear();
                    editor.commit();
                    getActivity().finish();
                    return true;
                }

            });

            ListPreference provListPref = (ListPreference) findPreference("list_provWageNew");
            provListPref.setEntries(TaxManager.getActiveProvinceStrings());
            provListPref.setEntryValues(TaxManager.getActiveProvinceStrings());
            provListPref.setValueIndex(TaxManager.PROV_AB);
            provListPref.setDefaultValue(TaxManager.getActiveProvinceStrings()[1]);  //best province

            ListPreference yearListPref = (ListPreference) findPreference("list_taxYearNew");
            yearListPref.setEntries(TaxManager.yearStrings);
            yearListPref.setEntryValues(TaxManager.yearStrings);
            yearListPref.setValueIndex(TaxManager.yearStrings.length - 1);
            yearListPref.setDefaultValue(TaxManager.yearStrings[TaxManager.yearStrings.length-1]);
        }
    }
}

    
