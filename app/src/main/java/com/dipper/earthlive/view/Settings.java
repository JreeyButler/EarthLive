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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MenuItem;

import com.dipper.earthlive.R;
import com.dipper.earthlive.notification.BaseNotification;
import com.dipper.earthlive.util.Constants;
import com.dipper.earthlive.util.Tools;

import java.util.Objects;

import static com.dipper.earthlive.util.Constants.Key;


/**
 * @author Dipper
 * @date 2018/12/1
 * @email dipper.difference@gmail.com
 */
public class Settings extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    private final String TAG = "Settings";

    private ListPreference mDataFrom;
    private ListPreference mWallpaperSize;
    private ListPreference mUpdateCycle;
    private SwitchPreference mAutoUpdate;
    private SwitchPreference mWifiOnly;
    private PreferenceScreen mDataTraffic;
    private PreferenceScreen mOpenSource;

    private Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        Objects.requireNonNull(getActionBar()).setDisplayHomeAsUpEnabled(true);
        mContext = this;
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initDefaultValue();
    }

    private void initDefaultValue() {
        if (mDataFrom.getValue() == null || "".equals(mDataFrom.getValue())) {
            int index = getIndexFromValue(mDataFrom.getKey(),
                    mContext.getResources().getString(R.string.config_data_from));
            mDataFrom.setValueIndex(index);
            mDataFrom.setSummary(getSummaryFromIndex(mDataFrom.getKey(), index));
        } else {
            mDataFrom.setSummary(getSummaryFromValue(mDataFrom.getKey(), mDataFrom.getValue()));
        }
        if (mWallpaperSize.getValue() == null || "".equals(mWallpaperSize.getValue())) {
            int index = getIndexFromValue(mWallpaperSize.getKey(),
                    mContext.getResources().getString(R.string.config_wallpaper_size));
            mWallpaperSize.setValueIndex(index);
            mWallpaperSize.setSummary(getSummaryFromIndex(mWallpaperSize.getKey(), index));
        } else {
            mWallpaperSize.setSummary(getSummaryFromValue(mWallpaperSize.getKey(), mWallpaperSize.getValue()));
        }
        if (mUpdateCycle.getValue() == null || "".equals(mUpdateCycle.getValue())) {
            int index = getIndexFromValue(mUpdateCycle.getKey(),
                    mContext.getResources().getString(R.string.config_jp_update_cycle));
            mUpdateCycle.setValueIndex(index);
            mUpdateCycle.setSummary(getSummaryFromIndex(mUpdateCycle.getKey(), index));
        } else {
            mUpdateCycle.setSummary(getSummaryFromValue(mUpdateCycle.getKey(), mUpdateCycle.getValue()));
        }
    }

    private void initView() {
        mDataFrom = (ListPreference) findPreference(Key.KEY_DATA_FROM);
        mWallpaperSize = (ListPreference) findPreference(Key.KEY_WALLPAPER_SIZE);
        mAutoUpdate = (SwitchPreference) findPreference(Key.KEY_AUTO_UPDATE);
        mWifiOnly = (SwitchPreference) findPreference(Key.KEY_WIFI_ONLY);
        mUpdateCycle = (ListPreference) findPreference(Key.KEY_UPDATE_CYCLE);
        mDataTraffic = (PreferenceScreen) findPreference(Key.KEY_DATA_TRAFFIC);
        mOpenSource = (PreferenceScreen) findPreference(Key.KEY_OPEN_SOURCE);

        mDataFrom.setOnPreferenceChangeListener(this);
        mWallpaperSize.setOnPreferenceChangeListener(this);
        mUpdateCycle.setOnPreferenceChangeListener(this);

        mAutoUpdate.setOnPreferenceClickListener(this);
        mWifiOnly.setOnPreferenceClickListener(this);
        mDataFrom.setOnPreferenceClickListener(this);
        mWallpaperSize.setOnPreferenceClickListener(this);
        mUpdateCycle.setOnPreferenceClickListener(this);
        mOpenSource.setOnPreferenceClickListener(this);

        mWifiOnly.setShouldDisableView(true);
        mUpdateCycle.setShouldDisableView(true);
        mDataTraffic.setShouldDisableView(true);

        filterAutoPreference(mAutoUpdate.isChecked());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        String key = preference.getKey();
        key = key == null ? "" : key;
        String value = String.valueOf(o);

        if (Key.KEY_DATA_FROM.equals(key)) {
            String cycle = mContext.getResources().getString(R.string.config_jp_update_cycle);
            if (value.equals(mContext.getResources().getString(R.string.value_japan))) {
                mDataFrom.setSummary(R.string.japan);
                mUpdateCycle.setEntries(R.array.jp_update_cycle_entries);
                mUpdateCycle.setEntryValues(R.array.jp_update_cycle_values);
                cycle = mContext.getResources().getString(R.string.config_jp_update_cycle);
            } else if (value.equals(mContext.getResources().getString(R.string.value_china))) {
                mDataFrom.setSummary(R.string.china);
                mUpdateCycle.setEntries(R.array.cn_update_cycle_entries);
                mUpdateCycle.setEntryValues(R.array.cn_update_cycle_values);
                cycle = mContext.getResources().getString(R.string.config_cn_update_cycle);
                Log.d(TAG, "onPreferenceChange: 123");
            } else if (value.equals(mContext.getResources().getString(R.string.value_usa))) {
                mDataFrom.setSummary(R.string.usa);
            }
            // 更新壁纸
//            if (mAutoUpdate.isChecked()) {
            updateWallpaper();
//            }
            updateCycle(cycle);
        } else if (Key.KEY_WALLPAPER_SIZE.equals(key)) {
            if (value.equals(mContext.getResources().getString(R.string.value_720p))) {
                mWallpaperSize.setSummary(R.string.size_720p);
            } else if (value.equals(mContext.getResources().getString(R.string.value_1080p))) {
                mWallpaperSize.setSummary(R.string.size_1080p);
            }
            calculateData(value, mUpdateCycle.getValue());
        } else if (Key.KEY_UPDATE_CYCLE.equals(key)) {
            if (value.equals(mContext.getResources().getString(R.string.value_10_minus))) {
                mUpdateCycle.setSummary(R.string.minus_10);
            } else if (value.equals(mContext.getResources().getString(R.string.value_30_minus))) {
                mUpdateCycle.setSummary(R.string.minus_30);
            } else if (value.equals(mContext.getResources().getString(R.string.value_60_minus))) {
                mUpdateCycle.setSummary(R.string.minus_60);
            }
            calculateData(mWallpaperSize.getValue(), value);
            // 更新壁纸
            if (mAutoUpdate.isChecked()) {
                updateWallpaper();
            }
        }
        Tools.setStringSharePreference(mContext, key, value);
        return true;
    }

    private void updateWallpaper() {
        mContext.sendBroadcast(new Intent(Constants.ACTION_UPDATE_WALLPAPER));
    }

    private void updateCycle(String value) {
        mUpdateCycle.setSummary(getSummaryFromValue(mUpdateCycle.getKey(), value));
        mUpdateCycle.setValue(value);
        Tools.setStringSharePreference(mContext, mUpdateCycle.getKey(), value);
    }

    /**
     * 计算开启自动更新每小时消耗的流量
     *
     * @param size  壁纸大小
     * @param cycle 更新周期
     */
    private void calculateData(String size, String cycle) {
        Log.d(TAG, "calculateData: size = " + size + ",cycle = " + cycle);
        // 平均300KB/张
        final int size720P = 300;
        // 平均400KB/张
        final int size1080P = 400;
        long length = 0;
        if (size.equals(mContext.getResources().getString(R.string.value_720p))) {
            length += size720P;
        } else if (size.equals(mContext.getResources().getString(R.string.value_1080p))) {
            length += size1080P;
        }
        if (cycle.equals(mContext.getResources().getString(R.string.value_10_minus))) {
            length = 60 / 10 * length;
        } else if (cycle.equals(mContext.getResources().getString(R.string.value_30_minus))) {
            length = 60 / 30 * length;
        }
        if (mAutoUpdate.isChecked()) {
            mDataTraffic.setSummary(Tools.storageUnitConversion(length));
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        key = key == null ? "" : key;
        if (Key.KEY_DATA_FROM.equals(key)) {
            String value = ((ListPreference) preference).getValue();
            Log.d(TAG, "onPreferenceClick: value = " + value);
        } else if (Key.KEY_WALLPAPER_SIZE.equals(key)) {
            String value = ((ListPreference) preference).getValue();
            Log.d(TAG, "onPreferenceClick: value = " + value);
        } else if (Key.KEY_UPDATE_CYCLE.equals(key)) {
            String value = ((ListPreference) preference).getValue();
            Log.d(TAG, "onPreferenceClick: value = " + value);
        } else if (Key.KEY_AUTO_UPDATE.equals(key)) {
            SwitchPreference switchPreference = ((SwitchPreference) preference);
            boolean isAuto = switchPreference.isChecked();
            Log.d(TAG, "onPreferenceClick: isAuto = " + isAuto);
            switchPreference.setChecked(isAuto);
            filterAutoPreference(isAuto);
            Tools.setBooleanSharePreference(mContext, key, isAuto);
        } else if (Key.KEY_OPEN_SOURCE.equals(key)) {
            mContext.startActivity(new Intent(this, OpenSourceActivity.class));
        }
        return false;
    }

    private void filterAutoPreference(boolean isAuto) {
        mWifiOnly.setEnabled(isAuto);
        mUpdateCycle.setEnabled(isAuto);
        mDataTraffic.setEnabled(isAuto);
        BaseNotification notification = new BaseNotification(mContext);
        if (isAuto) {
            notification.showNotification(null);
            mAutoUpdate.setSummary(R.string.auto_help);
            calculateData(mWallpaperSize.getValue(), mUpdateCycle.getValue());
        } else {
            notification.cancelNotification();
            mAutoUpdate.setSummary("");
            mDataTraffic.setSummary("");
        }
        if (mDataFrom.getValue().equals(mContext.getResources().getString(R.string.value_japan))) {
            mUpdateCycle.setEntryValues(R.array.jp_update_cycle_values);
            mUpdateCycle.setEntries(R.array.jp_update_cycle_entries);
        } else if (mDataFrom.getValue().equals(mContext.getResources().getString(R.string.value_china))) {
            mUpdateCycle.setEntries(R.array.cn_update_cycle_entries);
            mUpdateCycle.setEntryValues(R.array.cn_update_cycle_values);
        }
    }

    private int getIndexFromValue(String key, String value) {
        CharSequence[] values = null;
        if (Key.KEY_DATA_FROM.equals(key)) {
            values = mDataFrom.getEntryValues();
        } else if (Key.KEY_WALLPAPER_SIZE.equals(key)) {
            values = mWallpaperSize.getEntryValues();
        } else if (Key.KEY_UPDATE_CYCLE.equals(key)) {
            values = mUpdateCycle.getEntryValues();
        }
        if (values == null || "".equals(value)) {
            return 0;
        }
        for (int i = 0; i < values.length; i++) {
            if (values[i].equals(value)) {
                return i;
            }
        }
        return 0;
    }

    private CharSequence getSummaryFromIndex(String key, int index) {
        CharSequence summary = mContext.getResources().getString(R.string.unknown);
        if (Key.KEY_DATA_FROM.equals(key)) {
            summary = mDataFrom.getEntries()[index];
        } else if (Key.KEY_WALLPAPER_SIZE.equals(key)) {
            summary = mWallpaperSize.getEntries()[index];
        } else if (Key.KEY_UPDATE_CYCLE.equals(key)) {
            summary = mUpdateCycle.getEntries()[index];
        }
        return summary;
    }

    private CharSequence getSummaryFromValue(String key, String value) {
        CharSequence summary = mContext.getResources().getString(R.string.unknown);
        int index = getIndexFromValue(key, value);
        if (Key.KEY_DATA_FROM.equals(key)) {
            summary = mDataFrom.getEntries()[index];
        } else if (Key.KEY_WALLPAPER_SIZE.equals(key)) {
            summary = mWallpaperSize.getEntries()[index];
        } else if (Key.KEY_UPDATE_CYCLE.equals(key)) {
            summary = mUpdateCycle.getEntries()[index];
        }
        return summary;
    }
}
