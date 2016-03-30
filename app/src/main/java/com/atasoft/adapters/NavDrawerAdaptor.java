package com.atasoft.adapters;

import android.support.v4.app.Fragment;


import com.atasoft.flangeassist.fragments.*;
import com.atasoft.flangeassist.fragments.cashcounter.CashCounter;


public class NavDrawerAdaptor {
	public static final String[] TABS = {"Paycheque Calculator", "Flange Tables", "Torque Pattern",
            "CPI Raise Estimator", "Unit Converter", "Shape Calculator", "Welding Reference",
            "Cash Counter", "Rigging Calculator", "Nozzle Calculator", "Hall Links", "Boilermaker Reporter", "Gross Tax Estimator"};
	private static int TAB_COUNT = TABS.length;

    public static Fragment getItem(int index) {

        switch (index) {
            case 0:
                // Flange Tables
                return new PaychequeFragment();
			case 1:
				// Flange Tables
				return new FlangeFragment();
			case 2:
				// Torque Pattern
				return new TorqueFragment();
			case 3:
				// CPI Raise Estimator
				return new WageCPIEstimate();
			case 4:
				// Unit Converter
				return new UnitConFragment();
			case 5:
				// Shape Calculator
				return new ShapeCalcFrag();
			case 6:
				// Welding Reference
				return new WeldingFrag();
			case 7:
				// Cash Counter
				return new CashCounter();
			case 8:
				// Rigging Calculator
				return new RigTrig();
			case 9:
				// Nozzle Calculator
				return new NozzleCalc();
            case 10:
                //Hall Links
                return new HallFragment();
            case 11:
                //Boilermaker Reporter
                return new ReporterFragment();
            case 12:
                //Gross Tax
                return new TaxGrossFragment();
		}

        return null;
    }

    public int getCount() {
        return TAB_COUNT;
    }
}
