package com.atasoft.flangeassist.fragments;

import android.os.*;
import android.support.v4.app.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.atasoft.flangeassist.*;

public class WageCPIEstimate extends Fragment implements OnClickListener
{
    private View thisFrag;
	boolean beenLoaded = false;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.wage_estimate_layout, container, false);
        this.thisFrag = v;
		
		setupSpinners();
		//Log.w("pci estimate", "ran create view");
        return v;
    }
	
	@Override
	public void onResume() {
		super.onResume();
	}

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
			case R.id.cpi_send:
				sendPush();
				break;
        }
    }

	//Calculates and holds the wage increase values when given CPI (Canada Price Index) rate and WTC (West Texas Crude) Price
	public class CPIHolder {
		public float cpiRate;
		public float oilPrice;
		public float wageRate;
		public float wageAnnualRate;
		public float wageAnnualCapped;
		public float wageIncrease;
		public float finalWage;
		public float oilBonus;
		public float semiAnnualCapped;
		public String flagStrings;
		
		//These values updates as contracts change
		private final float[] oilBrackets = {0, 60, 90, 110, 125};
		private final float[] oilBonuses = {0, 0, 0.005f, 0.01f, 0.015f};
		private final float annualCap = 0.05f;  //Annual rate increase cannot exceed 5%
		
		public void calcRate(float cpiRateIn, float oilPriceIn, float wageRate){
			this.cpiRate = cpiRateIn;
			this.oilPrice = oilPriceIn;
			this.wageRate = wageRate;
			this.flagStrings = "";
			
			if(oilPrice < oilBrackets[1]){  //If oil is lower than 60 no increase at all
				this.cpiRate = 0f;
				this.oilBonus = 0f;
				this.flagStrings = String.format("If West Texas Crude is below %.0f$/BBL there is no wage Increase.", oilBrackets[1]);
			} else {
				this.oilBonus = (oilPrice > oilBrackets[4] ? oilBonuses[4] : 
								oilPrice > oilBrackets[3] ? oilBonuses[3] :
								oilPrice > oilBrackets[2] ? oilBonuses[2] : 
								oilBonuses[1]);
			}
			
			this.wageAnnualRate = oilBonus + cpiRate;
			this.wageAnnualCapped = wageAnnualRate;
			if(wageAnnualRate > annualCap) {
				flagStrings += flagStrings == "" ? "" : "\n";
				this.flagStrings += "Total raise cannot exceed 5% in a year.";
				this.wageAnnualCapped = annualCap;
			}
			//semi-annual only capped to annualcap not annualcap/2 by contract
			this.semiAnnualCapped = wageAnnualRate / 2 < annualCap ? wageAnnualRate / 2 : annualCap;
			this.wageIncrease = wageRate * semiAnnualCapped;
			this.finalWage = wageRate + wageIncrease;

        }
	}

    private EditText cpiWageEdit;
	private NumberPicker cpiRatePick;
	private NumberPicker wtcPricePick;
	private String[] cpiValStrings;
	private String[] wtcValStrings;
	//private TextView textStartWage;
	private TextView textAnnualRate;
	private TextView textSemiRate;
	private TextView textSemiAmount;
	private TextView textFinalWage;
	private TextView textMessages;
	
	private CPIHolder cpiHolder;
	private void setupSpinners() {
		this.cpiWageEdit = (EditText) thisFrag.findViewById(R.id.cpi_wageedit);
		cpiWageEdit.setText("58");
		
		this.cpiHolder = new CPIHolder();
        Button cpiSend = (Button) thisFrag.findViewById(R.id.cpi_send);
		cpiSend.setOnClickListener(this);
		this.cpiRatePick = (NumberPicker) thisFrag.findViewById(R.id.cpi_picker);
		this.wtcPricePick = (NumberPicker) thisFrag.findViewById(R.id.wtc_picker);
		this.textAnnualRate = (TextView) thisFrag.findViewById(R.id.cpi_rateannual);
		this.textSemiRate = (TextView) thisFrag.findViewById(R.id.cpi_ratesemi);
		this.textSemiAmount = (TextView) thisFrag.findViewById(R.id.cpi_amountsemi);
		this.textFinalWage = (TextView) thisFrag.findViewById(R.id.cpi_ratefinal);
		this.textMessages = (TextView) thisFrag.findViewById(R.id.cpi_messages);
		//this.textStartWage = (TextView) thisFrag.findViewById(R.id.cpi_wageval);
	
		this.cpiValStrings = makePickerVals(cpiRatePick, "CPI Rate", "%.1f%%", 0f, 10f, 0.1f);		
		this.wtcValStrings = makePickerVals(wtcPricePick, "Oil Price", "%.0f$", 30f, 200f, 1f);
		
		cpiRatePick.setValue(28); //2.7
		wtcPricePick.setValue(81); //110
		
		sendPush();
    }
	
	//Populates a number picker with range and interval specified and returns the String Array
	private String[] makePickerVals(NumberPicker picker, String pickName, String formatStr, float floor, float ceiling, float interval){
		int pickLength = (int) ((ceiling - floor) / interval + 2);
		//Log.w("FlangeAssist WageCPIEstimate", String.format("floor: %.2f, ceiling: %.2f, interval: %.2f, pickLength: %2d", floor, ceiling, interval, pickLength)); 
		pickLength = pickLength > 0 ? pickLength : 1;
		String[] pickStrings = new String[pickLength];
		pickStrings[0] = pickName;
		
		//expand later if more precision needed
		
		float floatCount = floor;
		if(pickLength > 0){
			for(int i = 1; i<pickLength; i++) {
				pickStrings[i] = String.format(formatStr, floatCount);
				floatCount += interval;
				//Log.w("FlangeAssist WageCPIEstimate", String.format("Added %s in position %2d", pickStrings[i], i));
			}
		}
		
		picker.setMaxValue(pickStrings.length - 1);
		picker.setMinValue(0);
		picker.setDisplayedValues(pickStrings);
		
		return pickStrings;
	}
	
	private float cpiVal;
	private float wtcVal;
	private float wageVal;
	private void sendPush() {
		parseWageInput();
		updatePickerVals();
		cpiHolder.calcRate(cpiVal, wtcVal, wageVal);	
		updateText();
	}
	
	void updatePickerVals() throws NumberFormatException {
		try{
			this.cpiVal = Float.parseFloat(cpiValStrings[cpiRatePick.getValue()].replace("%", "")) / 100;
			this.wtcVal = Float.parseFloat(wtcValStrings[wtcPricePick.getValue()].replace("$", ""));
		} catch (NumberFormatException e) {
			this.cpiVal = 0;
			this.wtcVal = 0;
			Toast.makeText(getActivity(), "Error parsing NumberPicker values.", Toast.LENGTH_SHORT).show();
		}
	}
	
	void parseWageInput() throws NumberFormatException {
		try{
			this.wageVal = Float.parseFloat(cpiWageEdit.getText().toString());
		} catch (NumberFormatException e) {
			Toast.makeText(getActivity(), "Wage Input is Invalid.", Toast.LENGTH_SHORT).show();
			this.wageVal = 0f;
		}
	}
	
	private void updateText() {
		textAnnualRate.setText(String.format("Potential Annual Increase: %.1f%%", cpiHolder.wageAnnualCapped * 100));
		textSemiRate.setText(String.format("6 Month Increase: %.1f%%", cpiHolder.semiAnnualCapped * 100));
		textSemiAmount.setText(String.format("6 Month Raise: %.2f$", cpiHolder.wageIncrease));
		textFinalWage.setText(String.format("Adjusted Wage: %.2f$/hr", cpiHolder.finalWage));
		textMessages.setText(cpiHolder.flagStrings);
	}
}
