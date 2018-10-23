package com.trials.userpreference.demo;

import android.app.ProgressDialog;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;

import com.trials.supertriathlon.R;

/**
 * Created by Kohei Moroi on 12/23/2016.
 */


public class Spinner implements Runnable {
    /** The delay that must pass before showing a spinner. */
    private static final int SPINNER_DELAY_MS = 300;
    /** A Handler for showing a spinner if service call latency becomes too long. */
    private Handler spinnerHandler;
    private volatile boolean isCanceled = false;
    private volatile ProgressDialog progressDialog = null;
    private FragmentActivity mActivity;

    public Spinner(FragmentActivity activity) {
        this.mActivity = activity;
        spinnerHandler = new Handler();
    }
    @Override
    public synchronized void run() {
        if (isCanceled) {
            return;
        }
        if (this.mActivity != null) {
            progressDialog = ProgressDialog.show(this.mActivity,
                    this.mActivity.getString(R.string.nosql_dialog_title_pending_results_text),
                    this.mActivity.getString(R.string.nosql_dialog_message_pending_results_text));
        }
    }

    public void schedule() {
        isCanceled = false;
        // Post delayed runnable so that the spinner will be shown if the delay
        // expires and results haven't come back.
        spinnerHandler.postDelayed(this, SPINNER_DELAY_MS);
    }

    public synchronized void cancelOrDismiss() {
        isCanceled = true;
        // Cancel showing the spinner if it hasn't been shown yet.
        spinnerHandler.removeCallbacks(this);

        if (progressDialog != null) {
            // if the spinner has been shown, dismiss it.
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
}