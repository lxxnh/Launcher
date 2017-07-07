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
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.text.LoginFilter;
import android.util.Log;

import com.android.launcher3.compat.UserHandleCompat;
import com.android.launcher3.floatbutton.FloatWindowManager;
import com.android.launcher3.floatbutton.FloatWindowService;
import com.android.launcher3.theme.Utils;

import java.util.ArrayList;
import java.util.List;

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
        private Preference mPrivateReset;
        private Preference mDefaultDesSet;
        private SwitchPreference mFloatButtonPref;
        private SwitchPreference mHideStatusbarPref;
        private SharedPreferences mPreferences;
        private SharedPreferences.Editor mEdit;
        private List<String> mList;
        private SharedPreferences mSharedPreferences;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.launcher_preferences);

            SwitchPreference pref = (SwitchPreference) findPreference(
                    Utilities.ALLOW_ROTATION_PREFERENCE_KEY);
            pref.setPersistent(false);
            mSharedPreferences = getActivity().getSharedPreferences
                    (LauncherAppState.getSharedPreferencesKey(), Context.MODE_PRIVATE);
            mSharedPreferences.registerOnSharedPreferenceChangeListener(mSharedPreferenceChangeListener);

            mPrivatePref = findPreference(Utilities.PRIVATE_FOLDER_PREFERENCE_KEY);
            mPrivateChangePwdPref = findPreference(Utilities.PRIVATE_CHANGE_PASSWORD_PREFERENCE_KEY);
            mPrivateReset = findPreference(Utilities.PRIVATE_RESET_KEY);
            mDefaultDesSet = findPreference(Utilities.DEFAULT_DES_SET_KEY);
            mFloatButtonPref = (SwitchPreference) findPreference(Utilities.SHOW_FLOAT_BUTTON_KEY);
            mFloatButtonPref.setOnPreferenceChangeListener(this);
            mHideStatusbarPref = (SwitchPreference) findPreference(Utilities.HIDE_STATUSBAR_KEY);
            mHideStatusbarPref.setOnPreferenceChangeListener(this);

            if("com.android.launcher3".equals(getLauncherPackageName(getContext()))){
                getPreferenceScreen().removePreference(mDefaultDesSet);
            }

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
            mPrivateReset.setOnPreferenceClickListener(this);
            mDefaultDesSet.setOnPreferenceClickListener(this);
            mPreferences = getContext().getSharedPreferences(LauncherFiles.SHARED_PREFERENCES_KEY, Context.MODE_MULTI_PROCESS);
            mList = new ArrayList<String>();
        }

        private final OnSharedPreferenceChangeListener mSharedPreferenceChangeListener = new OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (Utils.SHOW_FLOAT_BUTTON.equals(key)) {
                    updateFloatButtonChecked();
                }
            }
        };

        private void updateFloatButtonChecked() {
            mFloatButtonPref.setChecked(mSharedPreferences.getBoolean(Utils.SHOW_FLOAT_BUTTON, true));
        }

        @Override
        public void onDestroy() {
            mSharedPreferences.unregisterOnSharedPreferenceChangeListener(mSharedPreferenceChangeListener);
            super.onDestroy();
        }

        @Override
        public void onResume() {
            super.onResume();
            PreferenceScreen screen = (PreferenceScreen) findPreference(Utilities.PREF_SCREEN_KEY);
            String pwd = mPreferences.getString(Utilities.PRIVATE_PWD,"");
            if(pwd.isEmpty()) {
                screen.removePreference(mPrivateChangePwdPref);
            } else {
                screen.addPreference(mPrivateChangePwdPref);
            }
            updateFloatButtonChecked();
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String key = preference.getKey();
            if (Utilities.ALLOW_ROTATION_PREFERENCE_KEY.equals(key)) {
                Bundle extras = new Bundle();
                extras.putBoolean(LauncherSettings.Settings.EXTRA_VALUE, (Boolean) newValue);
                getActivity().getContentResolver().call(
                        LauncherSettings.Settings.CONTENT_URI,
                        LauncherSettings.Settings.METHOD_SET_BOOLEAN,
                        preference.getKey(), extras);
            } else if (Utilities.SHOW_FLOAT_BUTTON_KEY.equals(key)) {
                if ((Boolean) newValue) {
                    Intent intent = new Intent(getContext(), FloatWindowService.class);
                    getActivity().startService(intent);
                } else {
                    FloatWindowManager.removeBigWindow(getContext());
                    FloatWindowManager.removeSmallWindow(getContext());
                    Intent intent = new Intent(getContext(), FloatWindowService.class);
                    getActivity().stopService(intent);
                }
            } else if (Utilities.HIDE_STATUSBAR_KEY.equals(key)) {
                mSharedPreferences.edit().putBoolean(Utils.HIDE_STATUSBAR, (Boolean) newValue).commit();
            }
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
            if(preference == mPrivateReset) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.reset_private_folder);
                builder.setMessage(R.string.reset_private_folder_msg);
                builder.setPositiveButton(R.string.private_confirm_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        removeSharedPreference();
                        LauncherAppState.setApplicationContext(getActivity());
                        LauncherAppState app = LauncherAppState.getInstance();
                        Cursor cr = LauncherAppState.getInstance().getIconCache().queryInfoFromDB();
                        while (cr.moveToNext()) {
                            String pkgName = cr.getString(2);
                            mList.add(pkgName);
                        }
                        for(int j=0; j<mList.size(); j++) {
                            app.getModel().mBgAllAppsList.removePrivatedPackage(mList.get(j), UserHandleCompat.myUserHandle());
                        }

                        //TODO 这里有问题。由于SettingsActivity是独立进行，app.getModel().getCallback()为null
                        app.getModel().getCallback().bindAppsAdd(LauncherAppState.getInstance().getModel().mBgAllAppsList.privatedRemoved);
                        app.getIconCache().clearPrivateApps();

                        dialogInterface.dismiss();
                    }
                });
                builder.setNegativeButton(R.string.private_confirm_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.create().show();
            }
            if(preference == mDefaultDesSet){
                Intent  paramIntent = new Intent("android.intent.action.MAIN");
                paramIntent.setComponent(new ComponentName("android", "com.android.internal.app.ResolverActivity"));
                paramIntent.addCategory("android.intent.category.DEFAULT");
                paramIntent.addCategory("android.intent.category.HOME");
                startActivity(paramIntent);
            }

            return true;
        }


        //reset the values of private folder
        public void removeSharedPreference() {
            mEdit = mPreferences.edit();
            mEdit.putBoolean(Utilities.FIRST_SET_PRIVATE,true);
            mEdit.putString(Utilities.PRIVATE_PWD,"");
            mEdit.commit();
        }

        /**
         * 获取正在运行桌面包名（注：存在多个桌面时且未指定默认桌面时，该方法返回Null）
         */
        public static String getLauncherPackageName(Context context) {
            final Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            final ResolveInfo res = context.getPackageManager().resolveActivity(intent, 0);
            Log.d("xjr", "activityInfo.packageName = " + res.activityInfo.packageName);
            if (res.activityInfo == null) {
                // should not happen. A home is always installed, isn't it?
                return null;
            }
            if (res.activityInfo.packageName.equals("android")) {
                // 有多个桌面程序存在，且未指定默认项时；
                return null;
            } else {
                return res.activityInfo.packageName;
            }
        }

    }
}
