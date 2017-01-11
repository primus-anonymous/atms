package com.neocaptainnemo.testing.service;

import android.content.Context;
import android.support.annotation.NonNull;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.neocaptainnemo.testing.model.AtmNode;
import com.neocaptainnemo.testing.model.ViewPort;

import java.util.Collections;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.functions.Func1;

public class AtmsRepo implements Atms {

    private OpenStreetMap openStreetMapApi;

    private Context context;

    public AtmsRepo(Context context) {
        this.context = context;

        OkHttpClient okHttp = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttp)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://overpass.osm.rambler.ru/")
                .build();

        openStreetMapApi = retrofit.create(OpenStreetMap.class);

    }


    @Override
    public Observable<List<AtmNode>> request(@NonNull ViewPort viewPort) {

        StringBuilder sb = new StringBuilder();

        Formatter formatter = new Formatter(sb, Locale.US);
        formatter.format("[out:json];node(%f,%f,%f,%f)[atm];out;", viewPort.getLatStart(),
                viewPort.getLngStart(), viewPort.getLatEnd(), viewPort.getLngEnd());

        return openStreetMapApi.request(sb.toString())
                .map(new Func1<OsmResponse, List<AtmNode>>() {
                    @Override
                    public List<AtmNode> call(OsmResponse osmResponse) {
                        if (osmResponse.getAtms() == null) {
                            return Collections.emptyList();
                        }

                        return osmResponse.getAtms();
                    }
                });
    }
}