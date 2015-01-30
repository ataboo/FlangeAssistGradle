package com.atasoft.adapters;

import java.util.ArrayList;
import java.util.List;

public class ExpandableGroup {

	public String string;
	public final List<String> children = new ArrayList<String>();

	public ExpandableGroup(String string) {
		this.string = string;
	}

} 
