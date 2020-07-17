package com.example.supportq.Models;

import android.app.ProgressDialog;
import android.content.Context;

public class ProgressIndicator {
    static ProgressDialog progressDialog;

    //displays progress bar
    public static void showMessage(Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Loading....");
        progressDialog.setMessage("Please wait");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    //hides progress bar
    public static void hideMessage(Context context) {
        progressDialog.dismiss();
    }

}
