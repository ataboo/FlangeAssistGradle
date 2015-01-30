package com.atasoft.flangeassist.fragments;

import android.content.*;
import android.os.*;
import android.preference.*;
import android.support.v4.app.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.atasoft.flangeassist.*;
import com.atasoft.helpers.*;

public class FlangeFragment extends Fragment {
    View thisFrag;
	Context context;
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.flanges, container, false);
        thisFrag = v;
        this.context = v.getContext();
		
		setupSpinners();
        loadPrefs();

        return v;
    }

	@Override
	public void onResume()
	{
		loadPrefs();
		super.onResume();
	}

	@Override
	public void onPause()
	{
		savePrefs();
		super.onPause();
	}
	
	Spinner rateS;
	Spinner sizeS;
	JsonPuller jPuller;
	TextView sDiamVal;
	TextView wrenchVal;	
	TextView driftVal;
	TextView sCountVal;
	TextView sLengthVal;
	TextView b7Val;
	TextView b7mVal;
	String[] fSizesCombined;
	String[] fSizes;
	String[] fRates;

    Spinner studSizeSpinner;
    TextView studWrenchVal;
    TextView studDriftVal;
    TextView studB7Val;
    TextView studB7MVal;
    String[] fStuds;

	SharedPreferences prefs;
	private void setupSpinners() {
		this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
		this.jPuller = new JsonPuller(thisFrag);
		this.fSizes = jPuller.getSizes();
		this.fRates = jPuller.getRates();
        this.fStuds = jPuller.getStudSizes();

		this.sizeS = (Spinner) thisFrag.findViewById(R.id.sizeSpinner);
		ArrayAdapter<String> adaptorSize = new ArrayAdapter<String>(getActivity().getApplicationContext(),
																 android.R.layout.simple_spinner_item, fSizes);
		sizeS.setAdapter(adaptorSize);
		
		this.rateS = (Spinner) thisFrag.findViewById(R.id.rateSpinner);
        ArrayAdapter<String> adapterRate = new ArrayAdapter<String>(getActivity().getApplicationContext(),
                android.R.layout.simple_spinner_item, fRates);
        rateS.setAdapter(adapterRate);

        this.studSizeSpinner = (Spinner) thisFrag.findViewById(R.id.stud_spinner);
        ArrayAdapter<String> adapterStud = new ArrayAdapter<String>(getActivity().getApplicationContext(),
            android.R.layout.simple_spinner_item, fStuds);
        studSizeSpinner.setAdapter(adapterStud);

        //Set Listeners on spinners to update stats
        rateS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
		    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                spinSend();
			}
			public void onNothingSelected(AdapterView<?> parent){}
		});
		sizeS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                spinSend();
            }
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
        studSizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                studSpinSend();
            }
            public void onNothingSelected(AdapterView<?> parent){}
        });

		this.sDiamVal = (TextView) thisFrag.findViewById(R.id.sDiamVal);
		this.wrenchVal = (TextView) thisFrag.findViewById(R.id.wrenchVal);
		this.driftVal = (TextView) thisFrag.findViewById(R.id.driftVal);
		this.sCountVal = (TextView) thisFrag.findViewById(R.id.sCountVal);
		this.sLengthVal = (TextView) thisFrag.findViewById(R.id.sLengthVal);
		this.b7Val = (TextView) thisFrag.findViewById(R.id.b7Val);
		this.b7mVal = (TextView) thisFrag.findViewById(R.id.b7MVal);

        this.studWrenchVal = (TextView) thisFrag.findViewById(R.id.stud_wrenchVal);
        this.studDriftVal = (TextView) thisFrag.findViewById(R.id.stud_driftVal);
        this.studB7Val = (TextView) thisFrag.findViewById(R.id.stud_b7Val);
        this.studB7MVal = (TextView) thisFrag.findViewById(R.id.stud_b7mVal);
	}

	String[] flangeVals;
	String[] studVals;
	private void spinSend() {
		String fSize = (String) sizeS.getSelectedItem();
		String fRate = (String) rateS.getSelectedItem();
		this.flangeVals = jPuller.pullFlangeVal(fSize, fRate);
		if(flangeVals == null){
			Log.e("FlangeFragment", "jPuller Returning null flangeVal");
			displayErr(true);
			return;
		}
		
		if(flangeVals[0].startsWith("-")){
			displayErr(false);
			return;
		}
		this.studVals = jPuller.pullStudVal(flangeVals[0]);
		
		if(studVals == null){
			displayErr(true);
			Log.e("FlangeFragment","jPuller returning null studVals");
			return;
		}
		
		displayVals();
	}
	
	private void displayVals() {
		sDiamVal.setText(flangeVals[0] + "\"");
		wrenchVal.setText(studVals[0] + "\"");
		driftVal.setText(studVals[1] + "\"");
		sCountVal.setText(flangeVals[2]);
		sLengthVal.setText(flangeVals[3] + "\"");
		b7Val.setText(studVals[3] + " ft-lbs");
		b7mVal.setText(studVals[2] + " ft-lbs");
		return;
	}
	
	private void displayErr(boolean realError){
		if(realError){
			sDiamVal.setText("err\"");
			wrenchVal.setText("err\"");
			driftVal.setText("err\"");
			sCountVal.setText("err");
			sLengthVal.setText("err\"");
			b7Val.setText("err ft-lbs");
			b7mVal.setText("err ft-lbs");
		} else {
			sDiamVal.setText("-\"");
			wrenchVal.setText("-\"");
			driftVal.setText("-\"");
			sCountVal.setText("-");
			sLengthVal.setText("-\"");
			b7Val.setText("- ft-lbs");
			b7mVal.setText("- ft-lbs");	
		}
	}

    private void studSpinSend(){
        // wrench size, drift pin, b7m, b7
        String[] studValArray = jPuller.pullStudVal(studSizeSpinner.getSelectedItem().toString());
        if(studValArray == null){
            studValArray = new String[]{"-","-","-","-"};
            studWrenchVal.setText(studValArray[0]);
            studDriftVal.setText(studValArray[1]);
            studB7Val.setText(studValArray[3]);
            studB7MVal.setText(studValArray[2]);
            return;
        }

        studWrenchVal.setText(studValArray[0] + "\"");
        studDriftVal.setText(studValArray[1] + "\"");
        studB7Val.setText(studValArray[3] + " ft-lbs");
        studB7MVal.setText(studValArray[2] + " ft-lbs");
    }
	
	private void loadPrefs(){
		sizeS.setSelection(prefs.getInt("ATA_flangeSize", 0));
		rateS.setSelection(prefs.getInt("ATA_flangeRate", 0));
        studSizeSpinner.setSelection(prefs.getInt("ATA_flangeStud", 0));
	}
	
	private void savePrefs(){
		SharedPreferences.Editor prefEdit = prefs.edit();
		prefEdit.putInt("ATA_flangeSize", sizeS.getSelectedItemPosition());
		prefEdit.putInt("ATA_flangeRate", rateS.getSelectedItemPosition());
        prefEdit.putInt("ATA_flangeStud", studSizeSpinner.getSelectedItemPosition());
		prefEdit.apply();
	}
}
