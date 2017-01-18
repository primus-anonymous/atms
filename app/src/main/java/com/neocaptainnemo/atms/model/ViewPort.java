package com.neocaptainnemo.atms.model;

import android.support.annotation.NonNull;

public class ViewPort {

    private double lngStart;
    private double latStart;
    private double lngEnd;
    private double latEnd;

    public ViewPort(double lngStart, double latStart, double lngEnd, double latEnd) {
        this.lngStart = lngStart;
        this.latStart = latStart;
        this.lngEnd = lngEnd;
        this.latEnd = latEnd;
    }

    public boolean isInside(@NonNull ViewPort another) {
        boolean res = (another.getLngStart() >= lngStart &&
                another.getLngStart() <= lngEnd &&
                another.getLatStart() >= latStart &&
                another.getLatStart() <= latEnd &&

                another.getLngEnd() >= lngStart &&
                another.getLngEnd() <= lngEnd &&
                another.getLatEnd() >= latStart &&
                another.getLatEnd() <= latEnd);
        return res;
    }

    public double getLngStart() {
        return lngStart;
    }

    public double getLatStart() {
        return latStart;
    }

    public double getLngEnd() {
        return lngEnd;
    }

    public double getLatEnd() {
        return latEnd;
    }
}
