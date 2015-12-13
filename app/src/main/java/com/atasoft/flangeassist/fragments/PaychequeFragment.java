 package com.atasoft.flangeassist.fragments;


import android.content.*;
import android.os.*;
import android.preference.*;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.util.Log;

import com.atasoft.flangeassist.*;
import com.atasoft.flangeassist.PayCalcClasses.PayCalcData;
import com.atasoft.flangeassist.PayCalcClasses.TaxManager;
import com.atasoft.helpers.*;

import java.text.DecimalFormat;
import java.text.NumberFormat;


 public class PaychequeFragment extends Fragment implements OnClickListener
{
    public enum DayType {
		FIVE_WEEK,
		FIVE_END,
		FOUR_WEEK,
		FOUR_FRI,
		FOUR_END
	}

	public static final String NAME = "Paycheque Calculator";
	private double[] wageRates;
    private View thisFragView;


	private SharedPreferences prefs;
	private Context context;
	private Boolean customDay;
	private String oldProvWage;
	private TaxManager taxManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.paycalc, container, false);
        thisFragView = v;
		context = getActivity().getApplicationContext();

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.thisFragView = getView();
        //this.context = thisFragView.getContext();
        this.context = getActivity().getApplicationContext();
        setupViews();

    }

    @Override
	public void onResume() {
		redoSpinners();
        loadState();
        super.onResume();
    }

    @Override
    public void onPause() {
        saveState();
        super.onPause();
    }

    private void redoSpinners(){
		Boolean custDayCheck = prefs.getBoolean(getString(R.string.pref_custDaysOn), false);
		if(custDayCheck) {
            if(customDay != custDayCheck) updateDaySpinners(custDayCheck);
		}
		String provWage = prefs.getString("list_provWageNew",  TaxManager.Prov.AB.getName());
		if(!provWage.equals(oldProvWage)){
            oldProvWage = provWage;
            setupViewsForProvince();
        }
		updateCalcOutput();
	}

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
			case R.id.clr_but:
				preSets(0);
				break;
			case R.id.tens_but:
			    preSets(1);
				break;
			case R.id.twelves_but:
			    preSets(2);
				break;
			default:  // Refreshes calc when other buttons called
			    updateCalcOutput();
				break;
        }
    }

    private Spinner sunSpin;
    private Spinner monSpin;
    private Spinner tueSpin;
    private Spinner wedSpin;
    private Spinner thuSpin;
    private Spinner friSpin;
    private Spinner satSpin;
    private Spinner mealSpin;
    private Spinner loaSpin;
    private Spinner wageSpin;
    private CheckBox taxToggle;
    private CheckBox cppToggle;
    private CheckBox fieldDuesToggle;
    private CheckBox monthlyDuesToggle;
    private CheckBox monHol;
    private CheckBox tueHol;
    private CheckBox wedHol;
    private CheckBox thuHol;
    private CheckBox friHol;
    private TextView sTimeText;
    private TextView hTimeText;
    private TextView dTimeText;
    private TextView wageRateVal;
    private TextView vacationVal;
    private TextView grossVal;
    private TextView exemptVal;
    private TextView dedVal;
    private TextView netVal;
    private ToggleButton fourToggle;
    private ToggleButton nightToggle;
    private ToggleButton travelToggle;
    private ToggleButton dayTravelToggle;


    private void setupViews(){
        if(taxManager == null){
            this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
            String provName = prefs.getString(getString(R.string.pref_prov), TaxManager.Prov.AB.getName());
            this.taxManager = new TaxManager(provName);
        }

        Button bClr = (Button) thisFragView.findViewById(R.id.clr_but);
        Button bTens = (Button) thisFragView.findViewById(R.id.tens_but);
        Button bTwelves = (Button) thisFragView.findViewById(R.id.twelves_but);
        fourToggle = (ToggleButton) thisFragView.findViewById(R.id.four_but);
        nightToggle = (ToggleButton) thisFragView.findViewById(R.id.night_but);
        travelToggle = (ToggleButton) thisFragView.findViewById(R.id.travel_but);
        dayTravelToggle = (ToggleButton) thisFragView.findViewById(R.id.travelday_but);
        taxToggle = (CheckBox) thisFragView.findViewById(R.id.tax_val);
        cppToggle = (CheckBox) thisFragView.findViewById(R.id.cpp_val);
        fieldDuesToggle = (CheckBox) thisFragView.findViewById(R.id.dues_val);
        monthlyDuesToggle = (CheckBox) thisFragView.findViewById(R.id.monthlydues_val);

        monHol = (CheckBox) thisFragView.findViewById(R.id.hol_mon);
        tueHol = (CheckBox) thisFragView.findViewById(R.id.hol_tue);
        wedHol = (CheckBox) thisFragView.findViewById(R.id.hol_wed);
        thuHol = (CheckBox) thisFragView.findViewById(R.id.hol_thu);
        friHol = (CheckBox) thisFragView.findViewById(R.id.hol_fri);

        bClr.setOnClickListener(this);
        bTens.setOnClickListener(this);
        bTwelves.setOnClickListener(this);
        fourToggle.setOnClickListener(this);
        nightToggle.setOnClickListener(this);
        travelToggle.setOnClickListener(this);
        dayTravelToggle.setOnClickListener(this);
        taxToggle.setOnClickListener(this);
        cppToggle.setOnClickListener(this);
        fieldDuesToggle.setOnClickListener(this);
        monthlyDuesToggle.setOnClickListener(this);
        monHol.setOnClickListener(this);
        tueHol.setOnClickListener(this);
        wedHol.setOnClickListener(this);
        thuHol.setOnClickListener(this);
        friHol.setOnClickListener(this);

        sTimeText = (TextView) thisFragView.findViewById(R.id.sing_val);
        hTimeText = (TextView) thisFragView.findViewById(R.id.half_val);
        dTimeText = (TextView) thisFragView.findViewById(R.id.doub_val);
        wageRateVal = (TextView) thisFragView.findViewById(R.id.wageRate_val);
        vacationVal = (TextView) thisFragView.findViewById(R.id.vacation_val);
        grossVal = (TextView) thisFragView.findViewById(R.id.gross_val);
        exemptVal = (TextView) thisFragView.findViewById(R.id.exempt_val);
        dedVal = (TextView) thisFragView.findViewById(R.id.deduct_val);
        netVal = (TextView) thisFragView.findViewById(R.id.net_val);

        setupSpinners();
    }


	private void setupSpinners() {
		sunSpin = (Spinner) thisFragView.findViewById(R.id.sunSpin);
		monSpin = (Spinner) thisFragView.findViewById(R.id.monSpin);
		tueSpin = (Spinner) thisFragView.findViewById(R.id.tueSpin);
		wedSpin = (Spinner) thisFragView.findViewById(R.id.wedSpin);
		thuSpin = (Spinner) thisFragView.findViewById(R.id.thuSpin);
		friSpin = (Spinner) thisFragView.findViewById(R.id.friSpin);
		satSpin = (Spinner) thisFragView.findViewById(R.id.satSpin);

		mealSpin = (Spinner) thisFragView.findViewById(R.id.meals_spin);
		loaSpin = (Spinner) thisFragView.findViewById(R.id.loa_spin);
		wageSpin = (Spinner) thisFragView.findViewById(R.id.wageSpin);


        //If Custom Day is active then they are added to the spinners
		updateDaySpinners(prefs.getBoolean(getString(R.string.pref_custDaysOn), false));

        ArrayAdapter<String> weekCount = new ArrayAdapter<String>(getActivity().getApplicationContext(),
                android.R.layout.simple_spinner_item,
				new String[]{"0","1","2","3","4","5","6","7"});
        loaSpin.setAdapter(weekCount);
        mealSpin.setAdapter(weekCount);

		setupViewsForProvince();

        addListenerToSpinner(monSpin);
        addListenerToSpinner(tueSpin);
        addListenerToSpinner(wedSpin);
        addListenerToSpinner(thuSpin);
        addListenerToSpinner(friSpin);
        addListenerToSpinner(satSpin);
        addListenerToSpinner(sunSpin);
        addListenerToSpinner(loaSpin);
        addListenerToSpinner(mealSpin);
        addListenerToSpinner(wageSpin);

		updateCalcOutput();
	}

    private void addListenerToSpinner(Spinner spinner){
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                updateCalcOutput();
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

	private void updateDaySpinners(Boolean custDayCheck){
		String[] workHrs;

		customDay = custDayCheck;
        verifyCustDays();
		if(customDay) {
			workHrs = new String[] {"0","8","10","12","13","A","B","C"};
		} else {
		    workHrs = new String[] {"0","8","10","12","13"};
        	customDay = false;  //for pushbootan usage if invalid
		}

		/*
		ArrayAdapter<String> weekAd = new ArrayAdapter<String>(getActivity().getApplicationContext(),
															   android.R.layout.simple_spinner_item, workHrs);
		*/

		ArrayAdapter<String> weekAd = new ArrayAdapter<String>(getActivity().getApplicationContext(),
			R.layout.spinner_layout, workHrs);

		monSpin.setAdapter(weekAd);
        tueSpin.setAdapter(weekAd);
        wedSpin.setAdapter(weekAd);
        thuSpin.setAdapter(weekAd);
        friSpin.setAdapter(weekAd);
        satSpin.setAdapter(weekAd);
        sunSpin.setAdapter(weekAd);
	}

	private void setupViewsForProvince() {
        oldProvWage = prefs.getString(getString(R.string.pref_prov), TaxManager.Prov.AB.getName());

        if(!TaxManager.validatePrefs(prefs)){
            Log.e("PaychequeFragment", "Province or Year prefs were malformed... Resetting them");
            prefs.edit().clear().apply();

            oldProvWage = TaxManager.Prov.AB.getName(); //Best Province

            SharedPreferences.Editor prefEdit =  prefs.edit();
            prefEdit.putString(getString(R.string.pref_prov), oldProvWage);
            prefEdit.putString(getString(R.string.pref_taxYear),
                    TaxManager.yearStrings[TaxManager.yearStrings.length - 1]);
            prefEdit.apply();
        }
		this.wageRates = floatToDoubArr(taxManager.getWageRates(oldProvWage));

		String[] wageNames = taxManager.getWageNames(oldProvWage);
        ArrayAdapter<String> wageAdapt = new ArrayAdapter<String>(getActivity().getApplicationContext(),
                android.R.layout.simple_spinner_item, wageNames);

        wageSpin.setAdapter(wageAdapt);

        int defaultWage = AtaMathUtils.bracketInt((int)wageRates[wageRates.length - 1], 0, wageSpin.getAdapter().getCount()-1);
        String[] mealSpinnerVals = prefs.getString(getString(R.string.pref_wageMealSpinners), "5,0,0,").split(",");
        mealSpinnerVals[0] = Integer.toString(defaultWage);
        StringBuilder builder = new StringBuilder();
        for(int i=0; i<mealSpinnerVals.length; i++){
            builder.append(mealSpinnerVals[i]);
            builder.append(",");
        }
        prefs.edit().putString(getString(R.string.pref_wageMealSpinners), builder.toString()).apply();
        //Log.w("PaychequeFragment", "wageMeaSpinner is: " + prefs.getString("payCalc_wageMealSpinners", "Failed"));
    }

    // TODO: stretch this out a bit and use string references
    private static final String[] custDayKeys = {"custom_a", "custom_b", "custom_c"};
    private static final String[] custDaySuffix = {"_straight", "_overtime", "_double"};
    private static final String[] custDayNames = {"Day A", "Day B", "Day C"};
	private void verifyCustDays() {
		for(int i = 0; i < custDayKeys.length; i++) {
            for(int j=0; j< custDaySuffix.length; j++){
                double[] parsedDay = parseDay(prefs.getString(custDayKeys[i] + custDaySuffix[j], "0"));
                if(parsedDay[1] == 0){
                    SharedPreferences.Editor pEdit = prefs.edit();
                    pEdit.putString(custDayKeys[i] + custDaySuffix[j], "0").apply();
                    Toast.makeText(context, custDayNames[i] + custDaySuffix[j].replace("_", " ") +
                            " was invalid... resetting",Toast.LENGTH_SHORT).show();
                }
            }
		}
	}

    //second value is fail check
	private double[] parseDay(String testStr) {
        double[] strParse = {0d, 0d};
        try{
            strParse[0] = Double.parseDouble(testStr);
        }
        catch(NumberFormatException e) {
            return strParse;
        }
        double bracketedParse = AtaMathUtils.bracketDouble(strParse[0], 0, 24);
        if(strParse[0] == bracketedParse) strParse[1] = 1d;
        return new double[]{bracketedParse, strParse[1]};
    }

	private void updateCalcOutput() {
        String provString = prefs.getString(getString(R.string.pref_prov), TaxManager.Prov.AB.getName());
        String yearString = prefs.getString(getString(R.string.pref_taxYear), TaxManager.yearStrings[TaxManager.yearStrings.length - 1]);

        PayCalcData payCalcData = new PayCalcData(taxManager, provString, yearString);

        int mealCount = Integer.parseInt(mealSpin.getSelectedItem().toString());
        int loaCount = Integer.parseInt(loaSpin.getSelectedItem().toString());
        float mealRate = checkPref(getString(R.string.pref_customMeal), 40, "Meal Rate");
        float loaRate = checkPref(getString(R.string.pref_loaRate), 195, "LOA Rate");

        payCalcData.setMealLOABonuses(mealCount * mealRate, prefs.getBoolean(getString(R.string.pref_taxableMeals), true), loaCount * loaRate, false);

        float weekTravelRate = travelToggle.isChecked() ? checkPref(getString(R.string.pref_weekTravelRate), 216, "Weekly Travel Rate") : 0f;
        float dayTravelRate = dayTravelToggle.isChecked() ? checkPref(getString(R.string.pref_dayTravelRate), 20, "Daily Travel") : 0f;
        boolean dayTravelTaxable = prefs.getBoolean(getString(R.string.pref_dayTravelTax), false);
        boolean weekTravelTaxable = prefs.getBoolean(getString(R.string.pref_weekTravelTax), false);

        payCalcData.setTravelBonuses(dayTravelRate, dayTravelTaxable, weekTravelRate, weekTravelTaxable);

        boolean nightOT = taxManager.getNightOT(provString);
        float nightPremium = 0f;
        if(!nightOT){
            boolean customNightRate = prefs.getBoolean(getString(R.string.pref_nightPremOn), false);
            nightPremium = customNightRate ? checkPref(getString(R.string.pref_nightPrem), 3f, "Custom Night Shift") : taxManager.getNightPremium(provString);
        }
        payCalcData.setNightPremium(nightPremium);

        float monthlyDues = 0f;
        if(monthlyDuesToggle.isChecked()){
            monthlyDues = prefs.getBoolean(getString(R.string.pref_monthDuesOn), false) ?
                    checkPref(getString(R.string.pref_monthDuesRate), 37.90f, "Monthly Dues") :
                    taxManager.getMonthlyDues(provString);
        }

        float fieldDuesRate = 0f;
        if(fieldDuesToggle.isChecked()) {
            fieldDuesRate = prefs.getBoolean(getString(R.string.pref_fieldDuesOn), false) ?
                    checkPref(getString(R.string.pref_fieldDuesRate), 3.75f, "Field Dues") / 100f:
                    taxManager.getFieldDues(provString);
        }
        payCalcData.setDues(fieldDuesRate, monthlyDues);

        boolean doubleOT = taxManager.getDoubleOT(provString);

        Spinner[] spinArr = {sunSpin, monSpin, tueSpin, wedSpin, thuSpin, friSpin, satSpin};
        boolean[] weekHolidays = {true, monHol.isChecked(), tueHol.isChecked(), wedHol.isChecked(),
                thuHol.isChecked(),friHol.isChecked(), true};  //sat and sun count as holidays
        for(int i=0; i<spinArr.length; i++){
            float[] splitArr;
            String itemStr = (spinArr[i].getSelectedItem().toString());
            if(itemStr.contains("A") || itemStr.contains("B") || itemStr.contains("C")){
                splitArr = getCustomDayPrefs(itemStr);
            } else {
                String selectedShift = (String) spinArr[i].getSelectedItem();
                splitArr = new float[]{Float.parseFloat(selectedShift)};
            }
            // Sends to add hours as weekend if double time checked
            int dayIndex = weekHolidays[i] ? 0 : i;

            payCalcData.addHours(splitArr, fourToggle.isChecked(), nightToggle.isChecked(), dayIndex, nightOT, doubleOT);
        }

        float wageRate;
        if(wageSpin.getSelectedItem().toString().contains("Custom")) {
            wageRate = checkPref(getString(R.string.pref_custom_wage), 20, "Custom Wage");
        } else {
            wageRate = (float) wageRates[wageSpin.getSelectedItemPosition()];
        }

        float vacRate = (prefs.getBoolean(getString(R.string.pref_vacOn), false)) ?
                checkPref(getString(R.string.pref_vacRate), 10.5f, "Custom Vac Rate") / 100f : taxManager.getVacationRate(provString);

        PayCalcData.EarningHolder earnings = payCalcData.getEarnings(wageRate, vacRate);

        float addTax = checkPref(getString(R.string.pref_addTax), 0, "Add-tax Rate");

        PayCalcData.DeductionHolder deductions = payCalcData.getDeductions(addTax);

        if(!taxToggle.isChecked()){
            deductions.fedTax = deductions.provTax = 0f;
        }

        if(!cppToggle.isChecked()){
            deductions.cpp = deductions.ei = 0f;
        }

        float fieldDues = deductions.getFieldDues(earnings.getDuesTaxable());
        float deductionsSum = deductions.getDeductionsSum(earnings.getDuesTaxable());
        float netPay = earnings.getGross() - deductionsSum;
        float[] hoursSum = payCalcData.getHoursSum();

        wageRateVal.setText(String.format("Wage: %.2f$", wageRate));
        vacationVal.setText(String.format("Vac\\Hol (%.2f%%): %.2f$", vacRate * 100d, earnings.vacationBonus));
        grossVal.setText(String.format("Gross: %.2f$", earnings.getGross()));
        exemptVal.setText(String.format("Tax Exempt: %.2f$", earnings.getExempt()));
        cppToggle.setText(String.format("EI/CPP: %.2f$ + %.2f$", deductions.ei, deductions.cpp));
		fieldDuesToggle.setText(String.format("Work Dues (%.2f%%): %.2f$", fieldDuesRate * 100, fieldDues));
		monthlyDuesToggle.setText(String.format("Monthly Dues: %.2f$", deductions.monthlyDues));
		dedVal.setText(String.format("Deductions: %.2f$", deductionsSum));
		netVal.setText(String.format("Takehome: %.2f$", netPay));
		sTimeText.setText("1.0x: " + twoPrecision(hoursSum[0]));
		hTimeText.setText("1.5x: " + twoPrecision(hoursSum[1]));
		dTimeText.setText("2.0x: " + twoPrecision(hoursSum[2]));

		if(addTax == 0) {
			taxToggle.setText(String.format("Tax: %.2f$", deductions.getTaxes()));
		} else {
			taxToggle.setText(String.format("Tax: %.2f$ + %.2f$", deductions.getTaxes(), addTax));
		}
	}

	//unnecessary double
	private double checkPrefDouble(String preferenceKey, double defaultVal, String toastName) {
		double retVal;
        String defaultString = Double.toString(defaultVal);
		String prefString = prefs.getString(preferenceKey, defaultString);
		try {
			retVal = Double.parseDouble(prefString);
		}
		catch (NumberFormatException e) {
			setPrefDefault(preferenceKey, defaultString);
			Toast.makeText(context, toastName + " wasn't a number.", Toast.LENGTH_SHORT).show();
			return defaultVal;
		}
		if(retVal > 100000 || retVal < 0) {
			setPrefDefault(preferenceKey, defaultString);
			Toast.makeText(context, toastName + " was out of range.", Toast.LENGTH_SHORT).show();
			return defaultVal;
		}
		return retVal;
	}

    private float checkPref(String prefKey, float defaultVal, String errorName){
        float retVal;
        String defaultString = Float.toString(defaultVal);
        String prefString = prefs.getString(prefKey, defaultString);
        try {
            retVal = Float.parseFloat(prefString);
        }
        catch (NumberFormatException e) {
            setPrefDefault(prefKey, defaultString);
            Toast.makeText(context, errorName + " isn't a number.", Toast.LENGTH_SHORT).show();
            return defaultVal;
        }
        if(retVal > 100000 || retVal < 0) {
            setPrefDefault(prefKey, defaultString);
            Toast.makeText(context, errorName + " is out of range.", Toast.LENGTH_SHORT).show();
            return defaultVal;
        }
        return retVal;
    }

	void setPrefDefault(String prefKey, String defaultVal){
		SharedPreferences.Editor prefEdit = prefs.edit();
		prefEdit.putString(prefKey, defaultVal);
		prefEdit.apply();
    }

	private void preSets(int index){
		if(index == 0) {
        	sunSpin.setSelection(0, false);
        	monSpin.setSelection(0, false);
        	tueSpin.setSelection(0, false);
        	wedSpin.setSelection(0, false);
        	thuSpin.setSelection(0, false);
        	friSpin.setSelection(0, false);
        	satSpin.setSelection(0, false);
        	mealSpin.setSelection(0, false);
        	loaSpin.setSelection(0, false);
        	return;
        } else {
			index += 1;
        	sunSpin.setSelection(index, false);
        	monSpin.setSelection(index, false);
        	tueSpin.setSelection(index, false);
        	wedSpin.setSelection(index, false);
        	thuSpin.setSelection(index, false);
        	friSpin.setSelection(index, false);
        	satSpin.setSelection(index, false);
        	mealSpin.setSelection(0, false);
        	loaSpin.setSelection(0, false);
        }
        if(index == 3) {
        	mealSpin.setSelection(7, false);
        }
    }

	private float[] getCustomDayPrefs(String itemStr) {
        String prefName = "";
		float[] dayFloats = new float[custDaySuffix.length];

		if(itemStr.contains("A")) prefName = custDayKeys[0];
		if(itemStr.contains("B")) prefName = custDayKeys[1];
		if(itemStr.contains("C")) prefName = custDayKeys[2];
		for(int i=0; i<custDaySuffix.length; i++) {
            try{
                dayFloats[i] = Float.parseFloat(prefs.getString(prefName + custDaySuffix[i], "0"));
            } catch(NumberFormatException nfe){
                Log.e("PaychequeFragment", itemStr + ", suffix " + custDaySuffix[i] + " NumberFormatException.");
                dayFloats[i] = 0f;
            }
		}
        //Log.w("PaychequeFragment", String.format("dayFloats = %.2f, %.2f, %.2f", dayFloats[0], dayFloats[1], dayFloats[2]));
		return dayFloats;
	}

	private double[] hrsSplit(double hrs, DayType day) {
	//day 0 = 5-8s, 1 = 4-10s mon-thu, 2 = 4-10s fri, 3 = weekend
		double sTime = 0;
		double hTime = 0;
		double dTime = 0;
		switch(day) {
			case FIVE_WEEK:
				if (hrs > 10) {
					dTime = hrs - 10;
				}
				if (hrs > 8) {
					hTime = hrs - dTime - 8;
				}
				sTime = hrs - dTime - hTime;
				break;
			case FOUR_WEEK:
				if(hrs > 10) {
					dTime = hrs - 10;
				}
				sTime = hrs - dTime;
				break;
			case FOUR_FRI:
				if(hrs > 10) {
					dTime = hrs - 10;
				}
				hTime = hrs - dTime;
				break;
			default:
				dTime = hrs;
				break;
		}
		return new double[]{sTime, hTime, dTime};
	}

	private double calcDues(double grossNoVac, double duesRate) {
		return grossNoVac * duesRate;
	}

    //Groups like views and serializes to CSV strings
    private void saveState(){
        //Had to make the arrays local or they'd be null before they were initialized
        Spinner[] weekSpinners = {sunSpin, monSpin, tueSpin, wedSpin, thuSpin, friSpin, satSpin};
        Spinner[] wageMealSpinners = {wageSpin, mealSpin, loaSpin};
        CompoundButton[] saveChecks = {monHol, tueHol, wedHol, thuHol, friHol,  //Checkboxes
                taxToggle, cppToggle, fieldDuesToggle, monthlyDuesToggle,
                fourToggle, nightToggle, travelToggle, dayTravelToggle};
        if(prefs == null || wageSpin == null) return;

        StringBuilder weekStringBuild = new StringBuilder();
        for(int i=0; i<weekSpinners.length; i++){
            if(weekSpinners[i]==null) {
                return;
            }
            weekStringBuild.append(Integer.toString(weekSpinners[i].getSelectedItemPosition())).append(",");
        }
        StringBuilder holidayStringBuild = new StringBuilder();
        for(int i=0; i<saveChecks.length; i++){
            int boolInt = (saveChecks[i].isChecked()) ? 1 : 0;
            holidayStringBuild.append(boolInt).append(",");
        }
        StringBuilder wageMealBuild = new StringBuilder();
        for(int i=0; i<wageMealSpinners.length; i++){
            wageMealBuild.append(wageMealSpinners[i].getSelectedItemPosition()).append(",");
        }

        SharedPreferences.Editor prefEdit = prefs.edit();
        prefEdit.putString("payCalc_weekSpinners", weekStringBuild.toString());
        prefEdit.putString("payCalc_saveChecks", holidayStringBuild.toString());
        prefEdit.putString("payCalc_wageMealSpinners", wageMealBuild.toString());

        //prefEdit.putStringSet();
        prefEdit.apply();
    }

    private void loadState(){
        Spinner[] weekSpinners = {sunSpin, monSpin, tueSpin, wedSpin, thuSpin, friSpin, satSpin};
        Spinner[] wageMealSpinners = {wageSpin, mealSpin, loaSpin};
        CompoundButton[] saveChecks = {monHol, tueHol, wedHol, thuHol, friHol,  //Checkboxes
                taxToggle, cppToggle, fieldDuesToggle, monthlyDuesToggle,
                fourToggle, nightToggle, travelToggle, dayTravelToggle};
        if(prefs == null) return;

        String[] weekSpinnerIndices = prefs.getString("payCalc_weekSpinners", "0,0,0,0,0,0,0,").split(",");
        for(int i=0; i<weekSpinners.length; i++){
            if(weekSpinners[i] == null) return;
            weekSpinners[i].setSelection(AtaMathUtils.bracketInt(weekSpinnerIndices[i], 0, 6));
        }
        String[] saveChecksIndices = prefs.getString("payCalc_saveChecks", "0,0,0,0,0,1,1,1,0,0,0,0,0,").split(",");
        for(int i=0; i<saveChecks.length; i++){
            saveChecks[i].setChecked(saveChecksIndices[i].equals("1"));
        }
        String[] wageMealIndices = prefs.getString("payCalc_wageMealSpinners", "5,0,0,").split(",");
        //Incase number of items in wage spinner has changed with province between saves
        wageMealSpinners[0].setSelection(AtaMathUtils.bracketInt(
                wageMealIndices[0], 0, wageMealSpinners[0].getAdapter().getCount() - 1));
        wageMealSpinners[1].setSelection(AtaMathUtils.bracketInt(wageMealIndices[1], 0, 6));
        wageMealSpinners[2].setSelection(AtaMathUtils.bracketInt(wageMealIndices[2], 0, 6));
    }

    public static String twoPrecision (double d){
        NumberFormat nf = new DecimalFormat("###.##");
        return nf.format(d);
    }

    public static double[] floatToDoubArr(float[] fArr){
        double[] retArr = new double[fArr.length];

        for(int i=0; i<fArr.length; i++){
            retArr[i] = (double) fArr[i];
        }
        return retArr;
    }
}
