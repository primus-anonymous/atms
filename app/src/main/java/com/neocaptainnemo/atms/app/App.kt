package com.neocaptainnemo.atms.app

import android.app.Activity
import android.app.Application
import android.support.v4.app.Fragment

import com.facebook.stetho.Stetho
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

open class App : Application(), HasActivityInjector, HasSupportFragmentInjector {

    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Activity>

    @Inject
    lateinit var supportFragmentInjector: DispatchingAndroidInjector<Fragment>


    open var appComponent: AppComponent? = null

    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)

        DaggerAppComponent
                .builder()
                .application(this)
                .build()
                .inject(this)
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = supportFragmentInjector

    override fun activityInjector(): AndroidInjector<Activity> = activityInjector
}
