package com.neocaptainnemo.atms.service

import android.content.Context

import com.neocaptainnemo.atms.R

import javax.inject.Inject
import javax.inject.Singleton

class DistanceFormatter @Inject constructor(private val context: Context) {

    fun format(meters: Double): String {
        return if (meters < 1000.0) {
            context.getString(R.string.distance_m, meters)
        } else {
            context.getString(R.string.distance_km, meters / 1000.0)

        }

    }
}
