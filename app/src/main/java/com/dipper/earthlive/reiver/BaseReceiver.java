package com.dipper.earthlive.reiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


/**
 * @author Dipper
 * @date 2018/12/1
 * @email dipper.difference@gmail.com
 */
public class BaseReceiver extends BroadcastReceiver {
    private final String TAG = "BaseReceiver";
    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.mContext = context;
        String action = intent.getAction();
        action = action == null ? "" : action;
        if (Intent.ACTION_TIME_TICK.equals(action)) {

        } else if (Intent.ACTION_SCREEN_ON.equals(action)) {

        }
    }

}
