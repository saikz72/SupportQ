package com.example.supportq.Fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.supportq.R;

public class MyAlertDialogFragment extends DialogFragment {
    private static String title = "title";

    public MyAlertDialogFragment() {
        // Required empty public constructor
    }

    public static MyAlertDialogFragment newInstance(String title) {
        MyAlertDialogFragment fragment = new MyAlertDialogFragment();
        Bundle args = new Bundle();
        args.putString(MyAlertDialogFragment.title, title);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String title = getArguments().getString(getContext().getString(R.string.title));
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(getContext().getString(R.string.internet_message));
        alertDialogBuilder.setPositiveButton(getContext().getString(R.string.OK), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return alertDialogBuilder.create();
    }
}