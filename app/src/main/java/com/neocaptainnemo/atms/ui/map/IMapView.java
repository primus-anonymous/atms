package com.neocaptainnemo.atms.ui.map;

import android.support.annotation.NonNull;

import com.neocaptainnemo.atms.model.AtmNode;
import com.neocaptainnemo.atms.model.ViewPort;
import com.neocaptainnemo.atms.ui.IAtmsView;

public interface IMapView extends IAtmsView {


    /**
     * Enables my location on the map.
     */
    void enableMyLocation();

    /**
     * Disables my location on the map.
     */
    void disableMyLocation();

    /**
     * Clears selected marker.
     */
    void clearSelectedMarker();

    /**
     * Marks given atm as selected.
     *
     * @param atmNode - selected atm.
     */
    void selectAtm(@NonNull AtmNode atmNode);

    /**
     * Moves camera to given atm node.
     *
     * @param atmNode - atm node to be zoomed to.
     */
    void moveCameraToAtm(@NonNull AtmNode atmNode);

    interface MapDelegate {
        /**
         * Informs whoever interested that user moves a camera and needs to request some data for
         * the given view port.
         *
         * @param viewPort - rect bound to fetch data from.
         */
        void onGotViewPort(@NonNull ViewPort viewPort);

        /**
         * Informs user that he needs to zoom in further.
         */
        void onZoomFurther();
    }

}
