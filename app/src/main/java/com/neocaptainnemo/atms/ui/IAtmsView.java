package com.neocaptainnemo.atms.ui;

import android.location.Location;
import android.support.annotation.NonNull;

import com.neocaptainnemo.atms.model.AtmNode;

import java.util.List;

public interface IAtmsView {

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

    /**
     * Clears user data completely.
     */
    void clear();


    interface OnAtmSelected {
        /**
         * Atm selected delegate.
         *
         * @param atmNode - selcted atm.
         * @param src     - view source.
         */
        void onAtmSelected(@NonNull AtmNode atmNode, @NonNull String src);
    }

}
