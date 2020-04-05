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
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.dipper.earthlive.R;
import com.dipper.earthlive.util.Constants;

import java.util.Objects;

/**
 * @author Dipper
 * @date 2018/12/27
 */
public class OpenSourceActivity extends Activity implements View.OnClickListener {
    private final String TAG = "OpenSourceActivity";
    private final int GITEE_INDEX = 0;
    private final int GITHUB_INDEX = 1;
    private TextView mGitee;
    private TextView mGithub;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getActionBar()).setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_open_source);
        initView();
    }

    private void initView() {
        TextView mInformation = findViewById(R.id.information);
        mGitee = findViewById(R.id.url_gitee);
        mGithub = findViewById(R.id.url_github);
        mGitee.setText(Constants.REPOSITORIES_URL[GITEE_INDEX]);
        mGithub.setText(Constants.REPOSITORIES_URL[GITHUB_INDEX]);
        mGitee.setOnClickListener(this);
        mGithub.setOnClickListener(this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
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
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri;
        switch (view.getId()) {
            case R.id.url_gitee:
                uri = Uri.parse(mGitee.getText().toString());
                intent.setData(uri);
                startActivity(intent);
                break;
            case R.id.url_github:
                uri = Uri.parse(mGithub.getText().toString());
                intent.setData(uri);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
