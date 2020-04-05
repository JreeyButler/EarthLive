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

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author Dipper
 * @date 2018/11/23
 * @email dipper.difference@gmail.com
 */
public class DownloadTask implements Runnable {
    private final String TAG = "DownloadTask";
    private String url;
    private DownloadCallback mCallback;
    private String path;

    public DownloadTask(String url, String path, DownloadCallback callback) {
        this.url = url;
        this.path = path;
        this.mCallback = callback;
    }

    @Override
    public void run() {
        InputStream inputStream = null;
        String fileName = getFileName();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.body() == null) {
                Log.e(TAG, "run: response.body() is null ");
                return;
            }
            inputStream = response.body().byteStream();
            if (path == null || "".equals(path)) {
                mCallback.downloadFailed(fileName);
                return;
            }
            RandomAccessFile randomAccessFile = new RandomAccessFile(path + fileName, "rw");
            byte[] bytes = new byte[1024];
            int length;
            while ((length = inputStream.read(bytes)) != -1) {
                randomAccessFile.write(bytes, 0, length);
            }
        } catch (Exception e) {
            mCallback.downloadFailed(fileName);
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                    mCallback.downloadSucceed(fileName);
                } catch (IOException e) {
                    e.printStackTrace();
                    mCallback.downloadFailed(fileName);
                }
            }
        }
    }

    /**
     * 获取下载文件的名称
     *
     * @return 下载文件的名称
     */
    private String getFileName() {
        final String flag = "/";
        if (url == null || "".equals(url)) {
            return "";
        }
        int index = url.lastIndexOf(flag);
        String fileName = url.substring(index + 1);
        Log.d(TAG, "getFileName: file name = " + fileName);
        return fileName;
    }
}

