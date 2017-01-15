package com.neocaptainnemo.testing.ui.map;

import android.location.Location;
import android.support.annotation.NonNull;

import com.neocaptainnemo.testing.model.AtmNode;
import com.neocaptainnemo.testing.model.ViewPort;

import java.util.List;

public interface IMapView {

    /**
     * Shows atms on the map.
     *
     * @param atmNodes - atms to be shown.
     */
    void showAtms(List<AtmNode> atmNodes);

    /**
     * Clears map completely.
     */
    void clearMap();

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
     * Sets current user location.
     *
     * @param location - current user location.
     */
    void setMyLocation(Location location);

    /**
     * Marks given atm as selected.
     *
     * @param atmNode - selected atm.
     */
    void selectAtm(@NonNull AtmNode atmNode);

    interface OnGotViewPort {
        void onGotViewPort(@NonNull ViewPort viewPort);

        void onZoomFurther();
    }

    interface OnAtmClicked {
        void onAtmClicked(@NonNull AtmNode atmNode);
    }

}
