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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.dipper.earthlive.R;
import com.dipper.earthlive.service.WallpaperService;
import com.dipper.earthlive.util.Tools;

/**
 * @author Dipper
 * @date 2018/3/7
 */
public class WelcomeActivity extends Activity {
    private final String TAG = "WelcomeActivity";
    private final int EXIT = 1;
    private Context mContext;

    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        mContext = this;
        if (Tools.isServiceRunning(mContext, WallpaperService.class.getName())) {
            mHandler.sendEmptyMessage(EXIT);
        } else {
            Log.d(TAG, "onCreate: service not running," + WallpaperService.class.getName());
        }
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
        if (!Tools.isServiceRunning(mContext, WallpaperService.class.getName())) {
            mHandler.sendEmptyMessageDelayed(EXIT, 2000);
        }
    }

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case EXIT:
                    Intent intent = new Intent(mContext, MainActivity.class);
                    mContext.startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: ");
        super.onPause();
        finish();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }
}
