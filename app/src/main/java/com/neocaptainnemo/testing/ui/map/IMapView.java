package com.neocaptainnemo.testing.ui.map;

import android.location.Location;
import android.support.annotation.NonNull;

import com.neocaptainnemo.testing.model.AtmNode;
import com.neocaptainnemo.testing.model.ViewPort;

import java.util.List;

public interface IMapView {

    void showAtms(List<AtmNode> atmNodes);

    void clearMap();

    void enableMyLocation();

    void disableMyLocation();

    void clearSelectedMarker();

    void setMyLocation(Location location);

    void selectAtm(@NonNull AtmNode atmNode);

    interface OnGotViewPort {
        void onGotViewPort(@NonNull ViewPort viewPort);

        void onZoomFurther();
    }

    interface OnAtmClicked {
        void onAtmClicked(@NonNull AtmNode atmNode);
    }

}
