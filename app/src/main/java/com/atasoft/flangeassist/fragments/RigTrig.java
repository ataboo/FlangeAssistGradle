package com.atasoft.flangeassist.fragments;

import android.content.*;
import android.graphics.*;
import android.os.*;
import android.preference.*;
import android.support.v4.app.*;
import android.text.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.atasoft.flangeassist.*;

public class RigTrig extends Fragment
{
    private View thisFrag;
	private Context context;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.rigtrig_layout, container, false);
        this.thisFrag = v;
		this.context = getActivity().getApplicationContext();

		setupViews();
		setListeners();
		updateSlingCalc();
		updateNylon();
		
		return v;
    }

	@Override
	public void onPause()
	{
		savePrefs();
		super.onPause();
	}

	@Override
	public void onResume()
	{
		loadPrefs();
		super.onResume();
	}
	
	//-------------------Startup Functions-----------
	private static final String[] WRAP_TYPES = {"Vertical (1x)", "Choke (.75x)", "Basket (2x)"};
	//Sizes[], Vertical[], Choker[], Basket[] 
	private static final String[][] NYLON_CAPACITIES = new String[][] {
		{"1\"", "2\"", "3\"", "4\"", "6\"", "8\"", "10\"", "12\""},
		{"3,200", "6,200", "9,400", "12,400", "16,500", "22,000", "24,000", "28,800"},
		{"2,600", "5,000", "7,500", "10,000", "12,400", "16,500", "18,000", "21,600"},
		{"6,400", "12,400", "18,800", "24,800", "33,000", "44,000", "48,000", "57,600"}};
	private Spinner nylonSizeSpin;
	private Spinner nylonWrapSpin;
	private TextView nylonCapText;
		
	private Spinner legSpin;
	private Spinner wrapSpin;
	private EditText loadEdit;
	private EditText angleEdit;
	private TextView slingCapText;
	private TextView legWarningText;	
	private SharedPreferences prefs;
	private void setupViews() {
		this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
		
		//-------------Calculator--------------
		this.legSpin = (Spinner) thisFrag.findViewById(R.id.rigtrig_legsSpinner);
		String[] legsString = new String[8];
		for(int i=0; i<8; i++){
			legsString[i] =Integer.toString(i+1);
		} 
		ArrayAdapter<String> legAd = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, legsString);
		legSpin.setAdapter(legAd);
		
		this.wrapSpin = (Spinner) thisFrag.findViewById(R.id.rigtrig_wrapSpinner);
		ArrayAdapter<String> wrapAd = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, WRAP_TYPES);
		wrapSpin.setAdapter(wrapAd);
		
		this.loadEdit = (EditText) thisFrag.findViewById(R.id.rigtrig_loadEdit);
		this.angleEdit = (EditText) thisFrag.findViewById(R.id.rigtrig_angleEdit);
		this.slingCapText = (TextView) thisFrag.findViewById(R.id.rigtrig_slingCapText);
		this.legWarningText = (TextView) thisFrag.findViewById(R.id.rigtrig_legWarning);
		
		//-------------------Nylon Table------------------
		this.nylonSizeSpin = (Spinner) thisFrag.findViewById(R.id.rigtrig_nylonSizeSpin);
		ArrayAdapter<String> nylonSizeAd = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, NYLON_CAPACITIES[0]);
		nylonSizeSpin.setAdapter(nylonSizeAd);
		
		this.nylonWrapSpin = (Spinner) thisFrag.findViewById(R.id.rigtrig_nylonWrapSpin);
		nylonWrapSpin.setAdapter(wrapAd);
		
		this.nylonCapText = (TextView) thisFrag.findViewById(R.id.rigtrig_nylonTableCapacity);

    }
	
	private boolean listenFlag = false;
	private void setListeners(){
		if(listenFlag) return; 
		
		//----------Sling Calculator Listeners-----------------
		loadEdit.addTextChangedListener(new TextWatcher(){
				@Override
				public void afterTextChanged(Editable s){
					updateSlingCalc();
				}
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after){}
				@Override
				public void onTextChanged(CharSequence s, int start, int count, int after){}
		});
		angleEdit.addTextChangedListener(new TextWatcher(){
				@Override
				public void afterTextChanged(Editable s){
					updateSlingCalc();
				}
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after){}
				@Override
				public void onTextChanged(CharSequence s, int start, int count, int after){}
		});
		legSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
		        public void onItemSelected(AdapterView<?> parent, View view,
										   int pos, long id) {updateSlingCalc();}
				public void onNothingSelected(AdapterView<?> parent) {
				}
			});
		wrapSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
		        public void onItemSelected(AdapterView<?> parent, View view,
										   int pos, long id) {updateSlingCalc();}
				public void onNothingSelected(AdapterView<?> parent) {
				}
			});
			
		//--------------------Nylon Table Listeners--------------
		nylonSizeSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
		        public void onItemSelected(AdapterView<?> parent, View view,
										   int pos, long id) {updateNylon();}
				public void onNothingSelected(AdapterView<?> parent) {
				}
			});
		nylonWrapSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
		        public void onItemSelected(AdapterView<?> parent, View view,
										   int pos, long id) {updateNylon();}
				public void onNothingSelected(AdapterView<?> parent) {
				}
			});
		
		listenFlag = true;
    }

	//----------------Updating Functions---------------
	
	private void updateSlingCalc(){
		double loadVal = parseFromEditText(loadEdit, "Load EditText");
		double cageLoad = cageDouble(loadVal, 0, 1000000d);	
		if(loadVal != cageLoad){
			loadEdit.setTextColor(Color.RED);
			loadVal = cageLoad;
		} else {
			loadEdit.setTextColor(Color.WHITE);
		}		
		
		double angleVal;
		//if 1 leg than angle deactivated
		if(legSpin.getSelectedItemPosition() == 0){
			angleEdit.setTextColor(Color.GRAY);
			angleEdit.setEnabled(false);
			angleVal = 90d;
		} else {
			angleEdit.setEnabled(true);
			angleVal = parseFromEditText(angleEdit, "Angle EditText");
			double cageAngle = cageDouble(angleVal, 5d, 90d);
			if(angleVal != cageAngle){
				angleEdit.setTextColor(Color.RED);
				angleVal = cageAngle;
			} else {
				angleEdit.setTextColor(Color.WHITE);
			}
		}
		
		if(legSpin.getSelectedItemPosition() > 2){
			legWarningText.setVisibility(TextView.VISIBLE);
		} else {
			legWarningText.setVisibility(TextView.INVISIBLE);
		}
		
		int wrapIndex = wrapSpin.getSelectedItemPosition();
		// [1x, 0.75x, 2x]
		if(wrapIndex > 0) {
			loadVal = (wrapIndex == 1) ? loadVal / 0.75d: loadVal / 2;
		}
		double slingCapVal = cageDouble(calculateSlingCapacity(loadVal, legSpin.getSelectedItemPosition() + 1, angleVal), 0, 1000000000d);
		
		slingCapText.setText(String.format("Minimum Sling Capacity: %s", Double.toString(slingCapVal)));
	}
	
	private void updateNylon(){
		
		
		nylonCapText.setText(String.format("2-ply Nylon Rated Capacity: %s", NYLON_CAPACITIES[nylonWrapSpin.getSelectedItemPosition() + 1][nylonSizeSpin.getSelectedItemPosition()]));
	}
	
	private void savePrefs(){
		SharedPreferences.Editor prefEdit = prefs.edit();
		prefEdit.putInt("ATA_rigtrigLegs", legSpin.getSelectedItemPosition());
		prefEdit.putInt("ATA_rigtrigWrap", wrapSpin.getSelectedItemPosition());
		prefEdit.putString("ATA_rigtrigLoad", loadEdit.getText().toString());
		prefEdit.putString("ATA_rigtrigAngle", angleEdit.getText().toString());
		prefEdit.apply();
	}
	
	private void loadPrefs(){
		legSpin.setSelection(prefs.getInt("ATA_rigtrigLegs", 0));
		wrapSpin.setSelection(prefs.getInt("ATA_rigtrigWrap", 0));
		loadEdit.setText(prefs.getString("ATA_rigtrigLoad", "2000"));
		angleEdit.setText(prefs.getString("ATA_rigtrigAngle", "45"));
	}
	
	private double calculateSlingCapacity(double load, int legs, double angle){
		double angleRad = Math.toRadians(angle);
		return ((load /(double) legs) / Math.sin(angleRad));
		//Toast.makeText(context, String.format("Load: %.2f, Legs: %d, Angle: %.2f", load, legs, angle), Toast.LENGTH_SHORT).show();
	}
	
	private double parseFromEditText(EditText eText, String identity) throws NumberFormatException{
		double parsedDouble = 0;
		try{
			parsedDouble = Double.parseDouble(eText.getText().toString());
		} catch (NumberFormatException e){
			Log.e("RigTrig", String.format("%s threw NumberFormatException.", identity));
		}
		return parsedDouble;
	}
	
	private double cageDouble(double value, double floor, double ceiling){
		value = (value < floor) ? floor: (value > ceiling) ? ceiling : value;
		value = Math.round(value * 100d) / 100d; 
		return value;
	}
}
