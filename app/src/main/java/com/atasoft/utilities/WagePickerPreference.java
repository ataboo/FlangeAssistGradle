package com.atasoft.utilities;

import android.content.Context;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


/**
 * Created by ataboo on 2016-05-17.
 */
public class WagePickerPreference extends EditTextPreference implements Preference.OnPreferenceChangeListener {
    public WagePickerPreference(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public WagePickerPreference(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        return super.onCreateView(parent);
    }

    /*
    @Override
    protected View onCreateDialogView() {
        //View v = super.onCreateDialogView();

        EditText editText = new EditText(getContext());


        editText.setText("banana");

        return editText;
    }
    */

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if(positiveResult){

        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if(((String) newValue).contains("b")){
            Log.w("WagePickerPreference", "Contains b... no go bitch.");
            return false;
        }

        return true;
    }
}
