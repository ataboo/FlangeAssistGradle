package com.atasoft.shared;

import android.support.v4.app.Fragment;


import com.atasoft.flangeassist.fragments.callout.CalloutFragment;
import com.atasoft.flangeassist.fragments.cashcounter.CashCounter;
import com.atasoft.flangeassist.fragments.paycalc.PaycalcFragment;
import com.atasoft.flangeassist.fragments.*;
import com.atasoft.flangeassist.fragments.ropevalues.RopeFragment;


public class NavDrawerAdaptor {
	public static final String[] TABS = {"Paycheque Calculator", "Flange Tables", "146 Callout", "Torque Pattern",
            "CPI Raise Estimator", "Rope Values", "Unit Converter", "Shape Calculator", "Welding Reference",
            "Cash Counter", "Rigging Calculator", "Nozzle Calculator", "Hall Links", "Boilermaker Reporter", "Gross Tax Estimator"};
	private static int TAB_COUNT = TABS.length;

    public static Fragment getItem(int index) {

        switch (index) {
            case 0:
                return new PaycalcFragment();
			case 1:
				return new FlangeFragment();
			case 2:
				return new CalloutFragment();
			case 3:
				return new TorqueFragment();
			case 4:
				return new WageCPIEstimate();
			case 5:
				return new RopeFragment();
			case 6:
				return new UnitConFragment();
			case 7:
				return new ShapeCalcFrag();
			case 8:
				return new WeldingFrag();
			case 9:
				return new CashCounter();
			case 10:
				return new RigTrig();
			case 11:
				return new NozzleCalc();
            case 12:
                return new HallFragment();
            case 13:
                return new ReporterFragment();
            case 14:
                return new TaxGrossFragment();
		}

        return null;
    }

    public int getCount() {
        return TAB_COUNT;
    }
}
