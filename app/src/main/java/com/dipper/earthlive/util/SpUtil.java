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

package com.dipper.earthlive.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.dipper.earthlive.application.EarthLiveApplication;

public class SpUtil {
    private static final String TAG = "SpUtil";
    private static SpUtil mUtil;
    private Context mContext;

    private SpUtil() {
        mContext = EarthLiveApplication.getContext();
    }

    public static SpUtil getInstance() {
        if (mUtil == null) {
            mUtil = new SpUtil();
        }
        return mUtil;
    }

    /**
     * @param key   key
     * @param value value
     */
    public void setIntValue(String key, int value) {
        SharedPreferences sharedPreferences =
                mContext.getSharedPreferences(Constants.SHARE_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
        editor.clear();
    }

    /**
     * @param key 键
     * @return 值
     */
    public int getIntValue(String key) {
        SharedPreferences sharedPreferences =
                mContext.getSharedPreferences(Constants.SHARE_PREFERENCES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, -1);
    }


    @SuppressLint("CommitPrefEdits")
    public void setStringSharePreference(String key, String value) {
        SharedPreferences preferences = mContext.getSharedPreferences(Constants.SHARE_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public String getStringSharePreference(String key, String defaultValue) {
        SharedPreferences preferences = mContext.getSharedPreferences(Constants.SHARE_PREFERENCES_NAME, Context.MODE_PRIVATE);
        return preferences.getString(key, defaultValue);
    }

    /**
     *
     * @param key
     * @param value
     */
    public void setBooleanSharePreference(String key, boolean value) {
        SharedPreferences preferences = mContext.getSharedPreferences(Constants.SHARE_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public boolean getBooleanSharePreference(String key, boolean defaultValue) {
        SharedPreferences preferences = mContext.getSharedPreferences(Constants.SHARE_PREFERENCES_NAME, Context.MODE_PRIVATE);
        return preferences.getBoolean(key, defaultValue);
    }

    /**
     *
     * @param key
     * @param value
     */
    public void setLongSharePreference(String key, long value) {
        SharedPreferences preferences = mContext.getSharedPreferences(Constants.SHARE_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    /**
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public long getLongSharePreference(String key, long defaultValue) {
        SharedPreferences preferences = mContext.getSharedPreferences(Constants.SHARE_PREFERENCES_NAME, Context.MODE_PRIVATE);
        return preferences.getLong(key, defaultValue);
    }

}
