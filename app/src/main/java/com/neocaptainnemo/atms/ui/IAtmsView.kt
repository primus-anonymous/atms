package com.neocaptainnemo.atms.ui

import android.location.Location

import com.neocaptainnemo.atms.model.AtmNode

interface IAtmsView {

    /**
     * Displays list of atms on the list.
     *
     * @param atmNodes - atms to be displayed.
     */
    fun showAtms(atmNodes: List<AtmNode>)

    /**
     * Sets current location used for distance calculations.
     *
     * @param location - current user location.
     */
    fun setMyLocation(location: Location)

    /**
     * Clears user data completely.
     */
    fun clear()


    interface OnAtmSelected {
        /**
         * Atm selected delegate.
         *
         * @param atmNode - selcted atm.
         * @param src     - view source.
         */
        fun onAtmSelected(atmNode: AtmNode, src: String)
    }

}
