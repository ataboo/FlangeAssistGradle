package com.atasoft.flangeassist;

import android.annotation.*;
import android.support.v4.app.*;
import android.app.ActionBar;
import android.os.*;
import com.atasoft.flangeassist.fragments.*;

public class FragFramer extends FragmentActivity {
	public static final int PAY_CALC = 0;
	public static final int HALL = 1;
	public static final int ABOUT = 2;
	private ActionBar actionBar;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragframer);	
		actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
		
		//int fragCode = getIntent().getIntExtra("launch_frag", 0);
		int fragCode  = getIntent().getIntExtra("launch_frag", 0);
		launchFrag(fragCode);
		
	
		
	}
	
	private void launchFrag(int fragInt){
		Fragment frag;
		String name;
		switch (fragInt){
		case PAY_CALC:
			frag = new PaychequeFragment();
			name = PaychequeFragment.NAME;
			break;
		case HALL:
			frag = new HallFragment();
			name = HallFragment.NAME;
			break;
		default:
			frag = new AboutFragment();
			name = AboutFragment.NAME;
			break;
		}
		if(getFragmentManager().findFragmentByTag(name) == null){
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			setTitle(name);
			transaction.add(R.id.fragframe, frag, name);
			transaction.commit();
		}
	}
}
