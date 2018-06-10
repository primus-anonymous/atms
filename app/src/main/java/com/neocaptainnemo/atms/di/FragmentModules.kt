package com.neocaptainnemo.atms.di

import com.neocaptainnemo.atms.ui.list.ListFragment
import com.neocaptainnemo.atms.ui.map.GoogleMapsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface FragmentModules {

    @ContributesAndroidInjector
    fun contributeListFragment(): ListFragment

    @ContributesAndroidInjector
    fun contributeGoogleMapFragment(): GoogleMapsFragment

}