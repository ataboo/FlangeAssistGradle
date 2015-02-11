 package com.atasoft.flangeassist.fragments;


import android.content.*;
import android.os.*;
import android.preference.*;
import android.support.v4.app.Fragment;
import android.view.*;
import android.view.View.*;
import android.widget.*;

import com.atasoft.flangeassist.*;
import com.atasoft.helpers.*;

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
	private double vacationPay;
    private View thisFrag;
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
	
	private SharedPreferences prefs;
	private Context context;
	private Boolean customDay;
	private String oldProvWage;
	private TaxManager taxManager;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.paycalc, container, false);
        thisFrag = v;
		context = getActivity().getApplicationContext();
		this.taxManager = new TaxManager();

        Button bClr = (Button) v.findViewById(R.id.clr_but);
        Button bTens = (Button) v.findViewById(R.id.tens_but);
		Button bTwelves = (Button) v.findViewById(R.id.twelves_but);
		Button bFour = (Button) v.findViewById(R.id.four_but);
		Button bNight = (Button) v.findViewById(R.id.night_but);
		Button bTravel = (Button) v.findViewById(R.id.travel_but);
		Button bDayTravel = (Button) v.findViewById(R.id.travelday_but);
		taxVal = (CheckBox) v.findViewById(R.id.tax_val);
		cppVal = (CheckBox) v.findViewById(R.id.cpp_val);
		duesVal = (CheckBox) v.findViewById(R.id.dues_val);
		monthlyDuesVal = (CheckBox) v.findViewById(R.id.monthlydues_val);
		taxVal.setChecked(true);
		cppVal.setChecked(true);
		duesVal.setChecked(true);
		
		monHol = (CheckBox) v.findViewById(R.id.hol_mon);
		tueHol = (CheckBox) v.findViewById(R.id.hol_tue);
		wedHol = (CheckBox) v.findViewById(R.id.hol_wed);
		thuHol = (CheckBox) v.findViewById(R.id.hol_thu);
		friHol = (CheckBox) v.findViewById(R.id.hol_fri);
		
		
		
		bClr.setOnClickListener(this);
		bTens.setOnClickListener(this);
		bTwelves.setOnClickListener(this);
		bFour.setOnClickListener(this);
		bNight.setOnClickListener(this);
		bTravel.setOnClickListener(this);
		bDayTravel.setOnClickListener(this);
		taxVal.setOnClickListener(this);
		cppVal.setOnClickListener(this);
		duesVal.setOnClickListener(this);
		monthlyDuesVal.setOnClickListener(this);
		monHol.setOnClickListener(this);
		tueHol.setOnClickListener(this);
		wedHol.setOnClickListener(this);
		thuHol.setOnClickListener(this);
		friHol.setOnClickListener(this);
	
		prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
		
		setupSpinners();
        return v;
    }
	
	@Override
	public void onResume() {
		redoSpinners();
		
		super.onResume();
    }
	
	private void redoSpinners(){
		Boolean custDayCheck = prefs.getBoolean("custom_daycheck", false);
		if(custDayCheck) {
		    if(customDay != custDayCheck || !verifyCustDays()) updateDaySpinners(custDayCheck);
		}
		String provWage = prefs.getString("list_provwage", "AB");
		if(!provWage.equals(oldProvWage)) setupWageSpinner(provWage);
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
	private void setupSpinners() {
        sTimeText = (TextView) thisFrag.findViewById(R.id.sing_val);
		hTimeText = (TextView) thisFrag.findViewById(R.id.half_val);
		dTimeText = (TextView) thisFrag.findViewById(R.id.doub_val);
		wageRateVal = (TextView) thisFrag.findViewById(R.id.wageRate_val);
		vacationVal = (TextView) thisFrag.findViewById(R.id.vacation_val);
		grossVal = (TextView) thisFrag.findViewById(R.id.gross_val);
		exemptVal = (TextView) thisFrag.findViewById(R.id.exempt_val);
		dedVal = (TextView) thisFrag.findViewById(R.id.deduct_val);
		netVal = (TextView) thisFrag.findViewById(R.id.net_val);
		fourToggle = (ToggleButton) thisFrag.findViewById(R.id.four_but);
		nightToggle = (ToggleButton) thisFrag.findViewById(R.id.night_but);
		travelToggle = (ToggleButton) thisFrag.findViewById(R.id.travel_but);
		dayTravelToggle = (ToggleButton) thisFrag.findViewById(R.id.travelday_but);
		
		sunSpin = (Spinner) thisFrag.findViewById(R.id.sunSpin);
		monSpin = (Spinner) thisFrag.findViewById(R.id.monSpin);
		tueSpin = (Spinner) thisFrag.findViewById(R.id.tueSpin);
		wedSpin = (Spinner) thisFrag.findViewById(R.id.wedSpin);
		thuSpin = (Spinner) thisFrag.findViewById(R.id.thuSpin);
		friSpin = (Spinner) thisFrag.findViewById(R.id.friSpin);
		satSpin = (Spinner) thisFrag.findViewById(R.id.satSpin);

		mealSpin = (Spinner) thisFrag.findViewById(R.id.meals_spin);
		loaSpin = (Spinner) thisFrag.findViewById(R.id.loa_spin);
		wageSpin = (Spinner) thisFrag.findViewById(R.id.wageSpin);

		updateDaySpinners(prefs.getBoolean("custom_daycheck",false));

        ArrayAdapter<String> weekCount = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_item, 
																  new String[]{"0","1","2","3","4","5","6","7"});
        loaSpin.setAdapter(weekCount);
        mealSpin.setAdapter(weekCount);
		
		oldProvWage = prefs.getString("list_provwage", "AB");
		setupWageSpinner(oldProvWage);
		
		sunSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> parent, View view,
										   int pos, long id) {
					pushBootan();
				}
				public void onNothingSelected(AdapterView<?> parent) {
				}
			});
		monSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> parent, View view,
										   int pos, long id) {
					pushBootan();
				}
				public void onNothingSelected(AdapterView<?> parent) {
				}
			});
		tueSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> parent, View view,
										   int pos, long id) {
					pushBootan();
				}
				public void onNothingSelected(AdapterView<?> parent) {
				}
			});
		wedSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> parent, View view,
										   int pos, long id) {
					pushBootan();
				}
				public void onNothingSelected(AdapterView<?> parent) {
				}
			});
		thuSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> parent, View view,
										   int pos, long id) {
					pushBootan();
				}
				public void onNothingSelected(AdapterView<?> parent) {
				}
			});
		friSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> parent, View view,
										   int pos, long id) {
					pushBootan();
				}
				public void onNothingSelected(AdapterView<?> parent) {
				}
			});
		satSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> parent, View view,
										   int pos, long id) {
					pushBootan();
				}
				public void onNothingSelected(AdapterView<?> parent) {
				}
			});
		loaSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> parent, View view,
										   int pos, long id) {
					pushBootan();
				}
				public void onNothingSelected(AdapterView<?> parent) {
				}
			});
		mealSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> parent, View view,
										   int pos, long id) {
					pushBootan();
				}
				public void onNothingSelected(AdapterView<?> parent) {
				}
			});
		wageSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> parent, View view,
										   int pos, long id) {
					pushBootan();
				}
				public void onNothingSelected(AdapterView<?> parent) {
				}
			});
		
		pushBootan();
	}
	
	private void updateDaySpinners(Boolean custDayCheck){
		String[] workHrs;
		
		customDay = custDayCheck;
		if(customDay && verifyCustDays()) {
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
	
	private void setupWageSpinner(String provWage) {
		int prov = TaxManager.PROV_AB;
		if(provWage.contains("BC")) prov = TaxManager.PROV_BC;
		if(provWage.contains("ON")) prov = TaxManager.PROV_ON;
		this.vacationPay = taxManager.getVacationRate(prov);
		this.wageRates = taxManager.getWageRates(prov);
		
		String[] wageNames = taxManager.getWageNames(prov);
		ArrayAdapter<String> wageAdapt = new ArrayAdapter<String>(getActivity().getApplicationContext(), 
			android.R.layout.simple_spinner_item, wageNames);
		wageSpin.setAdapter(wageAdapt);
		wageSpin.setSelection((int) wageRates[wageRates.length - 1]);
		oldProvWage = provWage;
    }
	
	private Boolean verifyCustDays() {
		String[] dayKeys = {"custom_dayA", "custom_dayB", "custom_dayC"};
		String[] dayNames = {" Day A", " Day B", " Day C"};
		String[] dayBad = {"", "", ""};
		int errCount = 0;
		String toastStr = "The custom schedule";
		for(int i = 0; i < dayKeys.length; i++) {   
			if(!verifyDay(prefs.getString(dayKeys[i], ""))){
				dayBad[errCount] = dayNames[i];
				errCount++;
			}
		}
		switch (errCount) {
		    case 0:
		        return true;
			case 1:
			    toastStr = toastStr + dayBad[0] +
				    " was ";
				break;
			case 2:
			    toastStr = toastStr + "s" + dayBad[0] + " and" +
				    dayBad[1] + " were ";
				break;
			case 3:
			    toastStr = toastStr + "s" + dayBad[0] + "," +
				    dayBad[1] + ", and" + 
					dayBad[2] + " were ";
				break;
		}
		
		toastStr = toastStr + "not entered properly. Please format as (1x,1.5x,2x) ex. \"8.5,2,1.5\"";
		Toast.makeText(context, toastStr, Toast.LENGTH_LONG).show();
		return false;
	}
	
	private Boolean verifyDay(String testStr) {
		String[] splitStr = testStr.split(",");
		double[] strParse = new double[3];
		
		if(splitStr.length != 3) return false;
		for(int i = 0; i < 3; i++) {
			try{
				strParse[i] = Double.parseDouble(splitStr[i]);
			}
			catch(NumberFormatException e) {
				return false;
			}		
			if(strParse[i] < 0) return false;
		}

        return strParse[0] + strParse[1] + strParse[2] <= 24;
    }

	private void pushBootan() {
		
		double splitArr[] = new double[3];
		boolean fourTens = fourToggle.isChecked();
		
		//init with preferences now
		//double loaRate = Double.parseDouble(getString(R.string.loa_rate));
		//double mealRate = Double.parseDouble(getString(R.string.meal_rate));
		//double vacationPay = Double.parseDouble(getString(R.string.vacation_pay));
		//double travelRate = Double.parseDouble(getString(R.string.travel_rate));
		int timeSum[] = {0,0,0};

		int loaCount = Integer.parseInt(loaSpin.getSelectedItem().toString());
		int mealCount = Integer.parseInt(mealSpin.getSelectedItem().toString());
		double wageRate;
		boolean[] weekHolidays = {true, monHol.isChecked(), tueHol.isChecked(), wedHol.isChecked(),thuHol.isChecked(),friHol.isChecked(), true};  //sat and sun count as holidays
		double addTax = checkPrefDoub("custom_addtax", "0", "Addtax Rate");
		double mealRate = checkPrefDoub("custom_mealrate", "40", "Meal Rate");
		double weekTravel = checkPrefDoub("custom_weektravel", "216", "Weekly Travel Rate");
		double dayTravel = checkPrefDoub("custom_daytravel", "20", "Daily Travel");
		double loaRate = checkPrefDoub("custom_loa", "195", "LOA Rate");
		double monthlyDues = checkPrefDoub("custom_monthly_dues", "37.90", "Monthly Dues");
		double workingDuesRate = checkPrefDoub("custom_working_dues", ".0375", "Working Dues");
		
		if(wageSpin.getSelectedItem().toString().contains("Custom")) {
		    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

            float wageFloat = AtaMathUtils.bracketFloat(prefs.getString("custom wage", "20"), 0f, 1000000000f);
			wageRate = (double) wageFloat;
		} else {
			wageRate = wageRates[wageSpin.getSelectedItemPosition()];
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

		double grossVac = grossPay * (vacationPay + 1);
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
		
		String yearString = prefs.getString("list_taxYear", "2014");
		String provString = prefs.getString("list_provwage", "AB");

        //get strings out of the year prefs. array adapter?
		int taxYear = TaxManager.TY_2015;
		int taxProv = TaxManager.PROV_AB;
		
		if(yearString.contains("2013")) taxYear = TaxManager.TY_2013;
        if(yearString.contains("2014")) taxYear = TaxManager.TY_2014;
		
		if(provString.contains("BC")) taxProv = TaxManager.PROV_BC;
		if(provString.contains("ON")) taxProv = TaxManager.PROV_ON;
		//other provinces
		
		double[] taxReturns = taxManager.getTaxes(grossVac, taxYear, taxProv);
		//double[] taxReturns = taxManager.getTaxes(1020, taxYear, taxProv);
		
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
		vacationVal.setText(String.format("Vac\\Hol (%.0f%%): %.2f$", vacationPay * 100, grossVac - grossPay));
		grossVal.setText("Gross: " + String.format("%.2f", grossVac + exempt) + "$");
		exemptVal.setText("Tax Exempt: " + String.format("%.2f", exempt) + "$");
		cppVal.setText(String.format("EI/CPP: %.2f$ + %.2f$", deductions[3], deductions[2]));
		duesVal.setText(String.format("Work Dues (%.2f%%): %.2f$", workingDuesRate * 100, deductions[4]));
		monthlyDuesVal.setText("Monthly Dues: " + String.format("%.2f", deductions[5]) + "$");
		dedVal.setText("Deductions: " + String.format("%.2f", deductionsSum) + "$");
		netVal.setText("Takehome: " + String.format("%.2f", netPay) + "$");
		sTimeText.setText("1.0x: " + Integer.toString(timeSum[0]));
		hTimeText.setText("1.5x: " + Integer.toString(timeSum[1]));
		dTimeText.setText("2.0x: " + Integer.toString(timeSum[2]));
		
		if(addTax == 0) {
			taxVal.setText("Tax: " + String.format("%.2f", deductions[0] + deductions[1]) + "$");
		} else {
			taxVal.setText("Tax: " + String.format("%.2f", deductions[0] + deductions[1] - addTax) + "$ + " +
				String.format("%.2f", addTax) + "$");
		}
	}
	
	//unnecessary double
	private double checkPrefDoub(String prefKey, String defaultVal, String toastName) {
		double retVal = 0d;
		String prefString = prefs.getString(prefKey, defaultVal);
		try {
			retVal = Double.parseDouble(prefString);
		}
		catch (NumberFormatException e) {
			setPrefDefault(prefKey, defaultVal);
			Toast.makeText(context, toastName + " wasn't a number.", Toast.LENGTH_SHORT).show();
			return Double.parseDouble(defaultVal);
		}
		if(retVal > 100000 || retVal < 0) {
			setPrefDefault(prefKey, defaultVal);
			Toast.makeText(context, toastName + " was out of range.", Toast.LENGTH_SHORT).show();
			return Double.parseDouble(defaultVal);
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
		String[] splitPref = new String[3];
		double[] retDoub = new double[3];
		
		if(itemStr.contains("A")) splitPref = prefs.getString("custom_dayA", "0,0,0").split(",");
		if(itemStr.contains("B")) splitPref = prefs.getString("custom_dayB", "0,0,0").split(",");
		if(itemStr.contains("C")) splitPref = prefs.getString("custom_dayC", "0,0,0").split(",");
		
		for(int i = 0; i < splitPref.length; i++) {
			retDoub[i] = Double.parseDouble(splitPref[i]);
			
		}
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
}
