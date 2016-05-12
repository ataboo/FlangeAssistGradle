package com.atasoft.flangeassist.fragments.ropevalues;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.atasoft.flangeassist.R;

/**
 * Created by ataboo on 2016-05-10.
 */

public class RopeFragment extends android.support.v4.app.Fragment {

    public enum PrefKey {
        ROPE_TYPE("rope_type_index"),
        ROPE_DIAMETER("rope_diameter_index"),
        ROPE_SAFETY("rope_safety_index");

        public final String keyVal;
        PrefKey(String strVal){
            keyVal = strVal;
        }
    }

    private View thisView;
    private Spinner ropeTypeSpinner;
    private Spinner ropeSafetySpinner;
    private NumberPicker ropeDiameterPicker;
    private TextView breakStrengthText;
    private TextView wllText;
    private TextView boltSpacingText;
    private TextView boltCountText;

    private RopeData ropeData = new RopeData();
    private SharedPreferences prefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        thisView = inflater.inflate(R.layout.rope_val, container, false);
        this.prefs = PreferenceManager.getDefaultSharedPreferences(thisView.getContext());

        setupSpinners();
        return thisView;
    }
    private void setupSpinners() {

        //--------------------------------Find Views-----------------------------
        ropeTypeSpinner = (Spinner) thisView.findViewById(R.id.rope_type_spinner);
        ropeSafetySpinner = (Spinner) thisView.findViewById(R.id.rope_safety_spinner);
        ropeDiameterPicker = (NumberPicker) thisView.findViewById(R.id.rope_diameter_picker);
        breakStrengthText = (TextView) thisView.findViewById(R.id.rope_bs_text);
        wllText = (TextView) thisView.findViewById(R.id.rope_wll_text);
        boltSpacingText = (TextView) thisView.findViewById(R.id.rope_bolt_spacing_text);
        boltCountText = (TextView) thisView.findViewById(R.id.rope_bolt_count_text);


        //------------------------------Populate Spinners/Picker-------------------
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, ropeData.getTypeStrings() );
        ropeTypeSpinner.setAdapter(typeAdapter);

        ArrayAdapter<String> safetyAdaptor = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, ropeData.getSafetyStrings());
        ropeSafetySpinner.setAdapter(safetyAdaptor);

        String[] diameterStrings = ropeData.getSizeStrings();
        ropeDiameterPicker.setMinValue(0);
        ropeDiameterPicker.setMaxValue(diameterStrings.length - 1);
        ropeDiameterPicker.setDisplayedValues(diameterStrings);
        ropeDiameterPicker.setWrapSelectorWheel(false);
        // Keep from bringing up keyboard in picker.
        ropeDiameterPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        //----------------------------Set to Saved Values----------------------

        setViewsFromPrefs();

        //--------------------------Spinner Listeners--------------------------
        AdapterView.OnItemSelectedListener spinListener = new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                updateOutput();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

        ropeTypeSpinner.setOnItemSelectedListener(spinListener);
        ropeSafetySpinner.setOnItemSelectedListener(spinListener);

        //------------------------Picker Listener----------------------------------
        ropeDiameterPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                updateOutput();
            }
        });

        //------------------------Initialize Output--------------------------------
        updateOutput();
    }

    private void updateOutput(){

        RopeType ropeType = ropeData.getTypeAtIndex(ropeTypeSpinner.getSelectedItemPosition());
        RopeSafety ropeSafety = ropeData.getSafetyAtIndex(ropeSafetySpinner.getSelectedItemPosition());
        RopeSize ropeSize = ropeData.getSizeAtIndex(ropeDiameterPicker.getValue());

        float breakStrength = ropeSize.getBreakStrength(ropeType);
        float workLoadLimit = ropeSize.getWorkLoadLimit(ropeType, ropeSafety.getFactor());
        MixedFraction uBoltSpacing = new MixedFraction(ropeSize.getBoltSpacing());
        int uBoltCount = ropeSize.getBoltCount();

        String breakString = breakStrength < 1f ? String.format("Break Strength:  %.0f lbs", breakStrength * 2000f) :
                String.format("Break Strength:  %.2f tons", breakStrength);
        String wllString = workLoadLimit < 1f ? String.format("Load Limit (WLL):  %.0f lbs", workLoadLimit * 2000f) :
                String.format("Load Limit (WLL):  %.2f tons", workLoadLimit);

        breakStrengthText.setText(breakString);
        wllText.setText(wllString);
        boltCountText.setText(String.format("Lashing Clip Spacing:  %s", uBoltSpacing.toString()));
        boltSpacingText.setText(String.format("Lashing Clip Count:  %d", uBoltCount));

        setPrefsFromViews();
    }

    private void setViewsFromPrefs(){
        // Default IWRC
        setSpinnerIndex(prefs.getInt(PrefKey.ROPE_TYPE.keyVal, 0), ropeTypeSpinner);
        // Default 5x safety factor
        setSpinnerIndex(prefs.getInt(PrefKey.ROPE_SAFETY.keyVal, 1), ropeSafetySpinner);

        // Default 1/2" rope
        setPickerIndex(prefs.getInt(PrefKey.ROPE_DIAMETER.keyVal, 7), ropeDiameterPicker);
    }

    private void setPrefsFromViews(){
        SharedPreferences.Editor pEdit = prefs.edit();
        pEdit.putInt(PrefKey.ROPE_TYPE.keyVal, ropeTypeSpinner.getSelectedItemPosition());
        pEdit.putInt(PrefKey.ROPE_SAFETY.keyVal, ropeSafetySpinner.getSelectedItemPosition());
        pEdit.putInt(PrefKey.ROPE_DIAMETER.keyVal, ropeDiameterPicker.getValue());
        pEdit.apply();
    }

    private static void setSpinnerIndex(int indexVal, Spinner spinner){
        int adaptorCount = spinner.getAdapter().getCount();

        if(indexVal >= adaptorCount || indexVal < 0){
            Log.e("RopeFragment", String.format("Tried to set spinner from prefs but indexVal %d was out of range of %d.", indexVal, adaptorCount));
            return;
        }

        spinner.setSelection(indexVal, false);
    }

    private static void setPickerIndex(int indexVal, NumberPicker picker){
        if(indexVal > picker.getMaxValue() || indexVal < 0){
            Log.e("RopeFragment", String.format("Tried to set spinner from prefs but indexVal %d was out of range of %d.", indexVal, picker.getMaxValue()));
            return;
        }

        picker.setValue(indexVal);
    }
}
