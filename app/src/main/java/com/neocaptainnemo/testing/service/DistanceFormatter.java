package com.neocaptainnemo.testing.service;

import android.content.Context;

import com.neocaptainnemo.testing.R;

import javax.inject.Inject;

public class DistanceFormatter {
    private Context context;

    @Inject
    DistanceFormatter(Context context) {
        this.context = context;
    }

    public String format(double meters) {
        if (meters < 1000.0) {
            return context.getString(R.string.distance_m, meters);
        } else {
            return context.getString(R.string.distance_km, meters / 1000.0);

        }

    }
}
