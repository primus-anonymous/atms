package com.neocaptainnemo.atms.ui.map

import com.neocaptainnemo.atms.model.AtmNode
import com.neocaptainnemo.atms.model.ViewPort
import com.neocaptainnemo.atms.ui.IAtmsView

interface IMapView : IAtmsView {


    /**
     * Enables my location on the map.
     */
    fun enableMyLocation()

    /**
     * Disables my location on the map.
     */
    fun disableMyLocation()

    /**
     * Clears selected marker.
     */
    fun clearSelectedMarker()

    /**
     * Marks given atm as selected.
     *
     * @param atmNode - selected atm.
     */
    fun selectAtm(atmNode: AtmNode)

    /**
     * Moves camera to given atm node.
     *
     * @param atmNode - atm node to be zoomed to.
     */
    fun moveCameraToAtm(atmNode: AtmNode)

    interface MapDelegate {
        /**
         * Informs whoever interested that user moves a camera and needs to request some data for
         * the given view port.
         *
         * @param viewPort - rect bound to fetch data from.
         */
        fun onGotViewPort(viewPort: ViewPort)

        /**
         * Informs user that he needs to zoom in further.
         */
        fun onZoomFurther()
    }

}
