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

package com.dipper.earthlive.notification;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NavUtils;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


import com.dipper.earthlive.util.Constants;
import com.dipper.earthlive.util.SpUtil;
import com.dipper.earthlive.util.Tools;
import com.dipper.earthlive.view.MainActivity;
import com.dipper.earthlive.R;

/**
 * @author Dipper.Stephen
 * @date 2018/12/10
 * @email dipper.difference@gmail.com
 */
public class BaseNotification {
    private final String TAG = "BaseNotification";
    private Context mContext;
    private NotificationManager mManager;
    private Notification mNotification;

    private static final String NOTIFICATION_CHANNEL_ID = "Test";
    private static final String NOTIFICATION_CHANNEL_NAME = "Test";

    public BaseNotification(Context context) {
        this.mContext = context;
    }

    @SuppressLint("StringFormatMatches")
    public void showNotification(String message) {
        Log.d(TAG, "showNotification: ");
        String dataFrom = getDataFrom();
        String updateCycle = getUpdateCycle();
        String msg = String.format(getStringFromRes(R.string.notification_message), dataFrom, updateCycle);
        if (mManager == null) {
            // 获取通知服务
            mManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        Intent intent = new Intent(mContext, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            Notification.Builder builder = new Notification.Builder(mContext)
                    .setSmallIcon(R.drawable.icon_notification)
                    .setContentTitle(getStringFromRes(R.string.notification_title))
                    .setContentText(message == null ? msg : message);
            // 设置跳转Intent到通知中
            builder.setContentIntent(pendingIntent);
            // 构建通知
            mNotification = builder.build();
            mNotification.flags = Notification.FLAG_NO_CLEAR;
        } else {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
            mManager.createNotificationChannel(channel);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.icon_notification)
                    .setContentTitle(getStringFromRes(R.string.notification_title))
                    .setContentText(message == null ? msg : message);
            builder.setContentIntent(pendingIntent);
            // 构建通知
            mNotification = builder.build();
            mNotification.flags = Notification.FLAG_NO_CLEAR;
        }

        // 显示通知
        if (mManager != null) {
            mManager.notify(Constants.NOTIFICATION_ID, mNotification);
        }
    }


    public void cancelNotification() {
        Log.d(TAG, "cancelNotification: ");
        if (mManager == null) {
            mManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        mManager.cancel(Constants.NOTIFICATION_ID);
    }

    public Notification getNotification() {
        return this.mNotification;
    }

    /**
     * 获取数据来源
     *
     * @return 壁纸数据来源
     */
    private String getDataFrom() {
        String value = SpUtil.getInstance().getStringSharePreference(
                Constants.Key.KEY_DATA_FROM,
                getStringFromRes(R.string.value_japan));
        if (value.equals(getStringFromRes(R.string.value_japan))) {
            return getStringFromRes(R.string.japan);
        } else if (value.equals(getStringFromRes(R.string.value_china))) {
            return Tools.getStringFromRes(mContext, R.string.china);
        } else if (value.equals(getStringFromRes(R.string.value_usa))) {
            return getStringFromRes(R.string.usa);
        }
        return getStringFromRes(R.string.japan);
    }

    /**
     * 获取更新周期
     *
     * @return 更新周期
     */
    private String getUpdateCycle() {
        String value = SpUtil.getInstance().getStringSharePreference(
                Constants.Key.KEY_UPDATE_CYCLE,
                getStringFromRes(R.string.value_10_minus));
        if (value.equals(getStringFromRes(R.string.value_10_minus))) {
            return getStringFromRes(R.string.minus_10);
        } else if (value.equals(getStringFromRes(R.string.value_30_minus))) {
            return getStringFromRes(R.string.minus_30);
        } else if (value.equals(getStringFromRes(R.string.value_60_minus))) {
            return getStringFromRes(R.string.minus_60);
        }
        return getStringFromRes(R.string.minus_10);
    }

    private String getStringFromRes(int resId) {
        if (mContext == null) {
            return "";
        }
        return mContext.getResources().getString(resId);
    }
}
