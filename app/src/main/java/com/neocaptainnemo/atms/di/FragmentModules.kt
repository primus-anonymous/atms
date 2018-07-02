package com.neocaptainnemo.atms.di

import com.neocaptainnemo.atms.ui.list.ListFragment
import com.neocaptainnemo.atms.ui.map.GoogleMapsFragment
import com.neocaptainnemo.atms.ui.map.YandexMapsFragment
import com.neocaptainnemo.atms.ui.settings.MapTypeSelectionDialog
import com.neocaptainnemo.atms.ui.settings.SettingsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface FragmentModules {

    @ContributesAndroidInjector
    fun contributeListFragment(): ListFragment

    @ContributesAndroidInjector
    fun contributeGoogleMapFragment(): GoogleMapsFragment

    @ContributesAndroidInjector
    fun contributeYandexMapsFragment(): YandexMapsFragment

    @ContributesAndroidInjector
    fun contributeSettingsFragment(): SettingsFragment

    @ContributesAndroidInjector
    fun contributeMapTypeSelectionDialog(): MapTypeSelectionDialog

}