package com.neocaptainnemo.atms.model

class ViewPort(val lngStart: Double, val latStart: Double, val lngEnd: Double, val latEnd: Double) {

    fun isInside(another: ViewPort): Boolean {
        return another.lngStart >= lngStart &&
                another.lngStart <= lngEnd &&
                another.latStart >= latStart &&
                another.latStart <= latEnd &&

                another.lngEnd >= lngStart &&
                another.lngEnd <= lngEnd &&
                another.latEnd >= latStart &&
                another.latEnd <= latEnd
    }
}
