package com.dipper.earthlive.notification;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


import com.dipper.earthlive.util.Constants;
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

    public BaseNotification(Context context) {
        this.mContext = context;
    }

    @SuppressLint("StringFormatMatches")
    public void showNotification(String message) {
        Log.d(TAG, "showNotification: ");
        String dataFrom = getDataFrom();
        String updateCycle = getUpdateCycle();
        String msg = String.format(mContext.getResources().getString(R.string.notification_message), dataFrom, updateCycle);

        Notification.Builder builder = new Notification.Builder(mContext)
                .setSmallIcon(R.mipmap.ic_icon)
                .setContentTitle(mContext.getResources().getString(R.string.notification_title))
                .setContentText(message == null ? msg : message);

        Intent intent = new Intent(mContext, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        // 设置跳转Intent到通知中
        builder.setContentIntent(pendingIntent);
        // 获取通知服务
        NotificationManager manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        // 构建通知
        mNotification = builder.build();
        mNotification.flags = Notification.FLAG_NO_CLEAR;
        // 显示通知
        if (manager != null) {
            manager.notify(Constants.NOTIFICATION_ID, mNotification);
        }
    }


    public void cancelNotification() {
        Log.d(TAG, "cancelNotification: ");
        if (mManager == null) {
            mManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        if (mManager != null) {
            mManager.cancel(Constants.NOTIFICATION_ID);
        }
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
        String value = Tools.getStringSharePreference(mContext,
                Constants.Key.KEY_DATA_FROM,
                mContext.getResources().getString(R.string.value_japan));
        if (value.equals(mContext.getResources().getString(R.string.value_japan))) {
            return mContext.getResources().getString(R.string.japan);
        } else if (value.equals(mContext.getResources().getString(R.string.value_china))) {
            return mContext.getResources().getString(R.string.china);
        } else if (value.equals(mContext.getResources().getString(R.string.value_usa))) {
            return mContext.getResources().getString(R.string.usa);
        }
        return mContext.getResources().getString(R.string.japan);
    }

    /**
     * 获取更新周期
     *
     * @return 更新周期
     */
    private String getUpdateCycle() {
        String value = Tools.getStringSharePreference(mContext,
                Constants.Key.KEY_UPDATE_CYCLE,
                mContext.getResources().getString(R.string.value_10_minus));
        if (value.equals(mContext.getResources().getString(R.string.value_10_minus))) {
            return mContext.getResources().getString(R.string.minus_10);
        } else if (value.equals(mContext.getResources().getString(R.string.value_30_minus))) {
            return mContext.getResources().getString(R.string.minus_30);
        } else if (value.equals(mContext.getResources().getString(R.string.value_60_minus))) {
            return mContext.getResources().getString(R.string.minus_60);
        }
        return mContext.getResources().getString(R.string.minus_10);
    }
}