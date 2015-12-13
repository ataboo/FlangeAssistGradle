package com.atasoft.flangeassist;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.support.v7.widget.Toolbar;

import com.atasoft.flangeassist.fragments.SettingsCustomDays;
import com.atasoft.flangeassist.fragments.SettingsRates;
import com.atasoft.flangeassist.fragments.SettingsRegional;
import com.atasoft.flangeassist.fragments.SettingsReset;

import java.util.List;

public class PreferenceMenu extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setupToolBar();
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.setting_headers, target);
    }
    
    private void setupToolBar(){
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        LinearLayout content = (LinearLayout) root.getChildAt(0);
        LinearLayout toolbarContainer = (LinearLayout) View.inflate(this, R.layout.settings_layout, null);
        root.removeAllViews();
        toolbarContainer.addView(content);
        root.addView(toolbarContainer);
        
        Toolbar toolbar = (Toolbar) toolbarContainer.findViewById(R.id.toolbar);
        toolbar.setTitle("Toolbox Settings");
        toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private static final String[] fragNames = {
            SettingsRegional.class.getName(),
            SettingsRates.class.getName(),
            SettingsCustomDays.class.getName(),
            SettingsReset.class.getName()};
    
    @Override
    protected boolean isValidFragment(String fragmentName) {
        for(String fragName: fragNames){
                if(fragName.equals(fragmentName)) return true;
            }
        return false;
    }
    
    /*
    
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
    
    */
}

    
