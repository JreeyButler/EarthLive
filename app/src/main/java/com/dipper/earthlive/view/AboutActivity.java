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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dipper.earthlive.R;
import com.dipper.earthlive.util.Constants;

import java.util.Objects;

/**
 * @author Dipper
 * @date 2018/12/10
 * @email dipper.difference@gmail.com
 */
public class AboutActivity extends Activity implements View.OnClickListener, DialogInterface.OnCancelListener {
    private final String TAG = "AboutActivity";
    private Context mContext;

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
        String version = String.format(mContext.getResources().getString(R.string.version), getVersionNumber());
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
    private String getVersionNumber() {
        PackageManager manager = mContext.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
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
                startActivity(intent);
                break;
            case R.id.check_update:
                showUpdateDialog();
                break;
            default:
                break;
        }
    }

    /**
     *
     */
    private void showUpdateDialog() {
        ProgressDialog dialog = ProgressDialog.show(
                mContext,
                mContext.getResources().getString(R.string.check_update),
                mContext.getResources().getString(R.string.checking),
                true,
                true,
                this);
    }

    @Override
    public void onCancel(DialogInterface dialogInterface) {

    }
}
