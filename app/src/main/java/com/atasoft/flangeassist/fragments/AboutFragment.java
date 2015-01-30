package com.atasoft.flangeassist.fragments;

import android.os.*;
import android.app.*;
import android.view.*;
import com.atasoft.flangeassist.*;


public class AboutFragment extends Fragment {

	public static final String NAME = "About the Toolbox";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
						Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.about, container, false);
		return rootView;
	}
}

