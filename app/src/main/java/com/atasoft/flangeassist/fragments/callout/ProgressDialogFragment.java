package com.atasoft.flangeassist.fragments.callout;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;

/**
 * Created by ataboo on 4/15/2017.
 */

public class ProgressDialogFragment extends DialogFragment {
    Handler dismissHandler;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setCancelable(false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ProgressDialog dialog = new ProgressDialog(getActivity(), getTheme());
        dialog.setMessage("Loading Callout...");
        dialog.setIndeterminate(true);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        return dialog;
    }

    public void dismissWithMessage(String message, long delay) {
        ProgressDialog dialog = (ProgressDialog) getDialog();
        if (dialog != null) {
            dialog.setMessage(message);
        }

        dismissHandler = new Handler();

        dismissHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dismissAllowingStateLoss();
            }
        }, delay);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (dismissHandler != null) {
            dismissHandler.removeCallbacksAndMessages(null);
        }
    }
}
