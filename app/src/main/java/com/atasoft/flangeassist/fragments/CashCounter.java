package com.atasoft.flangeassist.fragments;

import android.content.*;
import android.os.*;
import android.preference.*;
import android.support.v4.app.*;
import android.text.format.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.view.animation.*;
import android.widget.*;
import com.atasoft.flangeassist.*;
import com.atasoft.helpers.*;
import android.graphics.*;


public class CashCounter extends Fragment implements OnClickListener {

	private View thisFrag;
	private Context context;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState){
								 
		View v = inflater.inflate(R.layout.cash_counter, container, false);
        this.thisFrag = v;
		this.context = getActivity().getApplicationContext();
		setupViews();
		setupTicker();
		
		return v;
	}
	
	@Override
	public void onResume(){
		ticker.run();
		super.onResume();
		recallSettings();
	}
	
	@Override
	public void onPause(){
		saveSettings();
		handler.removeCallbacks(ticker);
		super.onPause();
	}
	
	public class CounterDigit{
		TextView textView;
		int oldVal;
		int newVal;
		boolean changing = false;
		
		public CounterDigit(TextView textView, int startVal){
			this.textView = textView;
			this.oldVal = startVal;
			this.newVal = startVal;
			textView.setText(Integer.toString(startVal));
		}
		
		//returns true if changed
		public boolean changeVal(int newVal){
			this.newVal = newVal;
			this.changing = (newVal != oldVal);
			return changing;
		}
		
		public void hide(boolean isHide){
			int visCode = isHide ? TextView.GONE: TextView.VISIBLE;
			textView.setVisibility(visCode);
		}
	}
	
	@Override
    public void onClick(View v) {
		switch (v.getId()) {
			case R.id.cash_settingsBut:
				toggleSettingsHide();
				break;
			case R.id.cash_fourTensCheck:
				fourTenLock();
				break;
		}
    }
		
	//-------------------------initial functions-----------------
    private Time timeNow;
	private int[] shiftStartVal = {18, 30};
	private Button setExpand;
	private EditText wageEdit;
	private TextView otIndicator;
	private LinearLayout setLay;
	private CheckBox nightToggle;
	private CheckBox holidayToggle;
	private CheckBox fourTenToggle;
	private CheckBox weekendDoubleToggle;
	private EditText[] weekdayEdits = new EditText[3];
	private AtaTimePicker startAtaPicker;
	
	private float[] weekdayHours = new float[3];
	
	private TranslateAnimation slideInListen;
	private TranslateAnimation slideOutListen;
	private TranslateAnimation slideIn;
	private TranslateAnimation slideOut;

	private int[] oldCountVals = {0,0,0,0,0,0};
	private CounterDigit hundredthDigit;
	private CounterDigit tenthDigit;
	private CounterDigit oneDigit;
	private CounterDigit tenDigit;
	private CounterDigit hundredDigit;
	private CounterDigit thousandDigit;
	private static final String goldColor = "#FFDF00";
	private static final String silverColor = "#C0C0C0";
	private static final String bronzeColor = "#CD7F32";
	
	private float wageRate;
	private CounterDigit[] counterDigits;
	private SharedPreferences prefs;
	private void setupViews(){
		this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
		
		this.timeNow = new Time(Time.getCurrentTimezone());
		this.otIndicator = (TextView) thisFrag.findViewById(R.id.cash_counterOTIndicator);
		this.wageEdit = (EditText) thisFrag.findViewById(R.id.cash_wageEdit);
		//if(wageEdit.getText().toString() == "") 
		this.wageRate = prefs.getFloat("cashcount_wage", 43.25f);
		wageEdit.setText(Float.toString(wageRate), EditText.BufferType.EDITABLE);
		
		this.setExpand = (Button) thisFrag.findViewById(R.id.cash_settingsBut);
		setExpand.setOnClickListener(this);
		this.nightToggle = (CheckBox) thisFrag.findViewById(R.id.cash_nightshiftCheck);
		this.holidayToggle = (CheckBox) thisFrag.findViewById(R.id.cash_holidayCheck);
		this.weekendDoubleToggle = (CheckBox) thisFrag.findViewById(R.id.cash_weekendDoubleCheck);
		this.fourTenToggle = (CheckBox) thisFrag.findViewById(R.id.cash_fourTensCheck);
		fourTenToggle.setOnClickListener(this);
		this.weekdayEdits = new EditText[3];
		this.weekdayEdits[0] = (EditText) thisFrag.findViewById(R.id.cash_weekdaySingle);
		this.weekdayHours[0] = prefs.getFloat("cash_week_single", 8);
		this.weekdayEdits[1] = (EditText) thisFrag.findViewById(R.id.cash_weekdayHalf);
		this.weekdayHours[1] = prefs.getFloat("cash_week_half", 2);
		this.weekdayEdits[2] = (EditText) thisFrag.findViewById(R.id.cash_weekdayDouble);
		this.weekdayHours[2] = prefs.getFloat("cash_week_double", 2);
		for(int i=0; i<weekdayEdits.length; i++){
			weekdayEdits[i].setText(Float.toString(weekdayHours[i]));
		}
		
		this.hundredthDigit = new CounterDigit((TextView) thisFrag.findViewById(R.id.cash_hundredthsText), oldCountVals[5]);
		this.tenthDigit = new CounterDigit((TextView) thisFrag.findViewById(R.id.cash_tenthsText), oldCountVals[4]);
		this.oneDigit = new CounterDigit((TextView) thisFrag.findViewById(R.id.cash_onesText), oldCountVals[3]);
		this.tenDigit = new CounterDigit((TextView) thisFrag.findViewById(R.id.cash_tensText), oldCountVals[2]);
		this.hundredDigit = new CounterDigit((TextView) thisFrag.findViewById(R.id.cash_hundredsText), oldCountVals[1]);
		this.thousandDigit = new CounterDigit((TextView) thisFrag.findViewById(R.id.cash_thousandText), oldCountVals[0]);
		this.counterDigits = new CounterDigit[]{thousandDigit, hundredDigit, tenDigit, oneDigit, tenthDigit, hundredthDigit};
		
		
		this.slideIn = makeTranslateVertical(400f, 0f, 400);
		this.slideOut = makeTranslateVertical(0f, -400f, 400);
		this.slideInListen = makeTranslateVertical(400f, 0f, 400);
		this.slideOutListen = makeTranslateVertical(0f, -400f, 400);
		setEndListeners();
		
		setLay = (LinearLayout) thisFrag.findViewById(R.id.cash_setLin);
		NumberPicker startHourPick = (NumberPicker) thisFrag.findViewById(R.id.cash_startHour);
		NumberPicker startMinPick = (NumberPicker) thisFrag.findViewById(R.id.cash_startMin);
		startAtaPicker = new AtaTimePicker(startHourPick, startMinPick);
		recallSettings();
		toggleSettingsHide();
	}
	
	private TranslateAnimation makeTranslateVertical(float start, float end, int duration){
		//x start, x end, y start, y end
		TranslateAnimation slide = new TranslateAnimation(0f, 0f, start, end);
		slide.setDuration(duration);
		return slide;
	}
	
	private void setEndListeners(){
		slideInListen.setAnimationListener(new Animation.AnimationListener(){
			@Override
			public void onAnimationStart(Animation arg) {
			}           
			@Override
			public void onAnimationRepeat(Animation arg) {
			}           
			@Override
			public void onAnimationEnd(Animation arg) {		
				slideInEnd();
			}	
		});
		slideOutListen.setAnimationListener(new Animation.AnimationListener(){
			@Override
			public void onAnimationStart(Animation arg) {
			}           
			@Override
			public void onAnimationRepeat(Animation arg) {
			}           
			@Override
			public void onAnimationEnd(Animation arg) {		
				slideOutEnd();
			}	
		});
	}
	
	private Handler handler;
	private Runnable ticker;
	private void setupTicker(){
		handler = new Handler();
		
		ticker = new Runnable() {
            public void run() {
                long now = SystemClock.uptimeMillis();
                long next = now + 1000;
                handler.postAtTime(ticker, next);
				testClick();
            }
        };
	}
	
