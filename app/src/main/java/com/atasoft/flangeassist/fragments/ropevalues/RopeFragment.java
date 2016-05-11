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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        thisView = inflater.inflate(R.layout.rope_val, container, false);
        setupSpinners();
        setEditListener();
        return thisView;
    }

    private void setEditListener() {
        inBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                updateConversion();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int count, int after) {
            }
        });
    }

    //------------------Rope Functions------------------
    private Spinner ropeTypeSpinner;
    private NumberPicker ropeDiameterPicker;
    private TextView breakStrengthText;
    private TextView wllText;
    private TextView boltSpacingText;
    private TextView boltCountText;


    private void setupSpinners() {
        ropeTypeSpinner = (Spinner) thisView.findViewById(R.id.rope_type_spinner);
        ropeDiameterPicker = (NumberPicker) thisView.findViewById(R.id.rope_diameter_picker);
        breakStrengthText = (TextView) thisView.findViewById(R.id.rope_bs_text);
        wllText = (TextView) thisView.findViewById(R.id.rope_wll_text);
        boltSpacingText = (TextView) thisView.findViewById(R.id.rope_bolt_spacing_text);
        boltCountText = (TextView) thisView.findViewById(R.id.rope_bolt_count_text);


        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, RopeData.typeStrings);
        typeSpin.setAdapter(typeAdapter);

        typeSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                refreshUnits();
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        refreshUnits();
    }

    private String oldType = null;

    private void refreshUnits() {
        String type = (String) typeSpin.getSelectedItem();
        if (!type.equals(oldType)) {
            String[] unitStrings = dataHold.getUnitNames(type);
            ArrayAdapter<String> unitAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, unitStrings);
            unitSpin1.setAdapter(unitAdapter);
            unitSpin2.setAdapter(unitAdapter);
            if (unitStrings.length > 1) {  //Do(d)ge index out of range... Such Index.
                unitSpin2.setSelection(1);
            }
        }
        this.oldType = type;
    }

    private void updateConversion() {
        String inText = inBox.getText().toString();
        double inVal = 0;
        try {
            inVal = Double.parseDouble(inText);
        } catch (NumberFormatException e) {
            Log.e("UnitConFrag", "failed to parse input.");
        }
        String unit1 = unitSpin1.getSelectedItem().toString();
        String unit2 = unitSpin2.getSelectedItem().toString();
        String unitShorthand = dataHold.getUnit(oldType, unit2);
        double result = dataHold.convertValue(inVal, oldType, unit1, unit2);
        String fracResult = dataHold.makeFraction(result, 16);

        outBox.setText(String.format("%s %s", result, unitShorthand));
        fracBox.setText(String.format("%s %s (Nearest 16th)", fracResult, unitShorthand));
    }
}
