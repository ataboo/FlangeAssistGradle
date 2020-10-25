package com.atasoft.flangeassist.fragments;

import android.content.*;
import android.graphics.*;
import android.os.*;
import android.preference.*;
import androidx.core.app.*;
import android.text.*;
import android.util.*;
import android.view.*;
import android.widget.*;

import androidx.fragment.app.Fragment;

import com.atasoft.flangeassist.*;

public class NozzleCalc extends Fragment
{
    private View thisFrag;
	private Context context;
	private SharedPreferences prefs;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.nozzlecalc_layout, container, false);
        this.thisFrag = v;
		this.context = getActivity().getApplicationContext();
		this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
		
		setupViews();
		setListeners();
	
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
	private EditText shellODEdit;
	private EditText nozzleCentAngleEdit;
	private EditText nozzleODEdit;
	private TextView nozzleCentArcText;
	private TextView nozzleEdgeAngleText;
	private TextView nozzleEdgeArcText;
	private void setupViews() {
		this.shellODEdit = (EditText) thisFrag.findViewById(R.id.nozzle_shellODEdit);
		this.nozzleCentAngleEdit = (EditText) thisFrag.findViewById(R.id.nozzle_angleEdit);
		this.nozzleODEdit = (EditText) thisFrag.findViewById(R.id.nozzle_nozODEdit);
		this.nozzleCentArcText = (TextView) thisFrag.findViewById(R.id.nozzle_arcCenterText);
		this.nozzleEdgeAngleText = (TextView) thisFrag.findViewById(R.id.nozzle_angleEdgeText);
		this.nozzleEdgeArcText = (TextView) thisFrag.findViewById(R.id.nozzle_arcEdgesText);
    }

	private boolean listenFlag = false;
	private void setListeners(){
		if(listenFlag) return; 
		shellODEdit.addTextChangedListener(new TextWatcher(){
				@Override
				public void afterTextChanged(Editable s){
					updateNozzleCalc();
				}
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after){}
				@Override
				public void onTextChanged(CharSequence s, int start, int count, int after){}
			});
		nozzleCentAngleEdit.addTextChangedListener(new TextWatcher(){
				@Override
				public void afterTextChanged(Editable s){
					updateNozzleCalc();
				}
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after){}
				@Override
				public void onTextChanged(CharSequence s, int start, int count, int after){}
			});
		nozzleODEdit.addTextChangedListener(new TextWatcher(){
				@Override
				public void afterTextChanged(Editable s){
					updateNozzleCalc();
				}
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after){}
				@Override
				public void onTextChanged(CharSequence s, int start, int count, int after){}
			});
		listenFlag = true;
    }

	//----------------Updating Functions---------------
	private double shellOD;
	private double centAngle;
	private double nozzleOD;
	private void updateNozzleCalc(){
		this.shellOD = getDoubleFromEdit(shellODEdit, 0.01d, 1000000000);
		this.centAngle = getDoubleFromEdit(nozzleCentAngleEdit, 0, 180);
		this.nozzleOD = getDoubleFromEdit(nozzleODEdit, 0.01d, shellOD / 2);
		String nozEdgeAng = "error";
		String nozEdgeArc = "error";
		String nozCentArc = "error";
		double centAngleRads = Math.toRadians(centAngle);
		if(shellOD >= 0 && centAngle >= 0){
			double nozCentArcD = centAngleRads * (shellOD / 2);
			nozCentArc = Double.toString(digitRound(nozCentArcD, 4));
			if(nozzleOD >= 0){
				double deltaAngle = 
					Math.toDegrees(
						Math.asin(
							(nozzleOD/2)/(shellOD/2)
						)
					);
				double[] edgeAngles = {centAngle - deltaAngle, centAngle + deltaAngle};
				double[] edgeArcLengths = {shellOD/2 * Math.toRadians(edgeAngles[0]), shellOD/2 * Math.toRadians(edgeAngles[1])};
				nozEdgeAng = String.format("%s°, %s°", Double.toString(digitRound(edgeAngles[0], 2)), Double.toString(digitRound(edgeAngles[1], 4)));
				nozEdgeArc = String.format("%s, %s", Double.toString(digitRound(edgeArcLengths[0], 2)), Double.toString(digitRound(edgeArcLengths[1], 4)));
			}
		}
		
		nozzleCentArcText.setText("Nozzle Center Arclength: " + nozCentArc);
		nozzleEdgeAngleText.setText("Nozzle Edge Angles: " + nozEdgeAng);
		nozzleEdgeArcText.setText("Nozzle Edge Arclengths: " + nozEdgeArc);
	}
	
	private double digitRound(double val, int digits){
		double factor = Math.pow(10, digits);
		return Math.round(val * factor) / factor;
	}
	
	private double getDoubleFromEdit(EditText eText, double floor, double ceiling) throws NumberFormatException{
		double parseVal = -1d;
		try{
			parseVal = Double.parseDouble(eText.getText().toString()); 
		} catch(NumberFormatException e){
			eText.setTextColor(Color.RED);
			Log.e("NozzleCalc", "Exception parsing an EditText.");
		}
		if(parseVal < floor || parseVal > ceiling){
			eText.setTextColor(Color.RED);
			parseVal = -1d;
		} else {
			eText.setTextColor(Color.WHITE);
		}
		return parseVal;
	}
	
	private void savePrefs(){
		SharedPreferences.Editor prefEdit = prefs.edit();
		prefEdit.putFloat("ATA_nozzleShellOD", (float) shellOD);
		prefEdit.putFloat("ATA_nozzleAngle", (float) centAngle);
		prefEdit.putFloat("ATA_nozzleOD", (float) nozzleOD);
		prefEdit.apply();
	}
	
	private void loadPrefs(){
		shellODEdit.setText(Float.toString(prefs.getFloat("ATA_nozzleShellOD", 10f)));
		nozzleCentAngleEdit.setText(Float.toString(prefs.getFloat("ATA_nozzleAngle", 45f)));
		nozzleODEdit.setText(Float.toString(prefs.getFloat("ATA_nozzleOD", 2f)));
	}
}
