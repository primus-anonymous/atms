package com.neocaptainnemo.atms.ui.settings

import android.arch.lifecycle.ViewModel
import android.content.Context
import com.neocaptainnemo.atms.BuildConfig
import com.neocaptainnemo.atms.R
import com.neocaptainnemo.atms.model.MapType
import com.neocaptainnemo.atms.service.ISettings
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject


enum class ItemType {
    ABOUT_SM, RATE, MAP_TYPE, OTHER
}

sealed class SettingsItem

data class HeaderItem(val header: String) : SettingsItem()

data class ValueItem(val type: ItemType = ItemType.OTHER, val header: String, val value: String) : SettingsItem()


class SettingsViewModel @Inject constructor(private val settings: ISettings) : ViewModel() {

    private val mapTypeSubject = BehaviorSubject.createDefault(settings.mapType)

    var mapType: MapType
        get() = mapTypeSubject.value
        set(value) {
            settings.mapType = value
            mapTypeSubject.onNext(value)
        }


    fun items(context: Context): Observable<List<SettingsItem>> = mapTypeSubject.map {
        listOf(
                HeaderItem(context.getString(R.string.settings_category_data)),
                ValueItem(ItemType.ABOUT_SM, context.getString(R.string.about_osm), context.getString(R.string.about_osm_value)),
                HeaderItem(context.getString(R.string.settings_category_map)),
                ValueItem(ItemType.MAP_TYPE, context.getString(R.string.map_type),
                        if (it == MapType.GOOGLE) context.getString(R.string.map_google) else context.getString(R.string.map_yandex)),
                HeaderItem(context.getString(R.string.settings_category_common)),
                ValueItem(ItemType.RATE, context.getString(R.string.settings_rate_title), context.getString(R.string.settings_rate_value)),
                ValueItem(ItemType.OTHER, context.getString(R.string.settings_rate_about),
                        context.getString(R.string.settings_rate_about_summary, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE)))
    }

}