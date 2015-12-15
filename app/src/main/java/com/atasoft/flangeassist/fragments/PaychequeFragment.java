 package com.atasoft.flangeassist.fragments;


import android.content.*;
import android.os.*;
import android.preference.*;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
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
    public enum HoursPreset {
        ZERO(0, 0), TENS(2, 0), TWELVES(3, 7);

        private int shiftIndex;
        private int mealCount;
        HoursPreset(int shiftIndex, int mealCount){
            this.shiftIndex = shiftIndex;
            this.mealCount = mealCount;
        }

        int getShiftIndex(){
            return shiftIndex;
        }

        int[] getBonusCounts(){
            return new int[]{mealCount, 0};
        }
    }

    public enum SpinnerData {
        SUN(R.id.sunSpin),
        MON(R.id.monSpin),
        TUE(R.id.tueSpin),
        WED(R.id.wedSpin),
        THU(R.id.thuSpin),
        FRI(R.id.friSpin),
        SAT(R.id.satSpin),
        MEAL(R.id.meals_spin),
        LOA(R.id.loa_spin),
        WAGE(R.id.wageSpin);

        private final String[] customDayShifts = {"0","8","10","12","13","A","B","C"};
        private final String[] standardDayShifts = {"0","8","10","12","13"};
        private final String[] bonusDayOptions = {"0","1","2","3","4","5","6","7"};

        private Spinner spinner;
        private int id;
        private String selection;

        SpinnerData(int id){
            this.id = id;
        }

        static SpinnerData getDataBySpinner(Spinner spinner){
            for(SpinnerData spinnerData: SpinnerData.values()){
                if(spinner.getId() == spinnerData.id){
                    return spinnerData;
                }
            }
            return null;
        }

        void setSelection(int index, boolean animated){
            spinner.setSelection(index, animated);
            // Update this.selection so selectionChanged is !true when checked via listener.
            getSelectedItem();
        }

        void setView(View parentView){
            this.spinner = (Spinner) parentView.findViewById(id);
        }

        boolean selectionChanged(){
            String newSelection = (String) spinner.getSelectedItem();
            return !newSelection.equals(selection);
        }

        String getSelectedItem(){
            this.selection = (String) spinner.getSelectedItem();
            return selection;
        }

        void updateShiftAdaptor(Boolean customDays, Context context){
            String[] shifts = customDays ? customDayShifts: standardDayShifts;
            ArrayAdapter<String> shiftAdapter = new ArrayAdapter<String>(context, R.layout.spinner_layout, shifts);
            spinner.setAdapter(shiftAdapter);
        }

        void updateBonusAdaptor(Context context){
            if(this != MEAL || this != LOA){
                //throw(new Error("updateBonusAdaptor invalid spinner type call."));
            }

            ArrayAdapter<String> bonusDayAdaptor = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, bonusDayOptions);
            spinner.setAdapter(bonusDayAdaptor);
        }



    }

    public static final String NAME = "Paycheque Calculator";
    private float[] wageRates;

    private SpinnerData[] daySpinners = {SpinnerData.SUN, SpinnerData.MON, SpinnerData.TUE, SpinnerData.WED, SpinnerData.THU, SpinnerData.FRI, SpinnerData.SAT};
    private SpinnerData[] bonusSpinners = {SpinnerData.MEAL, SpinnerData.LOA};
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

    private View thisFragView;
    private SharedPreferences prefs;
    private Context context;
    private Boolean customDay;
    private String oldProvWage;
    private static int minDayWidthDP = 310;

	private TaxManager taxManager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.paycalc, container, false);
        thisFragView = v;
		context = getActivity().getApplicationContext();
        resizeDayLayout();

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.thisFragView = getView();
        //this.context = thisFragView.getContext();
        this.context = getActivity().getApplicationContext();
        setupViews();
        setupSpinners();

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
            updateWageSpinner();
        }
		updateCalcOutput();
	}

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
			case R.id.clr_but:
				setShift(HoursPreset.ZERO);
				break;
			case R.id.tens_but:
			    setShift(HoursPreset.TENS);
				break;
			case R.id.twelves_but:
			    setShift(HoursPreset.TWELVES);
				break;
        }

        // update calc output on any button push.
        updateCalcOutput();
    }

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
    }

    private void resizeDayLayout(){
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        RelativeLayout daySpinnerRelative = (RelativeLayout) thisFragView.findViewById(R.id.payDayRelative);
        float logicalDensity = metrics.density;
        int minWidth = dpToPixel(minDayWidthDP, logicalDensity);
        int screenWidth = metrics.widthPixels;
        int dayWidth = screenWidth < minWidth ? minWidth: screenWidth - 24;

        for(int i=0; i<daySpinnerRelative.getChildCount(); i++) {
            View view = (View) daySpinnerRelative.getChildAt(i);
            if (view instanceof LinearLayout) {
                LinearLayout daySpinnerLayout = (LinearLayout) view;
                daySpinnerLayout.getLayoutParams().width = dayWidth;
                Log.w("PayCalc", String.format("Set view to width: %d.", daySpinnerLayout.getWidth()));
                Log.w("PayCalc", String.format("Screen Dimensions: %d x %d. DP: %d x %d", metrics.widthPixels, metrics.heightPixels,
                        pixelToDp(metrics.widthPixels, logicalDensity), pixelToDp(metrics.heightPixels, logicalDensity)));
            }
        }
    }

    static int dpToPixel(int dp, float logicalDensity){
        return (int) Math.ceil(dp * logicalDensity);
    }

    static int pixelToDp(int pixels, float logicalDensity){
        return (int) Math.ceil(pixels / logicalDensity);
    }

	private void setupSpinners() {
        // FindViewByID for spinners and add listeners
        for(SpinnerData spinnerData : SpinnerData.values()){
            spinnerData.setView(thisFragView);
            addListenerToSpinner(spinnerData.spinner);
        }

        // Add ArrayAdaptors for DaySpinners
        updateDaySpinners(prefs.getBoolean(getString(R.string.pref_custDaysOn), false));

        // Add Array Adaptors for Meal and LOA Bonuses (0...7)
        for(SpinnerData spinnerData : bonusSpinners){
            spinnerData.updateBonusAdaptor(context);
        }

        updateWageSpinner();

        addListenerToSpinner(SpinnerData.WAGE.spinner);

		updateCalcOutput();
	}

    private void addListenerToSpinner(final Spinner spinner){
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                spinnerItemSelection((Spinner) parent);
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    // Called by spinner itemSelectedListener
    public void spinnerItemSelection(Spinner spinner){
        SpinnerData spinnerData = SpinnerData.getDataBySpinner(spinner);
        if(spinnerData == null){
            Log.w("PaychequeFragment", "Spinner data returned null. Ignoring ItemSelection.");
            return;
        }
        if(spinnerData.selectionChanged()){
            spinnerData.getSelectedItem();
            updateCalcOutput();
            //Log.i("PaychequeFragment", "Spinner data changed... running calc.");
        } else {
            //Log.i("PayCheque Fragment", "Spinner data didn't change.");
        }
    }

	private void updateDaySpinners(Boolean customDaysOn){
		for(SpinnerData spinnerData : daySpinners){
            spinnerData.updateShiftAdaptor(customDaysOn, context);
        }
        customDay = customDaysOn; //Flags change handled for redo spinners
	}

	private void updateWageSpinner() {
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

		this.wageRates = taxManager.getWageRates(oldProvWage);

		String[] wageNames = taxManager.getWageNames(oldProvWage);
        ArrayAdapter<String> wageAdapt = new ArrayAdapter<String>(getActivity().getApplicationContext(),
                android.R.layout.simple_spinner_item, wageNames);

        SpinnerData.WAGE.spinner.setAdapter(wageAdapt);

        String[] mealSpinnerVals = prefs.getString(getString(R.string.pref_wageMealSpinners), "5,0,0").split(",");
        if(Integer.parseInt(mealSpinnerVals[0]) > wageNames.length - 1){
            int defaultWageIndex = AtaMathUtils.bracketInt((int)wageRates[wageRates.length - 1], 0, wageNames.length - 1);
            mealSpinnerVals[0] = Integer.toString(defaultWageIndex);
            String mealSpinnerString = String.format("%s,%s,%s", mealSpinnerVals[0], mealSpinnerVals[1], mealSpinnerVals[2]);
            prefs.edit().putString(getString(R.string.pref_wageMealSpinners), mealSpinnerString).apply();
        }
    }

    private void setShift(HoursPreset preset){
        for(SpinnerData spinnerData: daySpinners){
            spinnerData.setSelection(preset.getShiftIndex(), false);
        }
        SpinnerData.MEAL.setSelection(preset.getBonusCounts()[0], false);
        SpinnerData.LOA.setSelection(preset.getBonusCounts()[1], false);
    }

    // TODO: stretch this out a bit and use string references
    private static final String[] custDayKeys = {"custom_a", "custom_b", "custom_c"};
    private static final String[] custDaySuffix = {"_straight", "_overtime", "_double"};
    private static final String[] custDayNames = {"Day A", "Day B", "Day C"};
	private boolean verifyCustDays() {
        boolean verifyFlag = true;

		for(int i = 0; i < custDayKeys.length; i++) {
            for(int j=0; j< custDaySuffix.length; j++){
                double[] parsedDay = parseDay(prefs.getString(custDayKeys[i] + custDaySuffix[j], "0"));
                if(parsedDay[1] == 0){
                    verifyFlag = false;
                    SharedPreferences.Editor pEdit = prefs.edit();
                    pEdit.putString(custDayKeys[i] + custDaySuffix[j], "0").apply();
                    Toast.makeText(context, custDayNames[i] + custDaySuffix[j].replace("_", " ") +
                            " was invalid... resetting",Toast.LENGTH_SHORT).show();
                }
            }
		}
        return verifyFlag;
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

        int mealCount = Integer.parseInt(SpinnerData.MEAL.getSelectedItem());
        int loaCount = Integer.parseInt(SpinnerData.LOA.getSelectedItem());
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


        boolean[] weekHolidays = {true, monHol.isChecked(), tueHol.isChecked(), wedHol.isChecked(),
                thuHol.isChecked(),friHol.isChecked(), true};  //sat and sun count as holidays
        for(int i=0; i<daySpinners.length; i++){
            float[] splitArr;
            String itemStr = (daySpinners[i].getSelectedItem());
            if(itemStr.contains("A") || itemStr.contains("B") || itemStr.contains("C")){
                splitArr = getCustomDayPrefs(itemStr);
            } else {
                String selectedShift = daySpinners[i].getSelectedItem();
                splitArr = new float[]{Float.parseFloat(selectedShift)};
            }
            // Sends to add hours as weekend if double time checked
            int dayIndex = weekHolidays[i] ? 0 : i;

            payCalcData.addHours(splitArr, fourToggle.isChecked(), nightToggle.isChecked(), dayIndex, nightOT, doubleOT);
        }

        float wageRate;
        if(SpinnerData.WAGE.getSelectedItem().contains("Custom")) {
            wageRate = checkPref(getString(R.string.pref_custom_wage), 20, "Custom Wage");
        } else {
            wageRate = (float) wageRates[SpinnerData.WAGE.spinner.getSelectedItemPosition()];
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

        wageRateVal.setText(String.format("Wage: $%.2f", wageRate));
        vacationVal.setText(String.format("Vac\\Hol (%.2f%%): $%.2f", vacRate * 100d, earnings.vacationBonus));
        grossVal.setText(String.format("Gross: $%.2f", earnings.getGross()));
        exemptVal.setText(String.format("Tax Exempt: $%.2f", earnings.getExempt()));
        cppToggle.setText(String.format("EI/CPP: $%.2f + $%.2f", deductions.ei, deductions.cpp));
		fieldDuesToggle.setText(String.format("Work Dues (%.2f%%): $%.2f", fieldDuesRate * 100, fieldDues));
		monthlyDuesToggle.setText(String.format("Monthly Dues: $%.2f", deductions.monthlyDues));
		dedVal.setText(String.format("Deductions: $%.2f", deductionsSum));
		netVal.setText(String.format("Takehome: $%.2f", netPay));
		sTimeText.setText("1.0x: " + twoPrecision(hoursSum[0]));
		hTimeText.setText("1.5x: " + twoPrecision(hoursSum[1]));
		dTimeText.setText("2.0x: " + twoPrecision(hoursSum[2]));

		if(addTax == 0) {
			taxToggle.setText(String.format("Tax: $%.2f", deductions.getTaxes()));
		} else {
			taxToggle.setText(String.format("Tax: $%.2f + $%.2f", deductions.getTaxes(), addTax));
		}
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

    //Groups like views and serializes to CSV strings
    private void saveState(){
        CompoundButton[] saveChecks = {monHol, tueHol, wedHol, thuHol, friHol,  //Checkboxes
                taxToggle, cppToggle, fieldDuesToggle, monthlyDuesToggle,
                fourToggle, nightToggle, travelToggle, dayTravelToggle};
        if(prefs == null || SpinnerData.WAGE.spinner == null) return;

        StringBuilder weekStringBuild = new StringBuilder();
        for(int i=0; i<daySpinners.length; i++){
            if(daySpinners[i].spinner==null) {
                return;
            }
            weekStringBuild.append(Integer.toString(daySpinners[i].spinner.getSelectedItemPosition())).append(",");
        }
        StringBuilder holidayStringBuild = new StringBuilder();
        for(int i=0; i<saveChecks.length; i++){
            int boolInt = (saveChecks[i].isChecked()) ? 1 : 0;
            holidayStringBuild.append(boolInt).append(",");
        }
        int[] wageMealIndices = {SpinnerData.WAGE.spinner.getSelectedItemPosition(),
                SpinnerData.MEAL.spinner.getSelectedItemPosition(),
                SpinnerData.LOA.spinner.getSelectedItemPosition()};

        SharedPreferences.Editor prefEdit = prefs.edit();
        prefEdit.putString("payCalc_weekSpinners", weekStringBuild.toString());
        prefEdit.putString("payCalc_saveChecks", holidayStringBuild.toString());
        prefEdit.putString("payCalc_wageMealSpinners", String.format("%d,%d,%d", wageMealIndices[0], wageMealIndices[1], wageMealIndices[2]));

        //prefEdit.putStringSet();
        prefEdit.apply();
    }

    // TODO: extract keys
    private void loadState(){
        CompoundButton[] saveChecks = {monHol, tueHol, wedHol, thuHol, friHol,  //Checkboxes
                taxToggle, cppToggle, fieldDuesToggle, monthlyDuesToggle,
                fourToggle, nightToggle, travelToggle, dayTravelToggle};
        if(prefs == null) return;

        String[] weekSpinnerIndices = prefs.getString("payCalc_weekSpinners", "0,0,0,0,0,0,0,").split(",");
        for(int i=0; i<daySpinners.length; i++){
            if(daySpinners[i].spinner == null) return;
            daySpinners[i].setSelection(AtaMathUtils.bracketInt(weekSpinnerIndices[i], 0, 6), false);
        }
        String[] saveChecksIndices = prefs.getString("payCalc_saveChecks", "0,0,0,0,0,1,1,1,0,0,0,0,0,").split(",");
        for(int i=0; i<saveChecks.length; i++){
            saveChecks[i].setChecked(saveChecksIndices[i].equals("1"));
        }
        String[] wageMealIndices = prefs.getString("payCalc_wageMealSpinners", "5,0,0,").split(",");
        //Incase number of items in wage spinner has changed with province between saves
        SpinnerData.WAGE.setSelection(AtaMathUtils.bracketInt(wageMealIndices[0], 0, wageRates.length - 1), false);
        SpinnerData.MEAL.setSelection(AtaMathUtils.bracketInt(wageMealIndices[1], 0, 6), false);
        SpinnerData.LOA.setSelection(AtaMathUtils.bracketInt(wageMealIndices[2], 0, 6), false);

        if(prefs.getBoolean(getString(R.string.pref_custDaysOn), false)){
            verifyCustDays();
        }
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
