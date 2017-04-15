package com.atasoft.flangeassist.fragments.callout;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.atasoft.flangeassist.R;

/**
 * Created by ataboo on 4/13/2017.
 */

public class CalloutFragment extends Fragment {
    View thisView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.flanges, container, false);
        thisView = v;

        return v;
    }


}
