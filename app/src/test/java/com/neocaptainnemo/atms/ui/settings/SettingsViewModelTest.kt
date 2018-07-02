package com.neocaptainnemo.atms.ui.settings

import android.content.Context
import com.neocaptainnemo.atms.BuildConfig
import com.neocaptainnemo.atms.R
import com.neocaptainnemo.atms.model.MapType
import com.neocaptainnemo.atms.service.ISettings
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class SettingsViewModelTest {

    @Mock
    lateinit var settings: ISettings

    @Mock
    lateinit var context: Context


    @Before
    fun setUp() {
        whenever(settings.mapType).then { MapType.GOOGLE }


        whenever(context.getString(R.string.settings_category_data)).then { "Data" }
        whenever(context.getString(R.string.about_osm)).then { "About OSM" }
        whenever(context.getString(R.string.about_osm_value)).then { "Click on OSM" }
        whenever(context.getString(R.string.settings_category_map)).then { "Map" }
        whenever(context.getString(R.string.map_type)).then { "Map Type" }
        whenever(context.getString(R.string.map_google)).then { "Google" }
        whenever(context.getString(R.string.map_yandex)).then { "Yandex" }
        whenever(context.getString(R.string.settings_category_common)).then { "Common" }
        whenever(context.getString(R.string.settings_rate_title)).then { "Rate" }
        whenever(context.getString(R.string.settings_rate_value)).then { "Click to rate" }
        whenever(context.getString(R.string.settings_rate_about)).then { "About" }
        whenever(context.getString(R.string.settings_rate_about_summary, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE)).then { "Version 1.2" }

    }

    @Test
    fun mapTypeGoogle() {
        whenever(settings.mapType).then { MapType.GOOGLE }

        val viewModel = SettingsViewModel(settings)

        val expected = listOf(HeaderItem("Data"),
                ValueItem(ItemType.ABOUT_SM, "About OSM", "Click on OSM"),
                HeaderItem("Map"),
                ValueItem(ItemType.MAP_TYPE, "Map Type", "Google"),
                HeaderItem("Common"),
                ValueItem(ItemType.RATE, "Rate", "Click to rate"),
                ValueItem(ItemType.OTHER, "About", "Version 1.2"))

        viewModel.items(context).test().assertValue(expected)

    }

    @Test
    fun mapTypeYandex() {
        whenever(settings.mapType).then { MapType.YANDEX }

        val viewModel = SettingsViewModel(settings)

        val expected = listOf(HeaderItem("Data"),
                ValueItem(ItemType.ABOUT_SM, "About OSM", "Click on OSM"),
                HeaderItem("Map"),
                ValueItem(ItemType.MAP_TYPE, "Map Type", "Yandex"),
                HeaderItem("Common"),
                ValueItem(ItemType.RATE, "Rate", "Click to rate"),
                ValueItem(ItemType.OTHER, "About", "Version 1.2"))


        viewModel.items(context).test().assertValue(expected)

    }

}