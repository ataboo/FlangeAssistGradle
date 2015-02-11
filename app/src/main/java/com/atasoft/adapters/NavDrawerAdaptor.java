package com.atasoft.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.atasoft.flangeassist.fragments.CashCounter;
import com.atasoft.flangeassist.fragments.FlangeFragment;
import com.atasoft.flangeassist.fragments.NozzleCalc;
import com.atasoft.flangeassist.fragments.RigTrig;
import com.atasoft.flangeassist.fragments.ShapeCalcFrag;
import com.atasoft.flangeassist.fragments.TorqueFragment;
import com.atasoft.flangeassist.fragments.UnitConFragment;
import com.atasoft.flangeassist.fragments.WageCPIEstimate;
import com.atasoft.flangeassist.fragments.WeldingFrag;


public class NavDrawerAdaptor {
	public static final String[] TABS = {"Flange\nTables", "Torque\nPattern", "CPI Raise\nEstimator", "Unit\nConverter", "Shape\nCalculator", "Welding\nReference","Cash\nCounter", "Rigging\nCalculator", "Nozzle\nCalculator"};
	private static int TAB_COUNT = TABS.length;

    public static Fragment getItem(int index) {

        switch (index) {
			case 0:
				// Flange Tables
				return new FlangeFragment();
			case 1:
				// Torque Pattern
				return new TorqueFragment();
			case 2:
				// CPI Raise Estimator
				return new WageCPIEstimate();
			case 3:
				// Unit Converter
				return new UnitConFragment();
			case 4:
				// Shape Calculator
				return new ShapeCalcFrag();
			case 5:
				// Welding Reference
				return new WeldingFrag();
			case 6:
				// Cash Counter
				return new CashCounter();
			case 7:
				// Rigging Calculator
				return new RigTrig();
			case 8:
				// Nozzle Calculator
				return new NozzleCalc();
		}

        return null;
    }

    public int getCount() {
        return TAB_COUNT;
    }
}