//-------------------Updating Functions-------------------
	
	private boolean settingsHidden = false;
	private void toggleSettingsHide(){
		settingsHidden = !settingsHidden;
		int visCode = (settingsHidden) ? View.GONE : View.VISIBLE;
		setLay.setVisibility(visCode);
	}
	
	//hh,mm,ss
    private int[] currentTimeArr = new int[3];
	private int[] shiftEnd = new int[3];
	private int[] shiftDuration = new int[3];
	private void updateValues(){
		
		//------Update time info-----
		for(int i=0; i<weekdayHours.length; i++){
			weekdayHours[i] = AtaMathUtils.bracketFloat(parseFromEdit(weekdayEdits[i], String.format("weekdayEdits[%s]", i)), 0, 24);
		}
		float shiftLengthFloat = AtaMathUtils.bracketFloat(weekdayHours[0] + weekdayHours[1] + weekdayHours[2], 0, 24);
		timeNow.setToNow();
		//Log.w("CashCounter", String.format("hour is %2d, day is %2d.", timeNow.hour, timeNow.weekDay));
		
		currentTimeArr = new int[]{timeNow.hour, timeNow.minute, timeNow.second};
		shiftDuration[0] = (int) shiftLengthFloat;
		shiftDuration[1] = (int) (shiftLengthFloat - (float) shiftDuration[0]) * 60;
		this.shiftStartVal = startAtaPicker.getVals();
		shiftEnd = getShiftEnd(shiftStartVal, shiftDuration);
		int[] newCountVals = {0,0,0,0,0,0};
		
		//------Update wage and schedule settings------
		this.wageRate = ataParseFloat(wageEdit.getText().toString());
		
		if(isInTimeRange(shiftStartVal, shiftEnd, currentTimeArr)){
			double earnings = getEarnings(currentTimeArr, shiftStartVal, wageRate);
			newCountVals = makeValsFromDouble(earnings);
		} else {
			otIndicate(OFF_SHIFT);
		}
		
		//------Write to counter------
		updateCounter(newCountVals);
	}
	
	private void recallSettings(){
		startAtaPicker.setPickerValue(new int[]{prefs.getInt("ATA_counterStartHour", 8), prefs.getInt("ATA_counterStartMin", 0)});
		wageEdit.setText(Float.toString(prefs.getFloat("ATA_counterWageRate", 43.90f)));
		nightToggle.setChecked(prefs.getBoolean("ATA_counterNightShift", false));
		weekendDoubleToggle.setChecked(prefs.getBoolean("ATA_counterWeekendDouble", true));
		holidayToggle.setChecked(prefs.getBoolean("ATA_counterHoliday", false));
		fourTenToggle.setChecked(prefs.getBoolean("ATA_counterFourTens", false));
		weekdayEdits[0].setText(Float.toString(prefs.getFloat("ATA_counterWeekdayST", 8f)));
		weekdayEdits[1].setText(Float.toString(prefs.getFloat("ATA_counterWeekdayOT", 2f)));
		weekdayEdits[2].setText(Float.toString(prefs.getFloat("ATA_counterWeekdayDT", 2f)));
		
		//going to be via preferences eventually
		//startAtaPicker.setPickerValue(shiftStartVal);
	}
	
	private void saveSettings(){
		//preferences eventually
		this.shiftStartVal = startAtaPicker.getVals();
		SharedPreferences.Editor prefEdit = prefs.edit();
		prefEdit.putInt("ATA_counterStartHour", shiftStartVal[0]);
		prefEdit.putInt("ATA_counterStartMin", shiftStartVal[1]);
		prefEdit.putFloat("ATA_counterWageRate", wageRate);
		prefEdit.putBoolean("ATA_counterNightShift", nightToggle.isChecked());
		prefEdit.putBoolean("ATA_counterWeekendDouble", weekendDoubleToggle.isChecked());
		prefEdit.putBoolean("ATA_counterHoliday", holidayToggle.isChecked());
		prefEdit.putBoolean("ATA_counterFourTens", fourTenToggle.isChecked());
		prefEdit.putFloat("ATA_counterWeekdayST", weekdayHours[0]);
		prefEdit.putFloat("ATA_counterWeekdayOT", weekdayHours[1]);
		prefEdit.putFloat("ATA_counterWeekdayDT", weekdayHours[2]);
		prefEdit.apply();
		
	}
	
	private boolean isInTimeRange(int[] rangeStart, int[] rangeEnd, int[] timeCheck){
		float floatStart = getFloatTime(rangeStart);
		float floatEnd = getFloatTime(rangeEnd);
		float floatCheck = getFloatTime(timeCheck);
		if(floatStart <= floatEnd){
            return !(floatCheck < floatStart || floatCheck > floatEnd);
        } else { //range stradles midnight
            return floatCheck > floatStart || floatCheck < floatEnd;
        }
	}
	
	private double getEarnings(int[] timeNowArr, int[] shiftStart, float wageVal){
		//used float for comparisons but keep into for calcs incase of rounding shenanigans
		float floatNow = getFloatTime(timeNowArr);
		float floatStart = getFloatTime(shiftStart);
		int secondsIntoShift = timeNowArr[2];
		secondsIntoShift += (timeNowArr[1] - shiftStart[1]) * 60;
		//already checked that it's mid shift
		if(floatNow > floatStart) {
			secondsIntoShift += (timeNowArr[0] - shiftStart[0]) * 3600;
		} else {  //start-->now range stradles midnight
			secondsIntoShift += (timeNowArr[0] - shiftStart[0] + 24) * 3600;
		}
		double hoursIntoShift = secondsIntoShift / 3600d;
		double earnings;
		
		boolean isFriday = false;
		boolean isWeekend = false;
		boolean isHoliday = false;

        //TODO add friday
		
		//during shift now is checked before
		// if shift doesn't stradle midnight and its saturday or sunday now...
	    if(floatNow > floatStart && (timeNow.weekDay == Time.SATURDAY || timeNow.weekDay == Time.SUNDAY)){
			isWeekend = true;
		}
		// if nightshift and after midnight sunday is sat shift and monday is sun shift
		if(floatNow < floatStart && (timeNow.weekDay == Time.SUNDAY || timeNow.weekDay == Time.MONDAY)){
			isWeekend = true;
		}
		if(holidayToggle.isChecked()) isHoliday = true;
		double[] hours = new double[3];  //single, ot, double
		if(fourTenToggle.isActivated()){
			hours[2] = AtaMathUtils.bracketDouble(hoursIntoShift - 10, 0, 24);
			if(isFriday){
				hours[1] = AtaMathUtils.bracketDouble(hoursIntoShift, 0, 10);
				hours[0] = 0;
			} else {
				hours[0] = AtaMathUtils.bracketDouble(hoursIntoShift, 0, 10);
				hours[1] = 0;
			}
		} else {
			hours[0] = AtaMathUtils.bracketDouble(hoursIntoShift, 0, weekdayHours[0]);
			hours[2] = AtaMathUtils.bracketDouble(hoursIntoShift - weekdayHours[0] - weekdayHours[1], 0, 24);
			hours[1] = AtaMathUtils.bracketDouble(hoursIntoShift - hours[0] - hours[2], 0, 24);	
		}
		if((isWeekend && weekendDoubleToggle.isChecked()) || isHoliday){
			hours[0] = 0;
			hours[1] = 0;
			hours[2] = AtaMathUtils.bracketDouble(hoursIntoShift, 0, 24);
		}
		if(hours[2] > 0){
		    if(isHoliday){
				otIndicate(HOLIDAY_TIME);
			} else {
				if(isWeekend){
					otIndicate(WEEKEND_DOUBLE);
				} else {
					otIndicate(DOUBLE_TIME);
				}
			}
		} else {
			if(hours[1] > 0){
				otIndicate(OVER_TIME);
			} else {
				otIndicate(STRAIGHT_TIME);
			}
		}
		double hoursEquivelant = 1d * hours[0] + 1.5d * hours[1] + 2d * hours[2];
		//Log.w("CashCounter",String.format("weekdayhours[0]:%.3f, weekdayhours[1]:%.3f, weekdayhours[2]:%.3f", weekdayHours[0], weekdayHours[1], weekdayHours[2]));
		
		//Log.w("CashCounter",String.format("hours[0]:%.3f, hours[1]:%.3f, hours[2]:%.3f, intoShift: %.3f", hours[0], hours[1], hours[2], hoursIntoShift));
		earnings = hoursEquivelant * wageVal;
		if(nightToggle.isChecked()) earnings += hoursIntoShift * 3d;
		earnings = Math.floor(earnings * 100) / 100;
		return earnings;
	}
	
	private static final int STRAIGHT_TIME = 0;
	private static final int DOUBLE_TIME = 1;
	private static final int OVER_TIME = 2;
	private static final int OFF_SHIFT = 3;
	private static final int HOLIDAY_TIME = 4;
	private static final int WEEKEND_DOUBLE = 5;
	
	private void otIndicate(int rateCode){
		switch(rateCode){
			case DOUBLE_TIME:
				otIndicator.setText("Double Time (x2)");
				otIndicator.setTextColor(Color.parseColor(goldColor));
				break;
			case OVER_TIME:
				otIndicator.setText("Overtime (x1.5)");
				otIndicator.setTextColor(Color.parseColor(silverColor));
				break;
			case STRAIGHT_TIME:
				otIndicator.setText("Straight Time (x1)");
				otIndicator.setTextColor(Color.parseColor(bronzeColor));
				break;
			case OFF_SHIFT:
				otIndicator.setText("Off Shift");
				otIndicator.setTextColor(Color.RED);
				break;
			case WEEKEND_DOUBLE:
				otIndicator.setText("Weekend Double (2x)");
				otIndicator.setTextColor(Color.parseColor(goldColor));
				break;
			case HOLIDAY_TIME:
				otIndicator.setText("Holiday Double (2x)");
				otIndicator.setTextColor(Color.parseColor(goldColor));
				break;
		}
	}
	
	private int[] makeValsFromDouble(double earnings){
		String valString = String.format("%.2f", earnings);
		int earnLength = valString.length();
		int[] retVals = new int[]{0,0,0,0,0,0};
		if (earnLength < 4 || earnLength > 7){
			Log.e("CashCounter_MakeValseFromDouble", "Earning String out of Range.");
			return retVals;
		}
		retVals[5] = Character.getNumericValue(valString.charAt(earnLength - 1)); //hundreds
		retVals[4] = Character.getNumericValue(valString.charAt(earnLength - 2)); //tenths
		retVals[3] = Character.getNumericValue(valString.charAt(earnLength - 4)); //ones decimal is @ 3
		retVals[2] = (earnLength >= 5) ? Character.getNumericValue(valString.charAt(earnLength - 5)) : 0;
		retVals[1] = (earnLength >= 6) ? Character.getNumericValue(valString.charAt(earnLength - 6)) : 0;
		retVals[0] = (earnLength >= 7) ? Character.getNumericValue(valString.charAt(earnLength - 7)) : 0;
		
		//Log.w("CashCounter_makeCountString", String.format("retString: %s, length: %s", valString, valString.length()));
		//return retString;
		return retVals;
	}
	
	private float getFloatTime(int[] intTime){
		if(intTime.length < 2) return 0f;
		
		float retFloat = intTime[0];
		retFloat += ((float) intTime[1]) / 60;
		if(intTime.length == 3) retFloat += ((float) intTime[2]) / 3600;
		return retFloat;
	}
	
	private int[] getShiftEnd(int[] shiftStart, int[] shiftDuration){
		int[] shiftEnd = {0,0};  //hr, min
		
		shiftEnd[1] = shiftStart[1] + shiftDuration[1];
		if(shiftEnd[1] >= 60){
			shiftDuration[0]++; //adds and hour when minutes overflow
			shiftEnd[1] = shiftEnd[1] % 60;
		}
		shiftEnd[0] = shiftStart[0] + shiftDuration[0];
		shiftEnd[0] = shiftEnd[0] % 24;
		
		return shiftEnd;
	}
	
	private float parseFromEdit(EditText eText, String name) throws NumberFormatException{
		try{
			return Float.parseFloat(eText.getText().toString());
		} catch(NumberFormatException e){
			Log.e("CashCounter", "NumberFormatException for EditText " + name + ".");
			return 0f;
		}
	}
	
	
	
	private boolean changeFlag = false;  //true when counter is changing to prevent multiple calls
	private void updateCounter(int[] newVals){
		/*
		if(newString.length() <= 6 && newString.length() > 0){
			for(int i = newString.length() - 1; i>=0; i--){
				newVals[i] = newString.charAt(i);
			}
		}
		*/
		boolean firstOne = true;
		for(int i=0; i < newVals.length; i++){
			if(counterDigits[i].changeVal(newVals[i])){
				//Make sure only first changed digit has listener to prevent multiple calls
				TranslateAnimation slide = (firstOne) ? slideOutListen : slideOut;
				counterDigits[i].textView.startAnimation(slide);
				this.changeFlag = true;
				firstOne = false;
			}
		}
		if(newVals[0] == 0){
			counterDigits[0].hide(true);
			if(newVals[1] == 0){
				counterDigits[1].hide(true);
				if(newVals[2] == 0){
					counterDigits[2].hide(true);
				} else {
					counterDigits[2].hide(false);
				}
			} else {
				counterDigits[1].hide(false);
				counterDigits[2].hide(false);
			}
		} else {
			counterDigits[0].hide(false);
			counterDigits[1].hide(false);
			counterDigits[2].hide(false);
		}	
	}
	
	private float ataParseFloat(String strIn) throws NumberFormatException{
		try{
			return Float.parseFloat(strIn);
		} catch (NumberFormatException e){
			Log.e("CashCounter", "Error parsing Float");
			return 0f;
		}
	}
	
	private void slideInEnd(){  //teehee
		this.changeFlag = false;  //allows new updateValues to be called
	}
	
	private void slideOutEnd(){
		boolean isFirstOne = true;
		for(CounterDigit digit: counterDigits){
			if(digit.changing) {
				digit.textView.setText(Integer.toString(digit.newVal));
				digit.oldVal = digit.newVal;
				TranslateAnimation slide = (isFirstOne) ? slideInListen : slideIn;
				digit.textView.startAnimation(slide);
				digit.changing = false;
				isFirstOne = false;
			}
		}
	}
	
	private void fourTenLock(){
			weekdayEdits[1].setEnabled(!fourTenToggle.isChecked());
	}
	
	private void testClick(){
		if(!changeFlag) {
			updateValues();
		} else {
			//Toast.makeText(context, "doubleTap", Toast.LENGTH_SHORT).show();
		}
	}
}
