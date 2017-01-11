package com.neocaptainnemo.testing.ui;

import android.support.annotation.NonNull;

import com.neocaptainnemo.testing.model.AtmNode;

import java.util.List;

interface IView {

    void onGotAtms(@NonNull List<AtmNode> atmNodes);

    void onStartGettingAtsm();

    void onAtmsFailed();

    void showZoomInFurther();

    void hideZoomInFurther();

    void showMap();

    void showList();

    void showSettings();
}
