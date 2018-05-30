package com.neocaptainnemo.atms.app

import android.app.Application

import com.facebook.stetho.Stetho

open class App : Application() {

    open var appComponent: AppComponent? = null

    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)

        appComponent = DaggerAppComponent
                .builder()
                .appModule(AppModule(this))
                .build()
    }
}
