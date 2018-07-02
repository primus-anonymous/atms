package com.neocaptainnemo.atms.service

import android.content.Context
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.neocaptainnemo.atms.BuildConfig
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class ServiceModule {

    @Provides
    @Singleton
    fun providesAtms(): Atms {

        val okHttp = OkHttpClient.Builder()

        if (BuildConfig.BUILD_TYPE == "debug") {
            okHttp.addNetworkInterceptor(StethoInterceptor())

        }

        val retrofit = Retrofit.Builder()
                .client(okHttp.build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BuildConfig.OSM_API_URL)
                .build()

        return AtmsRepo(retrofit.create(OpenStreetMap::class.java))
    }

    @Provides
    @Singleton
    fun providesSettings(context: Context): ISettings = Settings(context = context)

}
