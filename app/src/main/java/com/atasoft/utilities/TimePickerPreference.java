package com.atasoft.utilities;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

/**
 * Created by ataboo on 2016-05-16.
 */
public class TimePickerPreference extends DialogPreference {
    TimePicker picker;
    int lastHour;
    int lastMinute;

    TextView menuSummary;

    public TimePickerPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimePickerPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        View v = super.onCreateView(parent);

        View title = v.findViewById(android.R.id.title);
        if (title != null && title instanceof TextView) {
            TextView titleText = (TextView) title;

            ViewParent viewParent = titleText.getParent();

            if(viewParent != null && viewParent instanceof RelativeLayout) {

                RelativeLayout relLayout = (RelativeLayout) viewParent;

                ShiftPickerPreference.fixPreferenceTitle(titleText, getContext());

                menuSummary = new TextView(getContext());

                menuSummary.setText(String.format("Shift Start: %d:%d", lastHour, lastMinute));

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.BELOW, titleText.getId());
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

                menuSummary.setLayoutParams(params);

                relLayout.addView(menuSummary);
            } else {
                Log.e("TimePickerPreference", "Couldn't find relative layout.");
            }
        }

        return v;
    }

    public static int getHourFromPref(String prefVal){
        return Integer.parseInt(prefVal.split(",")[0]);
    }

    public static int getMinFromPref(String prefVal){
        return Integer.parseInt(prefVal.split(",")[1]);
    }

    public static int[] getTimeFromPref(String prefVal){
        return new int[]{getHourFromPref(prefVal), getMinFromPref(prefVal)};
    }

    @Override
    protected View onCreateDialogView() {
        picker = new TimePicker(getContext());
        picker.setIs24HourView(true);
        return picker;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        picker.setCurrentHour(lastHour);
        picker.setCurrentMinute(lastMinute);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            // needed when user edits the text field and clicks OK
            picker.clearFocus();
            setValue(picker.getCurrentHour(), picker.getCurrentMinute());
        }
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        String time=null;

        if (restoreValue) {
            if (defaultValue==null) {
                time=getPersistedString("08,00");
            }
            else {
                time=getPersistedString(defaultValue.toString());
            }
        }
        else {
            time=defaultValue.toString();
        }

        lastHour = getHourFromPref(time);
        lastMinute = getMinFromPref(time);
    }

    public void setValue(int hour, int minute) {
        String serialString = String.format("%d,%d", hour, minute);

        if (shouldPersist()) {
            persistString(serialString);
        }

        if (hour != lastHour || minute != lastMinute) {
            lastHour = hour;
            lastMinute = minute;
            notifyChanged();
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }
}
