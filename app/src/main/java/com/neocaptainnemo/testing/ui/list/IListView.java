package com.neocaptainnemo.testing.ui.list;

import android.location.Location;
import android.support.annotation.NonNull;

import com.neocaptainnemo.testing.model.AtmNode;

import java.util.List;

public interface IListView {

    /**
     * Displays list of atms on the list.
     *
     * @param atmNodes - atms to be displayed.
     */
    void showAtms(@NonNull List<AtmNode> atmNodes);

    /**
     * Sets current location used for distance calculations.
     *
     * @param location - current user location.
     */
    void setMyLocation(Location location);

    interface OnAtmSelected {
        void onAtmSelected(@NonNull AtmNode atmNode);
    }
}
