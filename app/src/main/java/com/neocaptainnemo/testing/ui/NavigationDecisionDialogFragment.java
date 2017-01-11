package com.neocaptainnemo.testing.ui;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.neocaptainnemo.testing.R;
import com.neocaptainnemo.testing.model.AtmNode;

import java.util.Formatter;
import java.util.Locale;

public class NavigationDecisionDialogFragment extends DialogFragment {

    static final String TAG = "NavigationDecisionDialogFragment";

    static NavigationDecisionDialogFragment instance(@NonNull AtmNode atmNode) {
        NavigationDecisionDialogFragment fragment = new NavigationDecisionDialogFragment();
        Bundle arg = new Bundle();
        arg.putParcelable("atm", atmNode);
        fragment.setArguments(arg);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.choose_the_nav_way)
                .setItems(R.array.nav_way_array, (dialog, which) -> {

                    AtmNode atm = getArguments().getParcelable("atm");

                    StringBuilder geoBuilder = new StringBuilder();
                    Formatter formatter = new Formatter(geoBuilder, Locale.US);

                    switch (which) {
                        case 0:
                            formatter.format("google.navigation:q=%f,%f&mode=%s", atm.getLat(), atm.getLon(), "w");
                            break;

                        case 1:
                            formatter.format("google.navigation:q=%f,%f&mode=%s", atm.getLat(), atm.getLon(), "d");
                            break;

                        default:
                            //do nothing
                            break;
                    }


                    Uri gmmIntentUri = Uri.parse(geoBuilder.toString());
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    startActivity(mapIntent);

                });
        return builder.create();
    }
}
