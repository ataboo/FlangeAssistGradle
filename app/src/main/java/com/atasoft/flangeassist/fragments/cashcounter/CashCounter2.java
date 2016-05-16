package com.atasoft.flangeassist.fragments.cashcounter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.atasoft.flangeassist.R;
import com.atasoft.flangeassist.fragments.cashcounter.CashCounterData.EarningAttributes;
import com.atasoft.flangeassist.fragments.cashcounter.CashCounterData.EarningsReturn;
import com.atasoft.helpers.AtaMathUtils;
import com.atasoft.helpers.AtaTimePicker;


public class CashCounter2 extends Fragment {

	public enum EarningType {
		WEEKEND_DOUBLE("Weekend Double (2x)", Color.parseColor(goldColor)),
		STRAIGHT_TIME("Straight Time (1x)", Color.parseColor(bronzeColor)),
		DOUBLE_TIME("Double Time (2x)", Color.parseColor(goldColor)),
		OVER_TIME("Overtime (1.5x)", Color.parseColor(silverColor)),
		OFF_SHIFT("Off Shift", Color.RED),
		HOLIDAY_TIME("Holiday Double (2x)", Color.parseColor(goldColor));

		private final String display;
		private final int color;

		EarningType(String display, int color){
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

    private static final String goldColor = "#FFDF00";
    private static final String silverColor = "#C0C0C0";
    private static final String bronzeColor = "#CD7F32";

	private CounterGameView counterGameView;
    private SharedPreferences prefs;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState){

		if(counterGameView == null) {
			prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
			counterGameView = new CounterGameView(getContext());
		}

		return counterGameView;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		counterGameView.destroy();
	}

	@Override
	public void onResume(){
		super.onResume();
		recallSettings();

        counterGameView.resume();
	}

	@Override
	public void onPause(){
		saveSettings();
		super.onPause();

        counterGameView.pause();
	}

	private void recallSettings(){

	}
	
	private void saveSettings(){
		//preferences eventually
		//SharedPreferences.Editor prefEdit = prefs.edit();
		//prefEdit.apply();
	}
}
