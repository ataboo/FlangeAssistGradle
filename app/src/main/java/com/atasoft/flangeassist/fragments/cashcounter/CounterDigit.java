package com.atasoft.flangeassist.fragments.cashcounter;

import android.widget.*;

public class CounterDigit
{
	public TextView textView;
	public int oldVal;
	public int newVal;
	public boolean changing = false;

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
