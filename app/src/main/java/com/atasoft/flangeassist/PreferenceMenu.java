package com.atasoft.flangeassist;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

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
            
            if(android.os.Build.VERSION.SDK_INT >= 14) {
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
            } else {
            	addPreferencesFromResource(R.xml.prefnoclick);
            }
            
        }
    }	

}

    
