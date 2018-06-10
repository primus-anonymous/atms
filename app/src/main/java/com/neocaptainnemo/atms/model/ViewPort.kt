package com.neocaptainnemo.atms.model

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil

data class ViewPort(val lngStart: Double, val latStart: Double, val lngEnd: Double, val latEnd: Double) {

    fun isInside(another: ViewPort): Boolean =
            another.lngStart >= lngStart &&
                    another.lngStart <= lngEnd &&
                    another.latStart >= latStart &&
                    another.latStart <= latEnd &&

                    another.lngEnd >= lngStart &&
                    another.lngEnd <= lngEnd &&
                    another.latEnd >= latStart &&
                    another.latEnd <= latEnd

    fun extended(): ViewPort {

        val southWest = SphericalUtil.computeOffset(LatLng(latStart, lngStart), 3000.0, 245.0)

        val startLng = southWest.longitude
        val startLat = southWest.latitude

        val northeast = SphericalUtil.computeOffset(LatLng(latEnd, lngEnd), 3000.0, 45.0)

        val endLng = northeast.longitude
        val endLat = northeast.latitude

        return ViewPort(startLng, startLat, endLng, endLat)
    }
}
