package com.neocaptainnemo.atms.di

import com.neocaptainnemo.atms.ui.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface ActivityModules {

    @ContributesAndroidInjector
    fun contributeMainActivity(): MainActivity

}