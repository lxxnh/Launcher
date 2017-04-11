/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.launcher3;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.text.LoginFilter;
import android.util.Log;

/**
 * Settings activity for Launcher. Currently implements the following setting: Allow rotation
 */
public class SettingsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new LauncherSettingsFragment())
                .commit();
    }

    /**
     * This fragment shows the launcher preferences.
     */
    public static class LauncherSettingsFragment extends PreferenceFragment
            implements OnPreferenceChangeListener, Preference.OnPreferenceClickListener{
        private Preference mPrivatePref;
        private Preference mPrivateChangePwdPref;
        private SharedPreferences mPreferences;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.launcher_preferences);

            SwitchPreference pref = (SwitchPreference) findPreference(
                    Utilities.ALLOW_ROTATION_PREFERENCE_KEY);
            pref.setPersistent(false);

            mPrivatePref = findPreference(Utilities.PRIVATE_FOLDER_PREFERENCE_KEY);
            mPrivateChangePwdPref = findPreference(Utilities.PRIVATE_CHANGE_PASSWORD_PREFERENCE_KEY);

            Bundle extras = new Bundle();
            extras.putBoolean(LauncherSettings.Settings.EXTRA_DEFAULT_VALUE, false);
            Bundle value = getActivity().getContentResolver().call(
                    LauncherSettings.Settings.CONTENT_URI,
                    LauncherSettings.Settings.METHOD_GET_BOOLEAN,
                    Utilities.ALLOW_ROTATION_PREFERENCE_KEY, extras);
            pref.setChecked(value.getBoolean(LauncherSettings.Settings.EXTRA_VALUE));

            pref.setOnPreferenceChangeListener(this);
            mPrivatePref.setOnPreferenceClickListener(this);
            mPrivateChangePwdPref.setOnPreferenceClickListener(this);
            mPreferences = getContext().getSharedPreferences(LauncherFiles.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
        }

        @Override
        public void onResume() {
            super.onResume();
            PreferenceScreen screen = (PreferenceScreen) findPreference(Utilities.PREF_SCREEN_KEY);
            String pwd = mPreferences.getString("private_pwd","");
            if(pwd.isEmpty()) {
                screen.removePreference(mPrivateChangePwdPref);
            } else {
                screen.addPreference(mPrivateChangePwdPref);
            }
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            Bundle extras = new Bundle();
            extras.putBoolean(LauncherSettings.Settings.EXTRA_VALUE, (Boolean) newValue);
            getActivity().getContentResolver().call(
                    LauncherSettings.Settings.CONTENT_URI,
                    LauncherSettings.Settings.METHOD_SET_BOOLEAN,
                    preference.getKey(), extras);
            return true;
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            if(preference == mPrivatePref) {
                Intent intent = new Intent();
                intent.setClass(getActivity(),PasswordActivity.class);
                getContext().startActivity(intent);
            }
            if(preference == mPrivateChangePwdPref) {
                Intent intent = new Intent();
                intent.setClass(getActivity(),PasswordActivity.class);
                intent.putExtra(Utilities.CHANGE_PWD_FLAG,Utilities.ALLOW_CHANGE_PWD);
                getContext().startActivity(intent);
            }
            return true;
        }
    }
}
