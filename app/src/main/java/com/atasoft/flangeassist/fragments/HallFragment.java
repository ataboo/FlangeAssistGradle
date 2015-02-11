package com.atasoft.flangeassist.fragments;

import android.os.*;
import android.support.v4.app.Fragment;
import android.util.*;
import android.view.*;
import android.widget.*;

import com.atasoft.adapters.*;
import com.atasoft.flangeassist.*;

public class HallFragment extends Fragment {
	
	public static final String NAME = "Hall Contacts";
    private View thisFrag;
	private SparseArray<ExpandableGroup> groups = new SparseArray<ExpandableGroup>();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
	View v = inflater.inflate(R.layout.halllinks, container, false);
    thisFrag = v;

	setLinks();
	ExpandableListView listView = (ExpandableListView) thisFrag.findViewById(R.id.listView);
	ExListAd adapter = new ExListAd(getActivity(),
										groups);
	listView.setAdapter(adapter);
	
    return v;					 
	}
	
	private void setLinks() {
		String[] rawArr = getResources().getStringArray(R.array.locals);
		String[] rawSplit;
		
		for(int i = 0; i < rawArr.length; i++) {
			// Display, Home, Phone, Callout
			rawSplit = rawArr[i].split(",");
			ExpandableGroup group = new ExpandableGroup(rawSplit[0]);
			
			for(int j = 1; j < rawSplit.length; j++) {
				group.children.add(suffixAdd(rawSplit[j]));
			}
			groups.append(i, group);
		}
	}
	String suffixAdd(String inString) {	
		return inString;
	}
	
} 


