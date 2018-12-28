/*
 * File Name: WallPaperService1.java
 * Author: dipper
 * Mail: yanliang.difference@gmail.com
 * Created Time: 2018年11月28日 16:27:52
 */
package com.dipper.earthlive.service;

import android.annotation.SuppressLint;


import android.app.Service;

import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Size;

import com.dipper.earthlive.R;
import com.dipper.earthlive.notification.BaseNotification;
import com.dipper.earthlive.util.Constants;
import com.dipper.earthlive.util.DownloadCallback;
import com.dipper.earthlive.util.DownloadTask;
import com.dipper.earthlive.util.Tools;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;

/**
 * @author Dipper
 * @date 2018/11/20
 * @email dipper.difference@gmail.com
 */
public class WallpaperService extends Service {
    private final String TAG = "WallpaperService";

    private BaseNotification mNotification;
    private WallpaperManager manager;
    private ExecutorService downloadService;
    private Context mContext;
    private String latestTime;

    private boolean isDownloading;
    private int downloadCount, successCount;
    private long nowTime;


    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: ");
        super.onCreate();
        mContext = this;
        initReceiver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        downloadCount = 0;
        Log.d(TAG, "onStartCommand: ");
        if (manager == null) {
            manager = WallpaperManager.getInstance(mContext);
        }
        if (isAutoUpdate()) {
            showNotification(null);
            if (mNotification != null) {
                startForeground(Constants.NOTIFICATION_ID, mNotification.getNotification());
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void initReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);

        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        intentFilter.addAction(Constants.ACTION_RENEW_WALLPAPER);
        registerReceiver(mReceiver, intentFilter);
    }

    private void setLockWallpaper(int resId) {
        // 获取类名
        Class class1 = manager.getClass();
        try {
            // 获取设置锁屏壁纸的函数
            Method setWallPaperMethod = class1.getMethod("setBitmapToLockWallpaper", Bitmap.class);
            // 调用锁屏壁纸的函数
            setWallPaperMethod.invoke(manager, BitmapFactory.decodeResource(getResources(), resId));
            Log.d(TAG, "setLockWallpaper: set lock wallpaper success!");
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            Log.e(TAG, "setLockWallpaper: set lock wallpaper failed", e);
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: ");
        if (manager == null) {
            manager = WallpaperManager.getInstance(mContext);
        }
        return new Builder();
    }

    public class Builder extends Binder {
        public WallpaperService build() {
            return WallpaperService.this;
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
        if (mNotification == null) {
            mNotification = new BaseNotification(mContext);
        }
        if (!isAutoUpdate()) {
            mNotification.cancelNotification();
        }
        if (mReceiver != null) {
            mContext.unregisterReceiver(mReceiver);
        }
    }

    private void noUpdate() {
        Log.d(TAG, "noUpdate: ");
        mContext.sendBroadcast(new Intent(Constants.ACTION_WALLPAPER_NO_UPDATE));
    }


    /**
     * 检查是否存在更新
     *
     * @return 更新状态
     */
    private boolean checkUpdate() {
        String latest = getLatestTime();
        String now = Tools.getLatestPictureTime();
        Log.d(TAG, "checkUpdate: latest time = " + latest + ", now = " + now);
        return "".equals(latest) || !now.equals(latest);
    }

    /**
     * 检查更新时间是否满足设置的更新周期
     *
     * @return true:满足用户设置的更新周期
     */
    private boolean checkUpdateTime() {
        nowTime = System.currentTimeMillis();
        long realLatestTime = getRealUpdateTime();
        String temp = Tools.getStringSharePreference(mContext, Constants.Key.KEY_UPDATE_CYCLE, mContext.getResources().getString(R.string.config_update_cycle));
        long cycle = Long.valueOf(temp) * 60 * 1000;
        Log.d(TAG, "checkUpdateTime: cycle = " + cycle + ", now = " + (nowTime - realLatestTime));
        return ((nowTime - realLatestTime) >= cycle);
    }

    /**
     * 获取最后更新时间
     */
    private String getLatestTime() {
        final String defaultValue = "0";
        if (mContext == null) {
            Log.e(TAG, "getLatestTime: context is null");
            return defaultValue;
        }
        return Tools.getStringSharePreference(mContext, Constants.KEY_LATEST_PICTURE_TIME, defaultValue);
    }

    /**
     * 获取最后更新时间
     *
     * @return 壁纸最后更新的时间
     */
    private long getRealUpdateTime() {
        final long defaultValue = 0L;
        return Tools.getLongSharePreference(mContext, Constants.KEY_LATEST_UPDATE_TIME, defaultValue);
    }

    /**
     * 保存最后更新时间
     */
    private void saveLastUpdateTime() {
        if (nowTime == 0L) {
            Log.d(TAG, "saveLastUpdateTime: error, time is null");
            return;
        }
        Log.d(TAG, "saveLastUpdateTime: nowTime =" + nowTime);
        Tools.setLongSharePreference(mContext, Constants.KEY_LATEST_UPDATE_TIME, nowTime);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            action = action == null ? "" : action;
            Log.d(TAG, "onReceive: action = " + action);
            if (Intent.ACTION_TIME_TICK.equals(action)) {
                if (isAutoUpdate() && checkUpdateTime() && checkUpdate()) {
                    latestTime = Tools.getLatestPictureTime();
                    updateWallpaper();
                }
            } else if (Intent.ACTION_SCREEN_ON.equals(action)) {
                if (isAutoUpdate() && checkUpdateTime() && checkUpdate()) {
                    latestTime = Tools.getLatestPictureTime();
                    updateWallpaper();
                }
            } else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                if (isAutoUpdate() && checkUpdateTime() && checkUpdate()) {
                    latestTime = Tools.getLatestPictureTime();
                    updateWallpaper();
                }
            } else if (Constants.ACTION_RENEW_WALLPAPER.equals(action)) {
                if (checkUpdate()) {
                    latestTime = Tools.getLatestPictureTime();
                    updateWallpaper();
                    return;
                }
                noUpdate();
            }
        }
    };

