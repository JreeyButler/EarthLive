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

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import com.dipper.earthlive.R;
import com.dipper.earthlive.service.WallpaperService;
import com.dipper.earthlive.util.Constants;
import com.dipper.earthlive.util.Tools;


import java.io.File;

/**
 * @author Dipper
 * @date 2018/11/20
 * @email dipper.difference@gmail.com
 */
public class MainActivity extends Activity implements View.OnClickListener {
    private final String TAG = "MainActivity";
    private ImageView wallpaper, renew;
    private Context mContext;
    private WallpaperService mWallpaperService;
    private boolean isServiceConnected;
    private boolean exitReady;

    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        initView();
        checkPermission();
    }

    private void checkPermission() {
        if (!Tools.hasPermissionGrated(mContext, permissions)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissions, 1);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!Tools.hasPermissionGrated(mContext, permissions)) {
            return;
        }
        initApplicationDir();
        renewWallpaper();
        initReceiver();
        initService();
    }

    private void initService() {
        if (!Tools.isServiceRunning(mContext, WallpaperService.class.getName()) && isAutoUpdate()) {
            Log.d(TAG, "initService: not running");
            startService(new Intent(mContext, WallpaperService.class));
        } else {
            Log.d(TAG, "initService: running");
            bindService(new Intent(mContext, WallpaperService.class), connection, Context.BIND_AUTO_CREATE);
        }
    }

    private void initReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_CHANGE_WALLPAPER_SUCCESS);
        filter.addAction(Constants.ACTION_WALLPAPER_NO_UPDATE);
        mContext.registerReceiver(receiver, filter);
    }


    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
        unregisterReceiver(receiver);
        if (isServiceConnected) {
            unbindService(connection);
        }
        if (!isAutoUpdate()) {
            Log.d(TAG, "onDestroy: stop service");
            stopService(new Intent(this, WallpaperService.class));
        }
        mHandler.removeCallbacksAndMessages(null);
    }

    private void initApplicationDir() {
        File file = new File(Constants.PICTURE_DIR_PATH);
        boolean status = true;
        if (!file.exists()) {
            status = file.mkdirs();
        }
        Log.d(TAG, "initApplicationDir: create picture dir " + (status ? "success" : "fail"));
        file = new File(Constants.WALLPAPER_DIR_PATH);
        if (!file.exists()) {
            status = file.mkdirs();
        }
        Log.d(TAG, "initApplicationDir: create wallpaper dir " + (status ? "success" : "fail"));
    }


    private void initView() {
        Button change = findViewById(R.id.change);
        Button display = findViewById(R.id.display);
        Button setWallpaper = findViewById(R.id.set_wallpaper);
        Toolbar mToolbar = findViewById(R.id.toolbar);
        renew = findViewById(R.id.renew);
        wallpaper = findViewById(R.id.wallpaper);
        mToolbar.inflateMenu(R.menu.menu_main);
        change.setOnClickListener(this);
        display.setOnClickListener(this);
        setWallpaper.setOnClickListener(this);
        renew.setOnClickListener(this);
        mToolbar.setOnMenuItemClickListener(menuItemClickListener);
    }

    /**
     * 刷新壁纸
     */
    @SuppressLint("CheckResult")
    private void renewWallpaper() {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.skipMemoryCache(true);
        requestOptions.dontAnimate();

        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);

        Log.d(TAG, "renewWallpaper: wallpaper exists = " + Tools.isWallpaperExists());
        if (Tools.isWallpaperExists()) {
            Glide.with(mContext)
                    .load(new File(Constants.PICTURE_DIR_PATH + Constants.DEFAULT_NAME + Constants.NORMAL_PICTURE_STUFF))
                    .apply(requestOptions)
                    .into(wallpaper);
        } else {
            Glide.with(mContext).load(R.drawable.defaula_wallpaper_japan).into(wallpaper);
        }
    }

    /**
     * 获取本地下载图片位置
     *
     * @return 图片位置
     */
    private String getPicturePath() {
        String dataFrom = Tools.getStringSharePreference(mContext, Constants.Key.KEY_DATA_FROM, mContext.getResources().getString(R.string.config_data_from));

        if (dataFrom.equals(mContext.getResources().getString(R.string.value_japan))) {
            return Constants.PICTURE_DIR_PATH + Constants.JAPAN_NAME + Constants.DEFAULT_PICTURE_STUFF;
        } else if (dataFrom.equals(mContext.getResources().getString(R.string.value_china))) {
            return Constants.PICTURE_DIR_PATH + Constants.CHINA_NAME + Constants.NORMAL_PICTURE_STUFF;
        } else if (dataFrom.equals(mContext.getResources().getString(R.string.value_usa))) {
            // 暂时先返回日本的节点
            return Constants.PICTURE_DIR_PATH + Constants.USA_NAME + Constants.DEFAULT_PICTURE_STUFF;
        }
        return Constants.PictureUrl.JAPAN_NORMAL_URL + Tools.getLatestPictureTime();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.set_wallpaper:
                mContext.sendBroadcast(new Intent(Constants.ACTION_SET_WALLPAPER));
                break;
            case R.id.renew:
                Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.anim_rotate);
                renew.startAnimation(animation);
                Log.d(TAG, "onClick: isServiceConnected = " + isServiceConnected);

                Intent intent = new Intent();
                intent.setAction(Constants.ACTION_RENEW_WALLPAPER);
                mContext.sendBroadcast(intent);
                break;
            default:
                break;
        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "onReceive: action = " + action);
            action = action == null ? "" : action;
            if (Constants.ACTION_CHANGE_WALLPAPER_SUCCESS.equals(action)) {
                renewWallpaper();
            } else if (Constants.ACTION_WALLPAPER_NO_UPDATE.equals(action)) {
                Log.d(TAG, "onReceive: no update,do nothing");
//                renewWallpaper();
            } else if (Constants.ACTION_WALLPAPER_DOWNLOAD_FAILED.equals(action)) {
                Tools.showToast(mContext, "壁纸更新失败，请检查网络设置。");
                renewWallpaper();
            }
            mHandler.sendEmptyMessageDelayed(Constants.MSG_CLEAR_RENEW_ANIMATION, 1000);
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constants.MSG_TEST_DISPLAY:
                    break;
                case Constants.MSG_DISPLAY_WALLPAPER:
                    Glide.with(mContext)
                            .load(new File(Constants.WALLPAPER_PATH))
                            .into(wallpaper);
                    break;
                case Constants.MSG_CLEAR_RENEW_ANIMATION:
                    renew.clearAnimation();
                    break;
                case Constants.MSG_EXIT_READY:
                    exitReady = false;
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult: request permission failed , finish activity now.");
                showNoPermissionDialog();

            }
        }
    }

    private void showNoPermissionDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        dialog.setTitle(R.string.no_permission_title);
        dialog.setMessage(R.string.no_permission_message);
        dialog.setNegativeButton(R.string.no_permission_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        dialog.show();
    }

    private Toolbar.OnMenuItemClickListener menuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            int itemId = menuItem.getItemId();
            switch (itemId) {
                case R.id.settings:
                    mContext.startActivity(new Intent(mContext, Settings.class));
                    break;
                case R.id.about:
                    mContext.startActivity(new Intent(mContext, AboutActivity.class));
                    break;
                default:
                    break;
            }
            return false;
        }
    };

    private boolean isAutoUpdate() {
        return Tools.getBooleanSharePreference(mContext,
                Constants.Key.KEY_AUTO_UPDATE,
                mContext.getResources().getBoolean(R.bool.config_auto_update));
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(TAG, "onServiceConnected: ");
            mWallpaperService = ((WallpaperService.Builder) iBinder).build();
            isServiceConnected = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG, "onServiceDisconnected: ");
            mWallpaperService = null;
            isServiceConnected = false;
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!exitReady) {
                Toast.makeText(mContext, mContext.getResources().getString(R.string.exit_ready), Toast.LENGTH_SHORT).show();
                exitReady = true;
                mHandler.sendEmptyMessageDelayed(Constants.MSG_EXIT_READY, 2000);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}

