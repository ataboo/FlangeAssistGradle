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
    private double[] vacRates;
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
		Boolean custDayCheck = prefs.getBoolean("custom_daycheck", false);
		if(custDayCheck) {
            if(customDay != custDayCheck) updateDaySpinners(custDayCheck);
		}
		String provWage = prefs.getString("list_provWageNew", TaxManager.provinceNames[1]);
		if(!provWage.equals(oldProvWage)){
            oldProvWage = provWage;
            setupViewsForProvince();
        }
		pushBootan();
		
		
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
			    pushBootan();
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
    private CheckBox taxVal;
    private CheckBox cppVal;
    private CheckBox duesVal;
    private CheckBox monthlyDuesVal;
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
            String provName = prefs.getString("list_provWageNew", TaxManager.provinceNames[1]);
            this.taxManager = new TaxManager(provName);
            TaxStatHolder statHolder = new TaxStatHolder(TaxManager.PROV_NB);  //int don't do nuttin yet.
            Log.w("PaychequeFrag", statHolder.surName);
			//new TaxManStat(provName);
        }

        Button bClr = (Button) thisFragView.findViewById(R.id.clr_but);
        Button bTens = (Button) thisFragView.findViewById(R.id.tens_but);
        Button bTwelves = (Button) thisFragView.findViewById(R.id.twelves_but);
        fourToggle = (ToggleButton) thisFragView.findViewById(R.id.four_but);
        nightToggle = (ToggleButton) thisFragView.findViewById(R.id.night_but);
        travelToggle = (ToggleButton) thisFragView.findViewById(R.id.travel_but);
        dayTravelToggle = (ToggleButton) thisFragView.findViewById(R.id.travelday_but);
        taxVal = (CheckBox) thisFragView.findViewById(R.id.tax_val);
        cppVal = (CheckBox) thisFragView.findViewById(R.id.cpp_val);
        duesVal = (CheckBox) thisFragView.findViewById(R.id.dues_val);
        monthlyDuesVal = (CheckBox) thisFragView.findViewById(R.id.monthlydues_val);

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
        taxVal.setOnClickListener(this);
        cppVal.setOnClickListener(this);
        duesVal.setOnClickListener(this);
        monthlyDuesVal.setOnClickListener(this);
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
		updateDaySpinners(prefs.getBoolean("custom_daycheck", false));

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

		pushBootan();
	}

    private void addListenerToSpinner(Spinner spinner){
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                pushBootan();
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
        oldProvWage = prefs.getString("list_provWageNew", TaxManager.provinceNames[1]);

        if(!TaxManager.validatePrefs(prefs)){
            Log.e("PaychequeFragment", "Province or Year prefs were malformed... Resetting them");
            prefs.edit().clear().apply();

            oldProvWage = TaxManager.provinceNames[TaxManager.PROV_AB]; //Best Province

            SharedPreferences.Editor prefEdit =  prefs.edit();
            prefEdit.putString("list_provWageNew", oldProvWage);
            prefEdit.putString("list_taxYearNew",
                    TaxManager.yearStrings[TaxManager.yearStrings.length - 1]);
            prefEdit.apply();
        }
        
		this.vacRates = taxManager.getVacationRate(oldProvWage);
		this.wageRates = taxManager.getWageRates(oldProvWage);
		
		String[] wageNames = taxManager.getWageNames(oldProvWage);
        ArrayAdapter<String> wageAdapt = new ArrayAdapter<String>(getActivity().getApplicationContext(),
                android.R.layout.simple_spinner_item, wageNames);

        wageSpin.setAdapter(wageAdapt);

        int defaultWage = AtaMathUtils.bracketInt((int)wageRates[wageRates.length - 1], 0, wageSpin.getAdapter().getCount()-1);
        String[] mealSpinnerVals = prefs.getString("payCalc_wageMealSpinners", "5,0,0,").split(",");
        mealSpinnerVals[0] = Integer.toString(defaultWage);
        StringBuilder builder = new StringBuilder();
        for(int i=0; i<mealSpinnerVals.length; i++){
            builder.append(mealSpinnerVals[i]);
            builder.append(",");
        }
        prefs.edit().putString("payCalc_wageMealSpinners", builder.toString()).commit();
        //Log.w("PaychequeFragment", "wageMeaSpinner is: " + prefs.getString("payCalc_wageMealSpinners", "Failed"));
    }
    
    private static final String[] custDayKeys = {"custom_a", "custom_b", "custom_c"};
    private static final String[] custDaySuffix = {"_straight", "_overtime", "_double"};
    private static final String[] custDayNames = {"Day A", "Day B", "Day C"};
	private void verifyCustDays() {
		for(int i = 0; i < custDayKeys.length; i++) {
            for(int j=0; j< custDaySuffix.length; j++){
                double[] parsedDay = parseDay(prefs.getString(custDayKeys[i] + custDaySuffix[j], "0"));
                if(parsedDay[1] == 0){
                    SharedPreferences.Editor pEdit = prefs.edit();
                    pEdit.putString(custDayKeys[i] + custDaySuffix[j], "0").commit();
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

	private void pushBootan() {
        //Log.w("Paycheque Fragment", "Selection is actually " + wageSpin.getSelectedItemPosition() + " also a load of crap.");
		double splitArr[];
		boolean fourTens = fourToggle.isChecked();

		double timeSum[] = {0,0,0};

		int loaCount = Integer.parseInt(loaSpin.getSelectedItem().toString());
		int mealCount = Integer.parseInt(mealSpin.getSelectedItem().toString());
		double wageRate;
		boolean[] weekHolidays = {true, monHol.isChecked(), tueHol.isChecked(), wedHol.isChecked(),thuHol.isChecked(),friHol.isChecked(), true};  //sat and sun count as holidays
		double addTax = checkPrefDouble("custom_addtax", 0, "Addtax Rate");
		double mealRate = checkPrefDouble("custom_mealrate", 40, "Meal Rate");
		double weekTravel = checkPrefDouble("custom_weektravel", 216, "Weekly Travel Rate");
		double dayTravel = checkPrefDouble("custom_daytravel", 20, "Daily Travel");
		double loaRate = checkPrefDouble("custom_loa", 195, "LOA Rate");
		double monthlyDues = checkPrefDouble("custom_monthly_dues", 37.90, "Monthly Dues");
		double workingDuesRate = checkPrefDouble("custom_working_dues", .0375, "Working Dues");
        double customVac = checkPrefDouble("custom_vac_rate", 10.5, "Custom Vac Rate") / 100f;

        boolean vacIsCustom = prefs.getBoolean("custom_vac_check", false);
        double vacRate = vacIsCustom ? customVac : vacRates[0];

		if(wageSpin.getSelectedItem().toString().contains("Custom")) {
		    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

            float wageFloat = AtaMathUtils.bracketFloat(prefs.getString("custom_wage", "20"), 0f, 1000000000f);
			wageRate = (double) wageFloat;
		} else {
			int selectedWageIndex = wageSpin.getSelectedItemPosition();
            wageRate = wageRates[selectedWageIndex];
            if(!vacIsCustom && vacRates.length == wageRates.length-1) {
                vacRate = vacRates[selectedWageIndex]; //Some provinces have graduated vacation rates
            }

		}
		int dayCount = 0;
		Spinner[] spinArr = {sunSpin, monSpin, tueSpin, wedSpin, thuSpin, friSpin, satSpin};
			for (int i = 0; i < spinArr.length; i++) {
				String itemStr = (spinArr[i].getSelectedItem().toString());
				if(itemStr.contains("A") || itemStr.contains("B") || itemStr.contains("C")){
					splitArr = getCustomDayPrefs(itemStr);
				} else {
					DayType dayTypeSet;
					Boolean weekEnd = weekHolidays[i];
					if(fourTens) {
						if(i == 5) { //fourtens friday
							dayTypeSet = DayType.FOUR_FRI;
						} else {
							dayTypeSet = DayType.FOUR_WEEK;
						}
					} else {
						dayTypeSet = DayType.FIVE_WEEK;
					}
					if(weekEnd) dayTypeSet = DayType.FIVE_END;  //works for fourtens too
					splitArr = hrsSplit(Double.parseDouble(spinArr[i].getSelectedItem().toString()), dayTypeSet);
				}
				if(splitArr[0] + splitArr[1] + splitArr[2] > 0) dayCount++;
				
				timeSum[0] += splitArr[0];
				timeSum[1] += splitArr[1];
				timeSum[2] += splitArr[2];
			}				
		double grossPay = wageRate * (timeSum[0] + (1.5 * timeSum[1]) + (2 * timeSum[2]));

		if(nightToggle.isChecked()) grossPay += (timeSum[0] + timeSum[1] + timeSum[2]) * 3;



		double grossVac = grossPay * (vacRate + 1);
		double exempt = loaCount * loaRate;
		if(travelToggle.isChecked()) {
			if(!prefs.getBoolean("taxable_weektravel", false)){
				exempt += weekTravel;
			} else {
				grossVac += weekTravel;
			}
		}
		
		if(dayTravelToggle.isChecked()) {
			if(!prefs.getBoolean("taxable_daytravel", true)){
				exempt += dayTravel * dayCount;
			} else {
				grossVac += dayTravel * dayCount;
			}
			//Toast.makeText(getActivity().getApplicationContext(), "banana", Toast.LENGTH_SHORT).show();
		}
		
		if(prefs.getBoolean("taxable_meals", true)){
			grossVac += mealCount * mealRate; 
		} else {
			exempt += mealCount * mealRate;
		}
		
		double[] deductions = new double[]{0,0,0,0,0,0};  //[fed tax, prov tax, cpp, ei, working dues, monthly dues]
		
		String yearString = prefs.getString("list_taxYearNew", TaxManager.yearStrings[TaxManager.yearStrings.length-1]);
		String provString = prefs.getString("list_provWageNew", TaxManager.provinceNames[1]);
		
		double[] taxReturns = taxManager.getTaxes(grossVac, yearString, provString);
		
		boolean cppChecked = cppVal.isChecked();
		if(taxVal.isChecked()){
			deductions[0] = taxReturns[0];
			deductions[1] = taxReturns[1];
		}
		//Log.w("paycheckFrag", String.format("Fed Tax is: %.2f, Prov tax is: %.2f", deductions[0], deductions[1]));
		
		if(cppChecked) {
			deductions[2] = taxReturns[2];
			deductions[3] = taxReturns[3];
		}
		deductions[0] += addTax;
		deductions[4] = duesVal.isChecked() ? calcDues(grossPay, workingDuesRate): 0;
		deductions[5] = monthlyDuesVal.isChecked() ? monthlyDues : 0;
		
		double deductionsSum = 0;
		for(double i: deductions) {
			deductionsSum += i;
		}
		
		double netPay = grossVac - deductionsSum + exempt;
		
		wageRateVal.setText("Wage: " + String.format("%.2f", wageRate) + "$");
		vacationVal.setText(String.format("Vac\\Hol (%.2f%%): %.2f$", vacRate * 100d, grossVac - grossPay));
		grossVal.setText("Gross: " + String.format("%.2f", grossVac + exempt) + "$");
		exemptVal.setText("Tax Exempt: " + String.format("%.2f", exempt) + "$");
		cppVal.setText(String.format("EI/CPP: %.2f$ + %.2f$", deductions[3], deductions[2]));
		duesVal.setText(String.format("Work Dues (%.2f%%): %.2f$", workingDuesRate * 100, deductions[4]));
		monthlyDuesVal.setText("Monthly Dues: " + String.format("%.2f", deductions[5]) + "$");
		dedVal.setText("Deductions: " + String.format("%.2f", deductionsSum) + "$");
		netVal.setText("Takehome: " + String.format("%.2f", netPay) + "$");
		sTimeText.setText("1.0x: " + twoPrecision(timeSum[0]));
		hTimeText.setText("1.5x: " + twoPrecision(timeSum[1]));
		dTimeText.setText("2.0x: " + twoPrecision(timeSum[2]));
		
		if(addTax == 0) {
			taxVal.setText("Tax: " + String.format("%.2f", deductions[0] + deductions[1]) + "$");
		} else {
			taxVal.setText("Tax: " + String.format("%.2f", deductions[0] + deductions[1] - addTax) + "$ + " +
				String.format("%.2f", addTax) + "$");
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
	
	void setPrefDefault(String prefKey, String defaultVal){
		SharedPreferences.Editor prefEdit = prefs.edit();
		prefEdit.putString(prefKey, defaultVal);
		prefEdit.commit();
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
	
	private double[] getCustomDayPrefs(String itemStr) {
        String prefName = "";
		double[] retDoub = new double[custDaySuffix.length];
		
		if(itemStr.contains("A")) prefName = custDayKeys[0];
		if(itemStr.contains("B")) prefName = custDayKeys[1];
		if(itemStr.contains("C")) prefName = custDayKeys[2];
		for(int i=0; i<custDaySuffix.length; i++) {
            try{
                retDoub[i] = Double.parseDouble(prefs.getString(prefName + custDaySuffix[i], "0"));
            } catch(NumberFormatException nfe){
                Log.e("PaychequeFragment", itemStr + ", suffix " + custDaySuffix[i] + " NumberFormatException.");
                retDoub[i] = 0d;
            }
		}
        //Log.w("PaychequeFragment", String.format("Retdoub = %.2f, %.2f, %.2f", retDoub[0], retDoub[1], retDoub[2]));
		return retDoub;
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
                taxVal, cppVal, duesVal, monthlyDuesVal,
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
        prefEdit.commit();
    }

    private void loadState(){
        Spinner[] weekSpinners = {sunSpin, monSpin, tueSpin, wedSpin, thuSpin, friSpin, satSpin};
        Spinner[] wageMealSpinners = {wageSpin, mealSpin, loaSpin};
        CompoundButton[] saveChecks = {monHol, tueHol, wedHol, thuHol, friHol,  //Checkboxes
                taxVal, cppVal, duesVal, monthlyDuesVal,
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
}