    private void updateWallpaper() {
        Log.d(TAG, "updateWallpaper: ");
        String[] urls = getPictureUrls();
        if (urls == null) {
            return;
        }
        // 正在下载
        if (isDownloading) {
            Log.d(TAG, "updateWallpaper: isDownloading");
            return;
        }
        // 无网络
        if (!Tools.isNetworkUseful(mContext)) {
            Log.e(TAG, "updateWallpaper: network error");
            showNotification(mContext.getResources().getString(R.string.network_error));
            return;
        }
        // 下载图片
        for (String url : urls) {
            startDownload(url);
        }
    }

    private void startDownload(String url) {
        Log.d(TAG, "startDownload: url = " + url);
        if (downloadService == null) {
            downloadService = Tools.getThreadPool(4, 8);
        }
        downloadService.submit(new DownloadTask(url, mCallBack));
        isDownloading = true;
    }


    private DownloadCallback mCallBack = new DownloadCallback() {

        @Override
        public void downloadSucceed(String fileName) {
            downloadCount++;
            successCount++;
            Log.d(TAG, "downloadSucceed: fileName = " + fileName);
            Log.d(TAG, "downloadSucceed: downloadCount = " + downloadCount + ",successCount = " + successCount);
            // 没下载完成，继续下载
            if (downloadCount < Constants.SIZE_WALLPAPER) {
                return;
            }
            isDownloading = false;
            downloadCount = 0;
            // 存在没有完全下载的图片
            if (successCount < Constants.SIZE_WALLPAPER) {
                successCount = 0;
                mContext.sendBroadcast(new Intent(Constants.ACTION_WALLPAPER_DOWNLOAD_FAILED));
                return;
            }

            Tools.generateWallpaper(getWallpaperSize());
            try {
                setHomeWallpaper();
                successCount = 0;
            } catch (Exception e) {
                e.printStackTrace();
            }
            setLatestTime(latestTime);
            saveLastUpdateTime();
            mContext.sendBroadcast(new Intent(Constants.ACTION_CHANGE_WALLPAPER_SUCCESS));

        }

        @Override
        public void downloadFailed(String fileName) {
            downloadCount++;
            if (downloadCount >= Constants.SIZE_WALLPAPER) {
                isDownloading = false;
                downloadCount = 0;
                successCount = 0;
                mContext.sendBroadcast(new Intent(Constants.ACTION_WALLPAPER_DOWNLOAD_FAILED));
            }
            Log.d(TAG, "downloadFailed: file name = " + fileName);
        }
    };

