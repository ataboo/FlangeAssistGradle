package com.atasoft.utilities;

import android.util.*;
import android.widget.*;

public class AtaTimePicker {
	
	private NumberPicker hourPick;
	private NumberPicker minPick;
	
	public AtaTimePicker(NumberPicker hourPicker, NumberPicker minPicker){
		this.hourPick = hourPicker;
		this.minPick = minPicker;
		
		String[]HOURS = makeStringsFromRange(0, 23);
		String[] MINUTES = makeStringsFromRange(0,59);
		
		populatePicker(hourPick, HOURS);
		populatePicker(minPick, MINUTES);
		
		//Prevents outOfBounds bug on orientation change.
		//hourPick.setSaveFromParentEnabled(false);
		//minPick.setSaveFromParentEnabled(false);
		//hourPick.setSaveEnabled(true);
		//minPick.setSaveEnabled(true);
		
		/*   Just incase I use something like this again. It bugged on orientation change though.
		this.pickLay = new LinearLayout(ctx);
		pickLay.setGravity(Gravity.CENTER_VERTICAL);
		LinearLayout.LayoutParams layPar = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 
															LinearLayout.LayoutParams.WRAP_CONTENT);
		pickLay.setLayoutParams(layPar);
		pickLay.setOrientation(LinearLayout.HORIZONTAL);
		
		//Label for time
		TextView label = new TextView(ctx);
		label.setText(labelName);
		label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		pickLay.addView(label);
		
		//Hour number picker
		this.hourPick = makePicker(HOURS, ctx);
		pickLay.addView(hourPick);
		
		//Seperates hours from minutes
		TextView seperator = new TextView(ctx);
		seperator.setText(":");
		seperator.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
		seperator.setPadding(8, 36, 8, 8);
		pickLay.addView(seperator);
		
		//Minute number picker
		this.minPick = makePicker(MINUTES, ctx);
		pickLay.addView(minPick);
		parent.addView(pickLay);
		hourPick.setValue(defTime[0]);
		minPick.setValue(defTime[1]);
		*/
	}
	
	public void setPickerValue(int[] timeSet){
		timeSet = validateTime(timeSet);
		hourPick.setValue(timeSet[0]);
		minPick.setValue(timeSet[1]);
		//Log.w("AtaTimePicker", String.format("Set value to %2d:%2d", timeSet[0], timeSet[1]));
    }
	
	public int[] getVals() throws NullPointerException{
		int[] retInt = {0,0};
		try{
			retInt[0] = hourPick.getValue();
			retInt[1] = minPick.getValue();
		} catch(NullPointerException e){
			Log.e("AtaTimePicker", "NullPointerException when trying getValse()");
		}
		return retInt;
	}
	
	private int[] validateTime(int[] timeArr){
		timeArr[0] = (timeArr[0] >= 0 && timeArr[0] <= 23) ? timeArr[0] : 0;
		timeArr[1] = (timeArr[1] >= 0 && timeArr[1] <= 59) ? timeArr[1] : 0;
		return timeArr;
	}

	private String[] makeStringsFromRange(int floor, int ceiling){
		String[] retString = new String[ceiling - floor + 1];
		for(int i=floor; i <= ceiling; i++){
			retString[i] = Integer.toString(i);
			if(i < 10) retString[i] = "0" + retString[i];
		}
		return retString;
	}

	private void populatePicker(NumberPicker picker, String[] values){
		picker.setDisplayedValues(values);
		picker.setMaxValue(values.length-1);
		picker.setMinValue(0);
    }
}
