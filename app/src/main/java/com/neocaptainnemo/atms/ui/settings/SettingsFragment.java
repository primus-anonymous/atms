package com.neocaptainnemo.atms.ui.settings;


import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;

import com.neocaptainnemo.atms.BuildConfig;
import com.neocaptainnemo.atms.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    public static final String TAG = "SettingsFragment";

    public static SettingsFragment instance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

    }

    @Override
    protected void onBindPreferences() {
        super.onBindPreferences();

        PreferenceScreen summary = (PreferenceScreen) findPreference("summary");

        summary.setSummary(getString(R.string.settings_rate_about_summary, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE));
    }
}