    /**
     * 设置手机壁纸
     *
     * @throws IOException
     */
    private void setHomeWallpaper() throws IOException {
        Bitmap wallpaper = BitmapFactory.decodeFile(Constants.WALLPAPER_PATH);
        if (wallpaper == null) {
            Log.d(TAG, "setHomeWallpaper: wallpaper is null");
            return;
        }
        manager.setBitmap(wallpaper);
    }
    
    private void setLatestTime(String time) {
        time = time == null ? "" : time;
        Tools.setStringSharePreference(mContext, Constants.KEY_LATEST_PICTURE_TIME, time);
    }

    @SuppressLint("StringFormatMatches")
    private void showNotification(String message) {
        if (mNotification == null) {
            mNotification = new BaseNotification(mContext);
        }
        mNotification.showNotification(message);
    }

    /**
     * 检查是否开启了自动更新
     *
     * @return true:开启；false:未开启
     */
    private boolean isAutoUpdate() {
        return Tools.getBooleanSharePreference(mContext,
                Constants.Key.KEY_AUTO_UPDATE,
                mContext.getResources().getBoolean(R.bool.config_auto_update));
    }

    /**
     * 获取壁纸尺寸
     *
     * @return 壁纸尺寸
     */
    private Size getWallpaperSize() {
        Size size = new Size(Constants.SIZE_720P_WIDTH, Constants.SIZE_720P_HEIGHT);
        String value = Tools.getStringSharePreference(mContext,
                Constants.Key.KEY_WALLPAPER_SIZE,
                mContext.getResources().getString(R.string.config_wallpaper_size));
        if (value.equals(mContext.getResources().getString(R.string.value_720p))) {
            return size;
        } else if (value.equals(mContext.getResources().getString(R.string.value_1080p))) {
            return new Size(Constants.SIZE_1080P_WIDTH, Constants.SIZE_1080P_HEIGHT);
        }
        return size;
    }

    /**
     * 获取图片地址
     *
     * @return 图片地址
     */
    private String[] getPictureUrls() {
        String api = getApi();
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
     * 通过用户设置的数据来源获取API
     *
     * @return data api
     */
    private String getApi() {
        String dataFrom = Tools.getStringSharePreference(mContext, Constants.Key.KEY_DATA_FROM, mContext.getResources().getString(R.string.config_data_from));
        if (dataFrom.equals(mContext.getResources().getString(R.string.value_japan))) {
            return Constants.PictureUrl.JAPAN_NORMAL_URL + Tools.getLatestPictureTime();
        } else if (dataFrom.equals(mContext.getResources().getString(R.string.value_china))) {
            return Constants.PictureUrl.CHINA_URL + Constants.TEST_PICTURE_NAME;
        } else if (dataFrom.equals(mContext.getResources().getString(R.string.value_usa))) {
            // 暂时先返回日本的节点
            return Constants.PictureUrl.JAPAN_NORMAL_URL + Tools.getLatestPictureTime();
        }
        return Constants.PictureUrl.JAPAN_NORMAL_URL + Tools.getLatestPictureTime();
    }

}

