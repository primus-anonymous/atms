package com.neocaptainnemo.atms.service

import com.neocaptainnemo.atms.model.AtmNode
import com.neocaptainnemo.atms.model.ViewPort
import io.reactivex.Observable
import java.util.*

class AtmsRepo(private val openStreetMapApi: OpenStreetMap) : Atms {


    override fun request(viewPort: ViewPort): Observable<List<AtmNode>> {

        val sb = StringBuilder()

        val formatter = Formatter(sb, Locale.US)
        formatter.format("[out:json];node(%f,%f,%f,%f)[atm];out;", viewPort.latStart,
                viewPort.lngStart, viewPort.latEnd, viewPort.lngEnd)

        return openStreetMapApi.request(sb.toString())
                .map { osmResponse ->
                    osmResponse.atms ?: listOf()
                }
    }
}