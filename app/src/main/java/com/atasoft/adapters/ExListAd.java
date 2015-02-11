package com.atasoft.adapters;

import android.app.*;
import android.content.*;
import android.graphics.drawable.*;
import android.net.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.atasoft.flangeassist.*;

public class ExListAd extends BaseExpandableListAdapter {

	private final SparseArray<ExpandableGroup> groups;
	private LayoutInflater inflater;
	private Activity activity;

	public ExListAd(Activity act, SparseArray<ExpandableGroup> groups) {
		activity = act;
		this.groups = groups;
		inflater = act.getLayoutInflater();
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return groups.get(groupPosition).children.get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public View getChildView(int groupPosition, final int childPosition,
							 boolean isLastChild, View convertView, ViewGroup parent) {
		final String children = (String) getChild(groupPosition, childPosition);
		TextView text;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.listrow_details, null);
		}
		text = (TextView) convertView.findViewById(R.id.textView1);
		
		
		convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					runLink(children);
					
				}
			});
			
		text = makeOption(children, text);
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return groups.get(groupPosition).children.size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return groups.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return groups.size();
	}

	@Override
	public void onGroupCollapsed(int groupPosition) {
		super.onGroupCollapsed(groupPosition);
	}

	@Override
	public void onGroupExpanded(int groupPosition) {
		super.onGroupExpanded(groupPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
							 View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.listrow_group, null);
		}
		ExpandableGroup group = (ExpandableGroup) getGroup(groupPosition);
		((CheckedTextView) convertView).setText("  " + group.string); // Don't judge me.
		((CheckedTextView) convertView).setChecked(isExpanded);
		convertView = setLocalDraw((CheckedTextView) convertView, group.string);
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}
	
	private int linkType(String children) {
		boolean isCallout = false;
		if(children.startsWith("call_")) {
			isCallout = true;
			children = children.split("_")[1];
		}
		if(children.startsWith("(")) {
			if(isCallout) {
				return 0;
			} else {
				return 1;
			}
		}
		if(children.startsWith("http") && isCallout) {
			return 2;   
		}
		return 3;
	}
	
	private void runLink (String inString) {
		int type = linkType(inString);
		Intent intent;
		String uri;
		if (inString.startsWith("call")) inString = inString.split("_")[1];
		
		uri = inString;
		intent = new Intent(Intent.ACTION_VIEW);
	    
		if (type == 0 || type == 1) {	
			uri = "tel:" + inString ;
			intent = new Intent(Intent.ACTION_DIAL);
		}
			
		intent.setData(Uri.parse(uri));
		activity.startActivity(intent);
    }
	
	private TextView makeOption(String children, TextView text) {
		Drawable catIcon;
		
		switch (linkType(children)) {
			case 0:
			    text.setText("Phone Callout");
				catIcon = activity.getResources().getDrawable(R.drawable.phoneicon);  // placeholder
				break;
			case 1:
			    text.setText("Phone Office");
				catIcon = activity.getResources().getDrawable(R.drawable.phoneicon);  // placeholder
				break;
			case 2:
			    text.setText("Browse Callout");
				catIcon = activity.getResources().getDrawable(R.drawable.linkicon);  // placeholder
				break;
			default:
			    text.setText("Browse Homepage");
				catIcon = activity.getResources().getDrawable(R.drawable.linkicon);  // placeholder
				break;
		}
		text.setCompoundDrawablesWithIntrinsicBounds(catIcon, null, null, null);
		return text;
	}
	
	private CheckedTextView setLocalDraw(CheckedTextView textViewIn, String groupString) {
		groupString = "logo" + (groupString.substring(0, 3)).trim();
		Context context = textViewIn.getContext();
		int id = context.getResources().getIdentifier(groupString, "drawable", context.getPackageName());
		Drawable logo = activity.getResources().getDrawable(id);
		textViewIn.setCompoundDrawablesWithIntrinsicBounds(logo, null, null, null);
		return textViewIn;
	}
} 
