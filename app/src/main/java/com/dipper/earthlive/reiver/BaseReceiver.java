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
