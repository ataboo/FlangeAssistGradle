package com.atasoft.flangeassist.fragments.callout;


import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.atasoft.flangeassist.CustomMenuLayout;
import com.atasoft.flangeassist.R;
import com.atasoft.flangeassist.fragments.callout.daters.CalloutJob;
import com.atasoft.flangeassist.fragments.callout.daters.CalloutResponse;
import com.atasoft.flangeassist.fragments.callout.daters.client.CalloutListener;
import com.atasoft.flangeassist.fragments.callout.daters.client.CalloutParser;
import com.atasoft.flangeassist.fragments.callout.daters.hire.ClassificationFilter;
import com.atasoft.flangeassist.fragments.callout.table.CalloutListAdapter;

import java.util.Date;
import java.util.EnumSet;


public class CalloutFragment extends Fragment implements CustomMenuLayout, CalloutDialogFrag.DismissListener {
    ExpandableListView listView;
    RelativeLayout rootView;
    CalloutListAdapter listAdapter;
    SparseArray<CalloutJob> jobs = new SparseArray<>();
    ProgressDialog progressDialog;
    Date lastPull;
    EnumSet<ClassificationFilter> activeFilters;
    LinearLayout errorView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = (RelativeLayout)inflater.inflate(R.layout.callout_list, container, false);
        listView = (ExpandableListView) rootView.findViewById(R.id.callout_list);
        errorView = (LinearLayout)inflater.inflate(R.layout.callout_error_text, container, false);
        errorView.setVisibility(View.GONE);
        rootView.addView(errorView);
        listAdapter = new CalloutListAdapter(getActivity(), jobs);
        listView.setAdapter(listAdapter);
        updateJobs();

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.e("BLAH", "inflated called.");

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        Log.e("BLAH", "inflated called.");

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public int getMenuRes() {
        return R.menu.callout_actions;
    }

    @Override
    public void onDismiss(EnumSet<ClassificationFilter> filters) {
        this.activeFilters = filters;

        PreferenceManager
                .getDefaultSharedPreferences(listView.getContext())
                .edit()
                .putString("callout_active_filters", ClassificationFilter.serialize(activeFilters))
                .apply();

        updateJobs();
    }

    @Override
    public EnumSet<ClassificationFilter> getActiveFilters() {
        if (activeFilters == null) {
            String serialized = PreferenceManager.getDefaultSharedPreferences(listView.getContext()).getString("callout_active_filters", "");
            activeFilters = ClassificationFilter.deserialize(serialized);
        }

        return activeFilters;
    }

    private void updateJobs() {
        errorView.setVisibility(View.GONE);

        getActiveFilters();
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading Callout...");
        progressDialog.show();

        (new CalloutParser()).parseCallout(getContext(), new CalloutListener() {
            @Override
            public void onSuccess(CalloutResponse response) {
                response.fillSparse(jobs, activeFilters);
                lastPull = response.lastPull;
                listAdapter.notifyDataSetChanged();
                hideProgress("Success!");
                checkNoJobs();
            }

            @Override
            public void onFail() {
                hideProgress("Error getting callouts");
            }
        });
    }

    private void checkNoJobs() {
        if (listAdapter.getGroupCount() == 0) {
            errorView.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgress(String message) {
        if (progressDialog != null) {
            progressDialog.setMessage(message);
            //TODO: Swap drawables.
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dismissProgress();
                }
            }, 500);
        }
    }

    private void dismissProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }


}
