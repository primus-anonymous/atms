package com.neocaptainnemo.atms.ui.settings


import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import android.support.v7.preference.PreferenceScreen
import com.neocaptainnemo.atms.BuildConfig
import com.neocaptainnemo.atms.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

    }

    override fun onStart() {
        super.onStart()

        val summary = findPreference("summary") as PreferenceScreen

        summary.summary = getString(R.string.settings_rate_about_summary, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE)

    }

    companion object {

        const val tag = "SettingsFragment"

        fun instance(): SettingsFragment = SettingsFragment()
    }
}
