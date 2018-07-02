package com.neocaptainnemo.atms.service

import android.content.Context
import com.neocaptainnemo.atms.model.MapType

class Settings(context: Context) : ISettings {

    private val prefs = context.getSharedPreferences(name, Context.MODE_PRIVATE)

    override var mapType: MapType
        get() {
            val key = prefs.getString(mapTypeKey, null)

            return MapType.values().find { it.id == key } ?: MapType.GOOGLE
        }
        set(value) {
            prefs.edit().putString(mapTypeKey, value.id).apply()
        }


    companion object {
        private const val name = "preferences.xml"

        private const val mapTypeKey = "mapType"
    }

}