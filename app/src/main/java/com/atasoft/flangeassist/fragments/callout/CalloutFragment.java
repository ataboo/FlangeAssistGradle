package com.atasoft.flangeassist.fragments.callout;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;
import com.atasoft.flangeassist.CustomMenuLayout;
import com.atasoft.flangeassist.R;
import com.atasoft.flangeassist.fragments.callout.daters.CalloutJob;
import com.atasoft.flangeassist.fragments.callout.daters.CalloutResponse;
import com.atasoft.flangeassist.fragments.callout.daters.client.CalloutListener;
import com.atasoft.flangeassist.fragments.callout.daters.client.CalloutParser;
import com.atasoft.flangeassist.fragments.callout.daters.hire.ClassificationFilter;
import com.atasoft.flangeassist.fragments.callout.table.CalloutListAdapter;
import com.atasoft.shared.AtaRequestQueue;

import java.util.Date;
import java.util.EnumSet;


public class CalloutFragment extends Fragment implements CustomMenuLayout, CalloutSettingsFragment.DismissListener {
    ExpandableListView listView;
    RelativeLayout rootView;
    CalloutListAdapter listAdapter;
    SparseArray<CalloutJob> jobs = new SparseArray<>();
    Handler progressHandler;
    Date lastPull;
    EnumSet<ClassificationFilter> activeFilters;
    LinearLayout errorView;
    TextView lastPullText;
    private FragmentActivity mActivity;
    ProgressDialogFragment progressFrag;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setRetainInstance(true);
        rootView = (RelativeLayout)inflater.inflate(R.layout.callout_list, container, false);
        listView = (ExpandableListView) rootView.findViewById(R.id.callout_list);
        lastPullText = (TextView) rootView.findViewById(R.id.last_pull_text);
        errorView = (LinearLayout)inflater.inflate(R.layout.callout_error_text, container, false);
        errorView.setVisibility(View.GONE);
        rootView.addView(errorView);
        listAdapter = new CalloutListAdapter(getActivity(), jobs);
        listView.setAdapter(listAdapter);
        updateJobs();

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        mActivity = (FragmentActivity) context;

        super.onAttach(context);
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
    public void onPause() {
        super.onPause();
        AtaRequestQueue.cancelAllRequests(mActivity);
        clearProgressHandler();
        if (progressFrag != null) {
            progressFrag.dismissAllowingStateLoss();
        }
    }

    @Override
    public EnumSet<ClassificationFilter> getActiveFilters() {
        if (activeFilters == null) {
            String serialized = PreferenceManager
                    .getDefaultSharedPreferences(listView.getContext())
                    .getString("callout_active_filters", null);

            if (serialized == null) {
                activeFilters = EnumSet.allOf(ClassificationFilter.class);
            } else {
                activeFilters = ClassificationFilter.deserialize(serialized);
            }
        }

        return activeFilters;
    }

    private void updateJobs() {
        if (!checkPermission()) {
            showErrorView("Cannot get the Callout without Internet Permission.");
            return;
        }

        errorView.setVisibility(View.GONE);
        getActiveFilters();
        showProgress(300);

        (new CalloutParser()).parseCallout(getContext(), new CalloutListener() {
            @Override
            public void onSuccess(CalloutResponse response) {
                response.fillSparse(jobs, activeFilters);
                setLastPull(response.lastPull);
                hideProgress("Success!");
                checkNoJobs();
            }

            @Override
            public void onFail(VolleyError error) {
                String errorDetail;

                if (error instanceof NoConnectionError) {
                    errorDetail = "Can't connect to the Internet.";
                } else {
                    errorDetail = "Failed to reach Toolbox server. Please try again later.";
                }

                hideProgress(errorDetail);
                showErrorView(errorDetail);
                jobs.clear();
            }

            @Override
            public void onBoth() {
                listAdapter.notifyDataSetChanged();
            }
        });
    }

    private void checkNoJobs() {
        if (listAdapter.getGroupCount() == 0) {
            showErrorView("Try changing your filter settings.");
        }
    }

    private void showProgress(long delay) {
        if (progressFrag != null) {
            return;
        }

        getProgressHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressFrag = new ProgressDialogFragment();
                mActivity.getSupportFragmentManager()
                        .beginTransaction()
                        .add(progressFrag, "progress_dialog_frag")
                        .commitAllowingStateLoss();
            }
        }, delay);
    }

    private void hideProgress(String message) {
        clearProgressHandler();
        if (progressFrag != null) {
            progressFrag.dismissWithMessage(message, 1000);
            progressFrag = null;
        }
    }

    private Handler getProgressHandler() {
        clearProgressHandler();

        if (progressHandler == null) {
            progressHandler = new Handler();
        }

        return progressHandler;
    }

    private void clearProgressHandler() {
        if (progressHandler != null) {
            progressHandler.removeCallbacksAndMessages(null);
        }
    }

    private void showErrorView(String detailText) {
        if (errorView == null) {
            return;
        }

        ((TextView)errorView.findViewById(R.id.detail)).setText(detailText);
        setLastPull(null);
        errorView.setVisibility(View.VISIBLE);
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        int result = ContextCompat.checkSelfPermission(mActivity, Manifest.permission.INTERNET);

        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.INTERNET}, 42);

        return false;
    }

    private void setLastPull(@Nullable Date lastPull) {
        String dateString = "N/A";

        if (lastPull != null) {
            dateString = CalloutResponse.DATE_FORMAT.format(lastPull);
        }

        lastPullText.setText("Last Server Update: " + dateString);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 42 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            updateJobs();
        } else {
            showErrorView("Cannot load the 146 Callout without Internet Permission.");
        }
    }
}
