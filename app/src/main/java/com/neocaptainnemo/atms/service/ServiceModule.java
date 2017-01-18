package com.neocaptainnemo.atms.service;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.neocaptainnemo.atms.BuildConfig;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class ServiceModule {

    @Provides
    @Singleton
    Atms providesAtms() {

        OkHttpClient.Builder okHttp = new OkHttpClient.Builder();

        if (BuildConfig.BUILD_TYPE.equals("debug")) {
            okHttp.addNetworkInterceptor(new StethoInterceptor());

        }

        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttp.build())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BuildConfig.OSM_API_URL)
                .build();

        return new AtmsRepo(retrofit.create(OpenStreetMap.class));
    }
}
