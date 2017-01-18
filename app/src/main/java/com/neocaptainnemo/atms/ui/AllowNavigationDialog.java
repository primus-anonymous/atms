package com.neocaptainnemo.atms.ui;

import android.Manifest;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.neocaptainnemo.atms.R;

public class AllowNavigationDialog extends DialogFragment {

    public static final String TAG = "AllowNavigationDialog";

    public static AllowNavigationDialog instance() {
        return new AllowNavigationDialog();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.allow_navigation)
                .setPositiveButton(R.string.btn_allow, (dialog, id) -> {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MainActivity.LOCATION_PERMISSION);
                })
                .setNegativeButton(R.string.btn_disallow, (dialog, id) -> {
                    //do nothing
                });
        return builder.create();
    }

}
