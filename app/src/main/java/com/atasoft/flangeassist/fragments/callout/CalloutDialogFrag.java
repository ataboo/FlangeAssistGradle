package com.atasoft.flangeassist.fragments.callout;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.atasoft.flangeassist.R;
import com.atasoft.flangeassist.fragments.callout.daters.hire.ClassificationFilter;

import java.util.ArrayList;
import java.util.EnumSet;

/**
 * Created by ataboo on 4/15/2017.
 */

public class CalloutDialogFrag extends DialogFragment {
    public interface DismissListener {
        public void onDismiss(EnumSet<ClassificationFilter> filters);
        public EnumSet<ClassificationFilter> getActiveFilters();
    }

    public static CalloutDialogFrag newInstance(DismissListener listener) {
        CalloutDialogFrag frag = new CalloutDialogFrag();
        frag.listener = listener;

        Bundle args = new Bundle();
        args.putSerializable("selectedFilters", listener.getActiveFilters());
        frag.setArguments(args);

        return frag;
    }

    private DismissListener listener;
    ArrayList<CheckBox> checkboxes = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout rootView = (LinearLayout) inflater.inflate(R.layout.callout_filters, container, false);

        EnumSet<ClassificationFilter> selectedFilters = EnumSet.noneOf(ClassificationFilter.class);
        if (getArguments() != null) {
            selectedFilters = (EnumSet<ClassificationFilter>) getArguments().getSerializable("selectedFilters");
        }

        for (ClassificationFilter filter: ClassificationFilter.values()) {
            rootView.addView(makeCheckbox(filter, selectedFilters.contains(filter), inflater.getContext()));
        }

        return rootView;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        EnumSet<ClassificationFilter> activeFilters = EnumSet.noneOf(ClassificationFilter.class);

        for(CheckBox checkbox: checkboxes) {
            if (checkbox.isChecked()) {
                activeFilters.add((ClassificationFilter) checkbox.getTag());
            }
        }

        this.listener.onDismiss(activeFilters);
        super.onDismiss(dialog);
    }

    private CheckBox makeCheckbox(ClassificationFilter filter, boolean active, Context context) {
        CheckBox checkbox = new CheckBox(context);
        checkbox.setText(filter.name);
        checkbox.setChecked(active);
        checkbox.setTag(filter);
        checkboxes.add(checkbox);
        return checkbox;
    }
}
