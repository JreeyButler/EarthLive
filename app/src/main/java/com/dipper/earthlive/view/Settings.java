/*
 * Copyright (c) [2019] [Dipper]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dipper.earthlive.view;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.dipper.earthlive.R;
import com.dipper.earthlive.util.Constants;

import static com.dipper.earthlive.util.Constants.Key;


/**
 * @author Dipper
 * @date 2018/12/1
 * @email dipper.difference@gmail.com
 */
public class Settings extends AppCompatActivity {
    private static final String TAG = "Settings";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addBackButton();
        setContentView(new LinearLayout(this));
        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    private void addBackButton() {
        ActionBar actionBar = getActionBar();
        if (actionBar == null) {
            Log.e(TAG, "addBackButton: action bar is null");
            return;
        }
        actionBar.setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener, Preference.SummaryProvider {
        private static final String TAG = "SettingsFragment";

        private Context mContext;

        @Override
        public void onAttach(@NonNull Context context) {
            super.onAttach(context);
            this.mContext = context;
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.settings, rootKey);
            initView();
        }

        private String[] keys = new String[]{
                Key.KEY_DATA_FROM,
                Key.KEY_WALLPAPER_SIZE,
                Key.KEY_AUTO_UPDATE,
                Key.KEY_WIFI_ONLY,
                Key.KEY_UPDATE_CYCLE,
                Key.KEY_DATA_TRAFFIC,
                Key.KEY_OPEN_SOURCE,
                Key.KEY_HELP,
        };

        private void initView() {
            for (String key : keys) {
                Preference preference = findPreference(key);
                if (preference == null) {
                    continue;
                }
                preference.setOnPreferenceChangeListener(this);
                preference.setOnPreferenceClickListener(this);
                preference.setSummaryProvider(this);

            }
            filterPreference(isAutoDownloadWallpaper());
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String key = preference.getKey();
            if (Key.KEY_AUTO_UPDATE.equals(key)) {
                filterPreference((boolean) newValue);
            }
            return true;
        }

        private void filterPreference(boolean isAuto) {
            Log.d(TAG, "filterPreference: isAuto = " + isAuto);
            for (String key : keys) {
                switch (key) {
                    case Key.KEY_WIFI_ONLY:
                    case Key.KEY_DATA_TRAFFIC:
                        Preference preference = findPreference(key);
                        if (preference == null) {
                            break;
                        }
                        preference.setEnabled(isAuto);
                        break;
                    default:
                        break;
                }
            }
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            String key = preference.getKey();
            key = key == null ? "" : key;
            switch (key) {
                case Key.KEY_OPEN_SOURCE:
                    mContext.startActivity(new Intent(mContext, OpenSourceActivity.class));
                    break;
                case Key.KEY_HELP:
                    mContext.startActivity(new Intent(mContext, HelpActivity.class));
                    break;
                default:
                    break;
            }
            return true;
        }

        @Override
        public CharSequence provideSummary(Preference preference) {
            CharSequence summary = "";
            String key = preference.getKey();
            if (key == null || "".equals(key)) {
                return summary;
            }
            switch (key) {
                case Key.KEY_DATA_FROM:
                case Key.KEY_WALLPAPER_SIZE:
                case Key.KEY_UPDATE_CYCLE:
                    ListPreference listPreference = ((ListPreference) preference);
                    int index = listPreference.findIndexOfValue(listPreference.getValue());
                    if (index == -1) {
                        break;
                    }
                    summary = listPreference.getEntries()[index];
                    break;
                default:
                    break;
            }
            return summary;
        }

        private void updateWallpaperSize() {
            mContext.sendBroadcast(new Intent(Constants.ACTION_UPDATE_SIZE));
        }

        private void updateWallpaper() {
            mContext.sendBroadcast(new Intent(Constants.ACTION_UPDATE_WALLPAPER));
        }

        private boolean isAutoDownloadWallpaper() {
            return false;
        }
    }
}
