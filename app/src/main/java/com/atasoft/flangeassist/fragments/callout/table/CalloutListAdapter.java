package com.atasoft.flangeassist.fragments.callout.table;

import android.app.Activity;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;

import com.atasoft.flangeassist.fragments.callout.daters.CalloutJob;

/**
 * Created by ataboo on 4/15/2017.
 */

public class CalloutListAdapter extends BaseExpandableListAdapter {
    public Activity activity;
    public LayoutInflater inflater;
    private final SparseArray<CalloutJob> jobs;

    public CalloutListAdapter(Activity activity, SparseArray<CalloutJob> jobs) {
        this.activity = activity;
        this.jobs = jobs;
        this.inflater = activity.getLayoutInflater();
    }

    @Override
    public int getGroupCount() {
        return jobs.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return jobs.get(groupPosition).getDetailRowCount();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return jobs.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return jobs.get(groupPosition).getDetailRow(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public int getChildType(int groupPosition, int childPosition) {
        return ((DetailRow)getChild(groupPosition, childPosition)).type.ordinal();
    }

    @Override
    public int getChildTypeCount() {
        return DetailRow.Type.values().length;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        return jobs.get(groupPosition).getGroupRow().render(inflater, convertView);
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        return jobs.get(groupPosition).getDetailRow(childPosition).render(inflater, convertView);
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
