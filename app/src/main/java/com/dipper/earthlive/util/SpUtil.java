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
import android.util.Log;

import androidx.preference.PreferenceManager;

/**
 * @author Dipper
 */
public class SpUtil {
    private static final String TAG = "SpUtil";

    private boolean isConfiguration;
    private SharedPreferences sharedPreferences;

    public SpUtil(Context context) {
        new SpUtil(context, false);
    }

    public SpUtil(Context context, boolean isConfiguration) {
        this.isConfiguration = isConfiguration;
        if (isConfiguration) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        } else {
            sharedPreferences = context.getSharedPreferences(Constants.SHARE_PREFERENCES_NAME, Context.MODE_PRIVATE);
        }
    }

    /**
     * @param key   key
     * @param value value
     */
    public void setInt(String key, int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (isConfiguration) {
            editor.putString(key, String.valueOf(value));
        } else {
            editor.putInt(key, value);
        }
        editor.apply();
        editor.clear();
    }

    /**
     * @param key 键
     * @return 值
     */
    public int getInt(String key) {
        if (isConfiguration) {
            String value = sharedPreferences.getString(key, "-1");
            return Integer.parseInt(value);
        }
        return sharedPreferences.getInt(key, -1);
    }


    @SuppressLint("CommitPrefEdits")
    public void setString(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
        editor.clear();
    }

    /**
     * @param key          键
     * @param defaultValue 默认值
     * @return 值
     */
    public String getString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    /**
     * @param key   键
     * @param value 值
     */
    public void setBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
        editor.clear();
    }

    /**
     * @param key          键
     * @param defaultValue 默认值
     * @return 值
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    /**
     * @param key   键
     * @param value 值
     */
    public void setLong(String key, long value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (isConfiguration) {
            editor.putString(key, String.valueOf(value));
        } else {
            editor.putLong(key, value);
        }
        editor.apply();
        editor.clear();
    }

    /**
     * @param key          键
     * @param defaultValue 默认值
     * @return 值
     */
    public long getLong(String key, long defaultValue) {
        if (isConfiguration) {
            String value = sharedPreferences.getString(key, String.valueOf(defaultValue));
            return Long.parseLong(value);
        }
        return sharedPreferences.getLong(key, defaultValue);
    }
}
