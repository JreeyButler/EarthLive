package com.dipper.earthlive.util;

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

    public DownloadTask(String url, DownloadCallback callback) {
        this.url = url;
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
            if (request != null) {
                inputStream = response.body().byteStream();
                RandomAccessFile randomAccessFile = new RandomAccessFile(Constants.PICTURE_DIR_PATH + fileName, "rw");
                byte[] bytes = new byte[1024];
                int length;
                while ((length = inputStream.read(bytes)) != -1) {
                    randomAccessFile.write(bytes, 0, length);
                }
            }
        } catch (Exception e) {
            mCallback.downloadFailed(fileName);
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            mCallback.downloadSucceed(fileName);
        }
    }

    private String getFileName() {
        final String flag = "_";
        if (url == null || "".equals(url)) {
            return "";
        }
        int index = url.indexOf(flag);
        return url.substring(index + 1, url.length());
    }

}

