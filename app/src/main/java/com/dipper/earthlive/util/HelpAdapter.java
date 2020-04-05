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

package com.dipper.earthlive.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.dipper.earthlive.R;
import com.dipper.earthlive.bean.Problem;

import java.util.List;

/**
 * @author Dipper
 * @date 2018/12/27
 */
public class HelpAdapter extends BaseAdapter {
    private final String TAG = "HelpAdapter";
    private Context mContext;
    private List<Problem> mList;
    private Holder holder;
    private boolean[] showControl;

    public HelpAdapter(Context context) {
        this.mContext = context;
        holder = new Holder();
    }

    public HelpAdapter(Context mContext, List<Problem> mList) {
        this.mContext = mContext;
        this.mList = mList;
        showControl = new boolean[mList.size()];
        holder = new Holder();
    }

    /**
     * 绑定数据
     *
     * @param dataList 数据列表
     */
    public void bindData(List<Problem> dataList) {
        this.mList = dataList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mList == null) {
            return 0;
        }
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.view_problem, null);
        holder.mTitle = convertView.findViewById(R.id.problem_title);
        holder.mAnswer = convertView.findViewById(R.id.problem_answer);
        holder.mTitle.setText(mList.get(position).getTitle());
        holder.mAnswer.setText(mList.get(position).getAnswer());
        if (showControl[position]) {
            holder.mAnswer.setVisibility(View.VISIBLE);
        } else {

            holder.mAnswer.setVisibility(View.GONE);
        }
        convertView.setTag(position);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tag = (Integer) v.getTag();
                showControl[tag] = !showControl[tag];
                notifyDataSetChanged();
            }
        });
        return convertView;
    }


    private class Holder {
        TextView mTitle;
        TextView mAnswer;
    }
}
