package com.atasoft.utilities;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.util.ArrayList;

/**
 * Created by ataboo on 2016-05-16.
 */
public class ShiftPickerPreference extends DialogPreference {
    NumberPicker straightPicker;
    NumberPicker otPicker;
    NumberPicker doublePicker;

    TextView menuSummary;

    int lastStraight;
    int lastOT;
    int lastDouble;

    public ShiftPickerPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShiftPickerPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);
    }

    public static float[] parseHours(String prefString){
        String[] splitStr = prefString.split(",");

        return new float[]{Float.parseFloat(splitStr[0]), Float.parseFloat(splitStr[1]), Float.parseFloat(splitStr[2])};

    }

    public float[] hoursFromValues(int[] pickerVals){
        return new float[]{((float)pickerVals[0])/2f, ((float)pickerVals[1])/2f, ((float)pickerVals[2])/2f};
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        View v = super.onCreateView(parent);

        View title = v.findViewById(android.R.id.title);
        if (title != null && title instanceof TextView) {
            TextView titleText = (TextView) title;
            fixPreferenceTitle(titleText, getContext());

            ViewParent viewParent = titleText.getParent();

            if(viewParent != null && viewParent instanceof RelativeLayout) {


                RelativeLayout relLayout = (RelativeLayout) viewParent;

                menuSummary = new TextView(getContext());
                float[] hours = hoursFromValues(new int[]{lastStraight, lastOT, lastDouble});
                menuSummary.setText(String.format("Straight: %.1f, Overtime: %.1f, Doubletime: %.1f", hours[0], hours[1], hours[2]));
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.BELOW, titleText.getId());
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

                menuSummary.setLayoutParams(params);

                relLayout.addView(menuSummary);
            } else {
                Log.e("ShiftPickerPreference", "Couldn't find relative layout in menu.");
            }
        }

        return v;
    }

    public static void fixPreferenceTitle(TextView title, Context context) {
        setTextAppearance(title, context, androidx.appcompat.R.style.Base_TextAppearance_AppCompat_Menu);
    }

    private static void setTextAppearance(TextView textView, Context context, int resId) {

        if (Build.VERSION.SDK_INT < 23) {

            textView.setTextAppearance(context, resId);

        } else {

            textView.setTextAppearance(resId);
        }
    }

    @Override
    protected View onCreateDialogView() {
        String[] pickerVals = makeDisplayVals();

        LinearLayout linLay = new LinearLayout(getContext());
        linLay.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;

        straightPicker = new NumberPicker(getContext());
        setupPicker(straightPicker, pickerVals);

        otPicker = new NumberPicker(getContext());
        setupPicker(otPicker, pickerVals);

        doublePicker = new NumberPicker(getContext());
        setupPicker(doublePicker, pickerVals);

        linLay.addView(straightPicker);
        linLay.addView(otPicker);
        linLay.addView(doublePicker);

        return linLay;
    }

    private void setupPicker(NumberPicker picker, String[] displayVals){
        picker.setMinValue(0);
        picker.setMaxValue(48);

        picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 1;
        picker.setLayoutParams(params);


        picker.setDisplayedValues(displayVals);

    }

    private String[] makeDisplayVals(){
        ArrayList<String> hourDisplays = new ArrayList<>(49);
        for(int i=0; i<=48; i++){
            float halfIndex = ((float) i)/ 2f;
            hourDisplays.add(String.format("%.1f", halfIndex));
        }

        return hourDisplays.toArray(new String[hourDisplays.size()]);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        straightPicker.setValue(lastStraight);
        otPicker.setValue(lastOT);
        doublePicker.setValue(lastDouble);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            // needed when user edits the text field and clicks OK
            straightPicker.clearFocus();
            otPicker.clearFocus();
            doublePicker.clearFocus();
            setValue(straightPicker.getValue(), otPicker.getValue(), doublePicker.getValue());
        }
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        super.onSetInitialValue(restoreValue, defaultValue);

        String time=null;

        if (restoreValue) {
            if (defaultValue==null) {
                time=getPersistedString("8.0,2.0,2.0");
            }
            else {
                time=getPersistedString(defaultValue.toString());
            }
        }
        else {
            time=defaultValue.toString();
        }

        float[] hours = ShiftPickerPreference.parseHours(time);

        lastStraight = (int) hours[0] * 2;
        lastOT = (int) hours[1] * 2;
        lastDouble = (int) hours[2] * 2;
    }

    public void setValue(int straightTime, int otTime, int doubleTime) {
        float straightFloat = ((float) straightTime) / 2f;
        float otFloat = ((float) otTime) / 2f;
        float doubleFloat = ((float) doubleTime) / 2f;

        if(straightFloat + otFloat + doubleFloat > 24){
            doubleFloat = AtaMathUtils.clampFloat(24f - straightFloat - otFloat, 0f, 24f);
            otFloat = AtaMathUtils.clampFloat(24f - straightFloat - doubleFloat, 0f, 24f);
        }


        String serialString = String.format("%.1f,%.1f,%.1f", straightFloat, otFloat, doubleFloat);

        if (shouldPersist()) {
            persistString(serialString);
        }

        if (straightTime != lastStraight || otTime != lastOT || doubleTime != lastDouble) {
            lastStraight = (int) straightFloat * 2;
            lastOT = (int) otFloat * 2;
            lastDouble = (int) doubleFloat * 2;
            notifyChanged();
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }
}
