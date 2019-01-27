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
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.util.Size;
import android.widget.Toast;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author Dipper
 * @date 2018/11/20
 * @email dipper.difference@gmail.com
 */
public class Tools {
    private final static String TAG = "Tools";

    /**
     * Dynamic permission check
     *
     * @param mContext    activity of application context
     * @param permissions important permission list
     * @return status of the permission list ,
     * true : have all permissions;
     * false: no partial permissions;
     */
    public static boolean hasPermissionGrated(Context mContext, String[] permissions) {
        for (String permission : permissions) {
            return checkPermission(mContext, permission);
        }
        return true;
    }

    /**
     * check the permission of the application
     *
     * @param context    activity or application context
     * @param permission check permission
     * @return status of permission.
     * true : has permission;
     * false: no permission
     */
    private static boolean checkPermission(Context context, String permission) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int result = context.checkSelfPermission(permission);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    /**
     * get signal thread pool
     *
     * @return Thread pool with 1 core thread
     */
    public static ExecutorService getSignalThreadPool() {
        return getThreadPool(1, 1);
    }

    /**
     * get a thread pool
     *
     * @param corePoolSize    the number of core thread pool
     * @param maximumPoolSize the number of maximum thread pool
     * @return Thread pool
     */
    public static ExecutorService getThreadPool(int corePoolSize, int maximumPoolSize) {
        ThreadFactory factory = new ThreadFactoryBuilder()
                .setNameFormat("single-pool-%d")
                .build();
        return new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingDeque<Runnable>(1024),
                factory,
                new ThreadPoolExecutor.AbortPolicy());
    }

    /**
     * check this device network is useful
     *
     * @param context activity context
     * @return network status，true:connect ，false:not connect
     */
    @SuppressLint("MissingPermission")
    public static boolean isNetworkUseful(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (info != null) {
                Log.d(TAG, "isNetworkUseful: isConnected = " + info.isConnected() + ", isAvailable = " + info.isAvailable());
                return info.isConnected() && info.isAvailable();
            }
        }
        return false;
    }

    /**
     * @param mContext activity context
     * @param key      key
     * @param value    value
     */
    public static void setIntValue(Context mContext, String key, int value) {
        SharedPreferences sharedPreferences =
                mContext.getSharedPreferences(Constants.SHARE_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
        editor.clear();
    }

    /**
     * @param mContext 上下文
     * @param key      键
     * @return 值
     */
    public static int getIntValue(Context mContext, String key) {
        SharedPreferences sharedPreferences =
                mContext.getSharedPreferences(Constants.SHARE_PREFERENCES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, -1);
    }

    @SuppressLint("CommitPrefEdits")
    public static void setStringSharePreference(Context context, String key, String value) {
        SharedPreferences preferences = context.getSharedPreferences(Constants.SHARE_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getStringSharePreference(Context context, String key, String defaultValue) {
        SharedPreferences preferences = context.getSharedPreferences(Constants.SHARE_PREFERENCES_NAME, Context.MODE_PRIVATE);
        return preferences.getString(key, defaultValue);
    }

    public static void setBooleanSharePreference(Context context, String key, boolean value) {
        SharedPreferences preferences = context.getSharedPreferences(Constants.SHARE_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getBooleanSharePreference(Context context, String key, boolean defaultValue) {
        SharedPreferences preferences = context.getSharedPreferences(Constants.SHARE_PREFERENCES_NAME, Context.MODE_PRIVATE);
        return preferences.getBoolean(key, defaultValue);
    }

    public static void setLongSharePreference(Context context, String key, long value) {
        SharedPreferences preferences = context.getSharedPreferences(Constants.SHARE_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static long getLongSharePreference(Context context, String key, long defaultValue) {
        SharedPreferences preferences = context.getSharedPreferences(Constants.SHARE_PREFERENCES_NAME, Context.MODE_PRIVATE);
        return preferences.getLong(key, defaultValue);
    }

    public static void generateWallpaper(Size size) {
        Log.d(TAG, "generateWallpaper: 13");
        if (!isPictureExists()) {
            Log.e(TAG, "generateWallpaper: picture not exists or open failed.");
            throw new RuntimeException("picture not exists or open failed.");
        }
        Log.d(TAG, "generateWallpaper: picture exists ");
        Bitmap bitmap00 = BitmapFactory.decodeFile(Constants.PICTURE_DIR_PATH + Constants.DEFAULT_NAME + Constants.NORMAL_PICTURE_STUFF);
        Bitmap bitmap = Bitmap.createBitmap(size.getWidth(), size.getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.BLACK);
        canvas.drawBitmap(bitmap00, ((size.getWidth() - bitmap00.getWidth()) / 2),
                (size.getHeight() - bitmap00.getHeight()) / 2,
                null);
        Log.d(TAG, "generateWallpaper: wallpaper width = " + bitmap.getWidth() + ",height = " + bitmap.getHeight());
        saveWallpaper(bitmap);
    }

    public static String storageUnitConversion(long length) {
        if (length <= Constants.KB) {
            return length + "KB";
        } else {
            DecimalFormat format = new DecimalFormat(".00");
            return format.format((float) length / Constants.KB) + "MB";
        }
    }

    /**
     * 将合成的Bitmap保存到文件夹中
     *
     * @param bitmap 合成的Bitmap
     */
    private static void saveWallpaper(Bitmap bitmap) {
        Log.d(TAG, "saveWallpaper: ");
        File wallpaper = new File(Constants.WALLPAPER_PATH);
        boolean isUpdate = true;
        if (wallpaper.exists()) {
            isUpdate = wallpaper.delete();
        }
        if (!isUpdate) {
            Log.d(TAG, "saveWallpaper: Wallpaper update failed! display old wallpaper");
        }
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(wallpaper));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            bos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 检查是否有上一次的壁纸存在
     *
     * @return 本地壁纸
     */
    public static boolean isWallpaperExists() {
        return new File(Constants.WALLPAPER_PATH).exists();
    }

    /**
     * 检查下载的图片是否存在
     *
     * @return true：有壁纸；false：无壁纸
     */
    private static boolean isPictureExists() {
        File picture00 = new File(Constants.PICTURE_DIR_PATH + Constants.DEFAULT_NAME + Constants.NORMAL_PICTURE_STUFF);
        return picture00.exists();
    }

    public static String[] getPictureUrls() {
        String latestTime = getLatestPictureTime();
        String api = Constants.PictureUrl.JAPAN_NORMAL_URL + latestTime;
        String[] urls = new String[Constants.SIZE_WALLPAPER];
        int index = 0;
        for (int i = 0; i < Constants.DEFAULT_SIZE; i++) {
            for (int j = 0; j < Constants.DEFAULT_SIZE; j++) {
                urls[index] = api + "_" + i + "_" + j + Constants.DEFAULT_PICTURE_STUFF;
                index++;
            }
        }
        Log.d(TAG, "getPictureUrls: urls size = " + urls.length);

        return urls;
    }

    /**
     * 获取现在最新的卫星图片时间
     *
     * @return 图片时间
     */
    public static String getLatestPictureTime() {
        long now = System.currentTimeMillis();
        long pictureTime = now - Constants.DEFAULT_DELAY_TIME;
        String date = dateFormat(pictureTime, Constants.DATE_FORMAT);
        String date1 = dateFormat(pictureTime, Constants.DATE_FORMAT_1);
        String time = getTime(date);
        return date1 + "/" + time;
    }

    private static String getTime(String time) {
        if (time == null || "".equals(time)) {
            Log.e(TAG, "getTime: time was null");
            return "";
        }
        Integer hour = Integer.valueOf(time.substring(0, 2)) - 8;
        Integer minus = Integer.valueOf(time.substring(3, time.length()));
        if (hour < 0) {
            hour = 24 + hour;
        }
        if (minus < 0) {
            minus = 60 - minus;
        }
        minus = minus / 10;
        String temp = String.valueOf(hour).length() <= 1 ? "0" + String.valueOf(hour) : String.valueOf(hour);
        Log.d(TAG, "getTime: hour = " + hour + ",minus = " + minus);
        return temp + minus + "0" + "00";
    }

    @SuppressLint("SimpleDateFormat")
    private static String dateFormat(long nowDate, String format) {
        Date date = new Date(nowDate);
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }


    public static long getFileLengthFromUrl(String url) throws IOException {
        if (url != null && !"".equals(url)) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();
            if (response != null && response.isSuccessful()) {
                long length = response.body().contentLength();
                response.body().close();
                return length;
            }
        }
        return 0L;
    }


    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}

