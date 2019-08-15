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

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.widget.ListView;

import com.dipper.earthlive.R;
import com.dipper.earthlive.util.HelpAdapter;
import com.dipper.earthlive.module.Problem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Dipper
 * @date 2018/11/20
 * @email dipper.difference@gmail.com
 */
public class HelpActivity extends Activity {
    public static final String TAG = "HelpActivity";
    private Context mContext;
    private List<Problem> mList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getActionBar()).setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_help);
        mContext = this;
        initView();
    }

    private void initView() {
        initData();
        HelpAdapter mAdapter = new HelpAdapter(mContext, mList);
        ListView mHelpList = findViewById(R.id.list_problems);
        mHelpList.setAdapter(mAdapter);
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

    /**
     * 模拟数据
     */
    private void initData() {
        if (mList == null) {
            mList = new ArrayList<>();
        }
        for (int i = 0; i < 20; i++) {
            Problem problem = new Problem();
            problem.setTitle("壁纸一片漆黑？");
            problem.setAnswer("刷新试试。");
            mList.add(problem);
        }
    }
}
