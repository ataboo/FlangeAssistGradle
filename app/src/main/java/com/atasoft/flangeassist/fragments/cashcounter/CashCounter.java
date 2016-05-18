package com.atasoft.flangeassist.fragments.cashcounter;

import android.content.*;
import android.graphics.*;
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
import com.atasoft.utilities.*;
import com.atasoft.flangeassist.fragments.cashcounter.CashCounterData.*;


public class CashCounter extends Fragment implements OnClickListener {

	public enum VestigialEarningType {
		WEEKEND_DOUBLE("Weekend Double (2x)", Color.parseColor(goldColor)),
	    STRAIGHT_TIME("Straight Time (1x)", Color.parseColor(bronzeColor)),
	    DOUBLE_TIME("Double Time (2x)", Color.parseColor(goldColor)),
		OVER_TIME("Overtime (1.5x)", Color.parseColor(silverColor)),
        OFF_SHIFT("Off Shift", Color.RED),
		HOLIDAY_TIME("Holiday Double (2x)", Color.parseColor(goldColor));
		
		private final String display;
		private final int color;
		
		VestigialEarningType(String display, int color){
			this.display = display;
			this.color = color;
		}
		
		public String toString(){
			return this.display;
	    }
		
		public int getColor(){
			return this.color;
		}
	}
	
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

	private TranslateAnimation slideInListen = makeTranslateVertical(400f, 0f, 400);
	private TranslateAnimation slideOutListen = makeTranslateVertical(0f, -400f, 400);
	private TranslateAnimation slideIn = makeTranslateVertical(400f, 0f, 400);
	private TranslateAnimation slideOut = makeTranslateVertical(0f, -400f, 400);

	private CashCounterData cashCounterData = new CashCounterData();

	private int[] oldCountVals = {0,0,0,0,0,0};

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
		
		Button setExpand = (Button) thisFrag.findViewById(R.id.cash_settingsBut);
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
			weekdayEdits[i].setText(String.format("%.1f", weekdayHours[i]));
		}
		
		CounterDigit hundredthDigit = new CounterDigit((TextView) thisFrag.findViewById(R.id.cash_hundredthsText), oldCountVals[5]);
		CounterDigit tenthDigit = new CounterDigit((TextView) thisFrag.findViewById(R.id.cash_tenthsText), oldCountVals[4]);
		CounterDigit oneDigit = new CounterDigit((TextView) thisFrag.findViewById(R.id.cash_onesText), oldCountVals[3]);
		CounterDigit tenDigit = new CounterDigit((TextView) thisFrag.findViewById(R.id.cash_tensText), oldCountVals[2]);
		CounterDigit hundredDigit = new CounterDigit((TextView) thisFrag.findViewById(R.id.cash_hundredsText), oldCountVals[1]);
		CounterDigit thousandDigit = new CounterDigit((TextView) thisFrag.findViewById(R.id.cash_thousandText), oldCountVals[0]);
		counterDigits = new CounterDigit[]{thousandDigit, hundredDigit, tenDigit, oneDigit, tenthDigit, hundredthDigit};

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
				clockTick();
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
  	private void updateValues(){
		
		//------Update time info-----
		for(int i=0; i<weekdayHours.length; i++){
			weekdayHours[i] = AtaMathUtils.clampFloat(parseFromEdit(weekdayEdits[i], String.format("weekdayEdits[%s]", i)), 0, 24);
		}
		timeNow.setToNow();
		shiftStartVal = startAtaPicker.getVals();

		//Log.w("CashCounter", String.format("hour is %2d, day is %2d.", timeNow.hour, timeNow.weekDay));

		//------Update wage and schedule settings------
		this.wageRate = AtaMathUtils.parseFloat(wageEdit.getText().toString());

		CashCounterData.EarningAttributes earningAttributes = cashCounterData.new EarningAttributes(
				shiftStartVal, weekdayHours, wageRate, holidayToggle.isChecked(), fourTenToggle.isChecked(),
				weekendDoubleToggle.isChecked(), nightToggle.isChecked());

		EarningsReturn earningsReturn = cashCounterData.getEarnings(timeNow, earningAttributes);

		//otIndicate(earningsReturn.earningType);
		updateCounter(CashCounterData.makeValsFromDouble(earningsReturn.earnings));
	}
	
	private void recallSettings(){
		startAtaPicker.setPickerValue(new int[]{prefs.getInt("ATA_counterStartHour", 8), prefs.getInt("ATA_counterStartMin", 0)});
		wageEdit.setText(String.format("%.2f", prefs.getFloat("ATA_counterWageRate", 43.90f)));

		nightToggle.setChecked(prefs.getBoolean("ATA_counterNightShift", false));
		weekendDoubleToggle.setChecked(prefs.getBoolean("ATA_counterWeekendDouble", true));
		holidayToggle.setChecked(prefs.getBoolean("ATA_counterHoliday", false));
		fourTenToggle.setChecked(prefs.getBoolean("ATA_counterFourTens", false));
		weekdayEdits[0].setText(String.format("%.1f", prefs.getFloat("ATA_counterWeekdatST", 8f)));
		weekdayEdits[1].setText(String.format("%.1f", prefs.getFloat("ATA_counterWeekdatOT", 2f)));
		weekdayEdits[2].setText(String.format("%.1f", prefs.getFloat("ATA_counterWeekdatDT", 2f)));
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

	private void otIndicate(VestigialEarningType vestigialEarningType){
		otIndicator.setText(vestigialEarningType.toString());
		otIndicator.setTextColor(vestigialEarningType.getColor());
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
	
	private void slideInEnd(){  //teehee
		this.changeFlag = false;  //allows new updateValues to be called
	}
	
	private void slideOutEnd(){
		boolean isFirstOne = true;
		for(CounterDigit digit: counterDigits){
			if(digit.changing) {
				digit.textView.setText(String.format("%d", digit.newVal));
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
	
	private void clockTick(){
		if(!changeFlag) {
			updateValues();
		}
        /*else {
			//Toast.makeText(context, "doubleTap", Toast.LENGTH_SHORT).show();
		}
		*/
	}
}
