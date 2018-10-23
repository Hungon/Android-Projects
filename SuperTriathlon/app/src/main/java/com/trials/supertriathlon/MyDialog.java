package com.trials.supertriathlon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

/**
 * Created by USER on 2/15/2016.
 */
public class MyDialog extends DialogFragment {
    /*
        Show message
     */
    public static void show(String title, String text) {
        MyDialog dialog = new MyDialog();
        Bundle args = new Bundle();
        args.putString("title",title);
        args.putString("text", text);
        dialog.setArguments(args);
        dialog.show(GameView.GetActivity().getFragmentManager(), "messageDialog");
    }
    /*
        Create dialog
     */
    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        ad.setTitle(getArguments().getString("title"));
        ad.setMessage(getArguments().getString("text"));
        ad.setPositiveButton("OK",null);
        return ad.create();
    }
}