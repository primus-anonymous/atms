package com.neocaptainnemo.testing.service;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

interface OpenStreetMap {

    @GET("cgi/interpreter")
    Observable<OsmResponse> request(@Query("data") String data);
}
