package com.neocaptainnemo.atms.mocks

import android.app.Activity
import android.app.Application
import android.support.v4.app.Fragment
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

class MockApp : Application(), HasActivityInjector, HasSupportFragmentInjector {

    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Activity>

    @Inject
    lateinit var supportFragmentInjector: DispatchingAndroidInjector<Fragment>

    lateinit var component: MockAppComponent

    override fun onCreate() {
        super.onCreate()

        component = DaggerMockAppComponent
                .builder()
                .application(this)
                .build()

        component.inject(this)
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = supportFragmentInjector

    override fun activityInjector(): AndroidInjector<Activity> = activityInjector

}
