package com.atasoft.shared;

import androidx.fragment.app.Fragment;
import com.atasoft.flangeassist.fragments.cashcounter.CashCounter;
import com.atasoft.flangeassist.fragments.paycalc.PaycalcFragment;
import com.atasoft.flangeassist.fragments.*;
import com.atasoft.flangeassist.fragments.ropevalues.RopeFragment;

public class NavDrawerAdaptor {
	public static final NavDrawerItem[] DRAWER_ITEMS = {
		new NavDrawerItem("Paycheque Calculator", new PaycalcFragment()),
			new NavDrawerItem("Flange Tables", new PaycalcFragment()),
			new NavDrawerItem("Torque Pattern", new FlangeFragment()),
			new NavDrawerItem( "CPI Raise Estimator", new WageCPIEstimate()),
			new NavDrawerItem("Rope Values", new RopeFragment()),
			new NavDrawerItem("Unit Converter", new UnitConFragment()),
			new NavDrawerItem("Shape Calculator", new ShapeCalcFrag()),
			new NavDrawerItem("Welding Reference", new WeldingFrag()),
			new NavDrawerItem("Cash Counter", new CashCounter()),
			new NavDrawerItem("Rigging Calculator", new RigTrig()),
			new NavDrawerItem("Nozzle Calculator", new NozzleCalc()),
			new NavDrawerItem("Hall Links", new HallFragment()),
			new NavDrawerItem("Gross Tax Estimator", new TaxGrossFragment()),
	};

	public static int tabCount() {
		return DRAWER_ITEMS.length;
	}

	public static Fragment getFragmentAtIndex(int index) {
		return DRAWER_ITEMS[index].fragment;
	}

	public static String[] getAllDrawerNames() {
		String[] names = new String[DRAWER_ITEMS.length];
		for(int i=0; i<names.length; i++) {
			names[i] = DRAWER_ITEMS[i].description;
		}

		return names;
	}
}
