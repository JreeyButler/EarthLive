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
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dipper.earthlive.R;
import com.dipper.earthlive.model.VersionInfo;
import com.dipper.earthlive.util.Constants;
import com.dipper.earthlive.util.DownloadCallback;
import com.dipper.earthlive.util.DownloadTask;
import com.dipper.earthlive.util.Tools;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author Dipper
 * @date 2018/12/10
 * @email dipper.difference@gmail.com
 */
public class AboutActivity extends Activity implements View.OnClickListener,
        DialogInterface.OnCancelListener, DownloadCallback {
    private final String TAG = "AboutActivity";
    private Context mContext;
    private ExecutorService mUpdateThread;
    private Future mUpdateFuture;
    private ProgressDialog checkUpdateDialog;
    private final int NEW_VERSION_RELEASED = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getActionBar()).setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_about);
        mContext = this;
        initView();
    }

    @SuppressLint("StringFormatInvalid")
    private void initView() {
        TextView mVersion = findViewById(R.id.app_version);
        Button mFeedback = findViewById(R.id.feedback);
        Button mCheckUpdate = findViewById(R.id.check_update);
        String version = String.format(mContext.getResources().getString(R.string.version), getVersionName());
        Log.d(TAG, "initView: version = " + version);
        mVersion.setText(version);
        mFeedback.setOnClickListener(this);
        mCheckUpdate.setOnClickListener(this);
    }


    /**
     * 获取软件版本号
     *
     * @return 软件版本号
     */
    private String getVersionName() {
        PackageManager manager = mContext.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    private int getVersionCode() {
        PackageManager manager = mContext.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            if (info == null) {
                return 0;
            }
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
        mHandle.removeCallbacksAndMessages(null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.feedback:
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:" + Constants.FEEDBACK_EMAIL));
                intent.putExtra(Intent.EXTRA_SUBJECT, mContext.getResources().getString(R.string.email_title));
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Log.e(TAG, "onClick: no email client, show dialog");
                    e.printStackTrace();
                    showNoEmailClientDialog();
                }
                break;
            case R.id.check_update:
                showUpdateDialog();
                checkUpdate();
                break;
            default:
                break;
        }
    }

    private void showNoEmailClientDialog() {
        AlertDialog dialog = new AlertDialog.Builder(mContext)
                .setTitle(R.string.no_email_client_title)
                .setMessage(R.string.no_email_client_message)
                .setCancelable(true)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(R.string.feedback_github, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String githubIssueUri = "https://github.com/JreeyButler/EarthLive/issues";
                        Uri uri = Uri.parse(githubIssueUri);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                })
                .create();
        dialog.show();
    }

    private void checkUpdate() {
        if (mUpdateThread == null) {
            mUpdateThread = Tools.getSignalThreadPool();
        }
        mUpdateFuture = mUpdateThread.submit(new UpdateTask());
    }

    /**
     * 显示检查升级的Dialog
     */
    private void showUpdateDialog() {
        checkUpdateDialog = ProgressDialog.show(
                mContext,
                mContext.getResources().getString(R.string.check_update),
                mContext.getResources().getString(R.string.checking),
                true,
                true,
                this);
    }

    @Override
    public void onCancel(DialogInterface dialogInterface) {
        if (mUpdateFuture == null) {
            Log.e(TAG, "onCancel: update thread not running , do nothing");
            return;
        }
        Log.d(TAG, "onCancel: update thread cancel now");
        mUpdateFuture.cancel(true);
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == NEW_VERSION_RELEASED) {
                if (checkUpdateDialog.isShowing()) {
                    checkUpdateDialog.dismiss();
                }
                String url = String.valueOf(msg.obj);
                showNewVersionReleasedDialog(url);
            }
        }
    };

    /**
     * 显示发现新版的Dialog
     *
     * @param url 新版本的下载链接
     */
    private void showNewVersionReleasedDialog(final String url) {
        AlertDialog dialog = new AlertDialog.Builder(mContext)
                .setTitle(R.string.new_application_released)
                .setMessage(R.string.released_message)
                .setPositiveButton(R.string.downlaod_now, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mUpdateThread.submit(new DownloadTask(url, Constants.RELEASE_APK_DOWNLOAD_PATH, AboutActivity.this));
                    }
                })
                .setNegativeButton(R.string.next_time, null)
                .show();

    }

    @Override
    public void downloadSucceed(String fileName) {

    }

    @Override
    public void downloadFailed(String fileName) {

    }

    private class UpdateTask implements Runnable {
        final String url = Constants.PictureUrl.TEST_UPDATE_JSON_URL + "output.json";

        @Override
        public void run() {
            Log.d(TAG, "run: ");
            OkHttpClient client = new OkHttpClient();
            Log.d(TAG, "run: url = " + url);
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = null;
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (response == null) {
                Log.e(TAG, "getMessageFromServer: response is null");
                return;
            }
            if (response.body() == null) {
                Log.e(TAG, "run: response.body() == null");
                return;
            }
            String versionInfoJson = null;
            try {
                versionInfoJson = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            response.body().close();
            if (versionInfoJson == null) {
                Log.e(TAG, "run: versionInfoJson is null");
                return;
            }
            VersionInfo info = analysisJson(versionInfoJson);
            Log.d(TAG, "run: info = " + info.toString());
            if (getVersionCode() < info.getApkData().getVersionCode()) {
                String fileName = info.getApkData().getOutputFile();
                Message msg = mHandle.obtainMessage();
                msg.what = NEW_VERSION_RELEASED;
                msg.obj = Constants.PictureUrl.TEST_UPDATE_JSON_URL + fileName;
                mHandle.sendMessage(msg);
            }
        }

        private VersionInfo analysisJson(String json) {
            Log.d(TAG, "analysisJson: json =" + json);
            return new Gson().fromJson(json, VersionInfo.class);
        }
    }


}
