package com.neocaptainnemo.atms.service

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenStreetMap {

    @GET("api/interpreter")
    fun request(@Query("data") data: String): Observable<OsmResponse>
}
