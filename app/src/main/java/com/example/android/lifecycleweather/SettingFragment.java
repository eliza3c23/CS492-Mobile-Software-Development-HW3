package com.example.android.lifecycleweather;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;

public class SettingFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle bundle,String s){
        addPreferencesFromResource(R.xml.prefs);
        EditTextPreference userPref = findPreference("pref_location");
        userPref.setSummary(userPref.getText());
    }
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,String key){
        if(key.equals("pref_location")){
            EditTextPreference preference = findPreference(key);
            preference.setSummary(preference.getText());
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }
    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangedListener(this);
    }
}
