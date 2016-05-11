package com.atasoft.flangeassist.fragments.ropevalues;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.atasoft.flangeassist.R;
import com.atasoft.helpers.ConvDataHold;

/**
 * Created by ataboo on 2016-05-10.
 */

public class RopeFragment extends android.support.v4.app.Fragment {

    private View thisView;
    private Spinner ropeTypeSpinner;
    private Spinner ropeSafetySpinner;
    private NumberPicker ropeDiameterPicker;
    private TextView breakStrengthText;
    private TextView wllText;
    private TextView boltSpacingText;
    private TextView boltCountText;

    private RopeData ropeData = new RopeData();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        thisView = inflater.inflate(R.layout.rope_val, container, false);
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
        Log.w("RopeFragment", "Updated Output.");


    }
}
