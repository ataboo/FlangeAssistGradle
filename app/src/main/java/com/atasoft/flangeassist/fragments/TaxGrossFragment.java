package com.atasoft.flangeassist.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.*;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.atasoft.flangeassist.*;
import com.atasoft.helpers.AtaMathUtils;
import com.atasoft.helpers.TaxManager;

import java.util.prefs.Preferences;


public class TaxGrossFragment extends Fragment implements OnClickListener {

    private View thisFragView;
    private Context context;
    private SharedPreferences prefs;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tax_gross_layout, container, false);
        this.context = v.getContext();
        this.thisFragView = v;
        this.prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        setupViews();
        loadSettings();
        return v;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.tax_submitButton:
                goSubmit();
                break;
        }
    }

    @Override
    public void onPause() {
        saveSettings();
        super.onPause();
    }

    @Override
    public void onResume() {
        loadSettings();
        super.onResume();
    }

    private EditText grossEdit;
    private Spinner provSpinner;
    private Spinner taxYearSpinner;
    private Button submitButton;
    private TextView fedTaxText;
    private TextView provTaxText;
    private TextView cppEIText;
    private TextView totalDedText;
    private TextView netIncomeText;
    private static final String noProvinceName = "No Provincial";
    private void setupViews(){
        //===============================Find Views=================================================
        this.grossEdit = (EditText) thisFragView.findViewById(R.id.tax_grossEdit);
        this.submitButton = (Button) thisFragView.findViewById(R.id.tax_submitButton);
        this.fedTaxText = (TextView) thisFragView.findViewById(R.id.tax_fedTotalText);
        this.provTaxText = (TextView) thisFragView.findViewById(R.id.tax_provTotalText);
        this.cppEIText = (TextView) thisFragView.findViewById(R.id.tax_cpp_ei);
        this.totalDedText = (TextView) thisFragView.findViewById(R.id.tax_totalDed);
        this.netIncomeText = (TextView) thisFragView.findViewById(R.id.tax_netIncome);
        this.taxYearSpinner = (Spinner) thisFragView.findViewById(R.id.tax_yearSpinner);
        this.provSpinner = (Spinner) thisFragView.findViewById(R.id.tax_provSpinner);

        submitButton.setOnClickListener(this);
        //===================================Populate Spinners=====================================
        String[] resProv = TaxManager.getActiveProvinceStrings();
        String[] provArr = new String[resProv.length +1];
        provArr[0] = noProvinceName;

        System.arraycopy(resProv, 0, provArr, 1, resProv.length);
        ArrayAdapter<String> provAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, provArr);
        provSpinner.setAdapter(provAdapter);

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, TaxManager.yearStrings);
        taxYearSpinner.setAdapter(yearAdapter);
    }

    private void goSubmit(){
        Double weekGross;
        String provName;
        String yearName;
        //===================================Get Inputs=============================================
        try{
            weekGross = Double.parseDouble(grossEdit.getText().toString());
            provName = provSpinner.getSelectedItem().toString();
            yearName = taxYearSpinner.getSelectedItem().toString();
        } catch (NumberFormatException | NullPointerException e){
            e.printStackTrace();
            Log.e("TaxGrossFragment", "Null pointer or format exception.");
            weekGross = 0d;
            provName = noProvinceName;
            yearName = "";
        }
        //=====================================Calc Taxes===========================================
        double[] taxVals = TaxManager.getTaxes(weekGross, yearName, provName);
        double taxSum = 0d;
        for(double d: taxVals) taxSum+=d;

        //=====================================Set TextViews========================================
        Resources res = getResources();
        fedTaxText.setText(res.getString(R.string.tax_fedTotal_text) + String.format("   $%.2f", taxVals[0]));
        provTaxText.setText(res.getString(R.string.tax_provTotal_text) + String.format("   $%.2f", taxVals[1]));
        cppEIText.setText(res.getString(R.string.tax_cpp_ei) + String.format("   $%.2f,  $%.2f", taxVals[2], taxVals[3]));

        String totalDed = String.format("   $%.2f", taxSum);
        totalDedText.setText(res.getString(R.string.tax_total_ded) + totalDed);

        String netIncome = String.format("   $%.2f", weekGross - taxSum);
        netIncomeText.setText(res.getString(R.string.tax_netIncome_text) + netIncome);
     }

    private void loadSettings(){
        grossEdit.setText(prefs.getString("tax_weekGross", "0"));
        taxYearSpinner.setSelection(prefs.getInt("tax_yearIndex", TaxManager.yearStrings.length - 1));
        provSpinner.setSelection(prefs.getInt("tax_provIndex", 2)); //Best province.
    }

    private void saveSettings(){
        SharedPreferences.Editor prefEdit = prefs.edit();
            prefEdit.putString("tax_weekGross", grossEdit.getText().toString());
            prefEdit.putInt("tax_yearIndex", taxYearSpinner.getSelectedItemPosition());
            prefEdit.putInt("tax_provIndex", provSpinner.getSelectedItemPosition());
        prefEdit.commit();
    }
}
