package com.atasoft.adapters;

import com.atasoft.flangeassist.fragments.*;

//TODO: v13 once AIDE supports it
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsPagerAdapter extends FragmentPagerAdapter {
	public static final String[] TABS = {"Flange\nTables", "Torque\nPattern", "CPI Raise\nEstimator", "Unit\nConverter", "Shape\nCalculator", "Welding\nReference","Cash\nCounter", "Rigging\nCalculator", "Nozzle\nCalculator"};
	public static int TAB_COUNT = TABS.length;
	
    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

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

    @Override
    public int getCount() {
        return TAB_COUNT;
    }
}
