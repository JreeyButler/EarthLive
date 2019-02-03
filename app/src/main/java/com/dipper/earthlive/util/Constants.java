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

import android.os.Environment;


import java.io.File;

/**
 * @author Dipper
 * @date 2018/11/20
 * @email dipper.difference@gmail.com
 */
public class Constants {

    public static final int MSG_TEST_DISPLAY = 0x10000;
    public static final int MSG_DISPLAY_WALLPAPER = 0x10001;
    public static final int MSG_CLEAR_RENEW_ANIMATION = 0x10002;

    public static final int DEFAULT_SIZE = 1;
    public static final int SIZE_WALLPAPER = 1;
    /**
     * delay 30 minus
     */
    public static final long DEFAULT_DELAY_TIME = 1800000L;
    public static final long KB = 1024L;
    public static final long MB = 1024L * KB;
    public static final int NOTIFICATION_ID = 181205;
    public static final int SIZE_720P_WIDTH = 720;
    public static final int SIZE_720P_HEIGHT = 1280;
    public static final int SIZE_1080P_WIDTH = 1080;
    public static final int SIZE_1080P_HEIGHT = 1920;

    public static final String WALLPAPER_INDEX = "wallpaper_index";
    public static final String SHARE_PREFERENCES_NAME = "earth_preferences";
    public static final String ACTION_CHANGE_WALLPAPER_SUCCESS = "change_wallpaper_success";
    public static final String ACTION_WALLPAPER_NO_UPDATE = "wallpaper_no_update";
    public static final String ACTION_WALLPAPER_DOWNLOAD_FAILED = "wallpaper_download_failed";
    public static final String ACTION_RENEW_WALLPAPER = "renew_wallpaper";
    public static final String ACTION_UPDATE_WALLPAPER = "update_wallpaper";
    public static final String ACTION_SET_WALLPAPER = "set_wallpaper";
    public static final String ACTION_UPDATE_SIZE = "update_size";
    public static final String DATE_FORMAT = "HH:mm";
    public static final String DATE_FORMAT_1 = "yyyy/MM/dd";
    public static final String DEFAULT_PICTURE_STUFF = ".png";
    public static final String NORMAL_PICTURE_STUFF = ".jpg";
    public static final String KEY_LATEST_PICTURE_TIME = "key_latest_picture_time";
    public static final String KEY_PICTURE_DOWNLOADED = "key_picture_downloaded";
    public static final String KEY_LATEST_UPDATE_TIME = "latest_update_time";
    public static final String TEST_PICTURE_NAME = "default";
//    public static final String FY_4A_PICTURE_NAME = "FY-4A";

    private static final String APPLICATION_NAME = "EarthLive";
    private static final String PICTURE_DIR_NAME = "Picture";
    private static final String WALLPAPER_DIR_NAME = "Wallpaper";
    private static final String WALLPAPER_NAME = "wallpaper";
    private static final String ROOT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
    private static final String APPLICATION_PATH = ROOT_PATH + APPLICATION_NAME + File.separator;

    public static final String PICTURE_DIR_PATH = APPLICATION_PATH + PICTURE_DIR_NAME + File.separator;
    public static final String WALLPAPER_DIR_PATH = APPLICATION_PATH + WALLPAPER_DIR_NAME + File.separator;

    public static final String PICTURE_0_0 = "0_0" + DEFAULT_PICTURE_STUFF;
    public static final String PICTURE_0_1 = "0_1" + DEFAULT_PICTURE_STUFF;
    public static final String PICTURE_1_0 = "1_0" + DEFAULT_PICTURE_STUFF;
    public static final String PICTURE_1_1 = "1_1" + DEFAULT_PICTURE_STUFF;

    public static final String WALLPAPER_PATH = WALLPAPER_DIR_PATH + WALLPAPER_NAME + DEFAULT_PICTURE_STUFF;
    //    public static final String PICTURE_PATH = PICTURE_DIR_PATH + PICTURE_0_0;
    public static final String JAPAN_NAME = "himawari8";
    public static final String CHINA_NAME = "fy4a";
    public static final String DEFAULT_NAME = "default";
    public static final String[] REPOSITORIES_URL = {
            // 开源中国
            "https://gitee.com/JreeyBulter/EarthLive",
            // Github
            "https://ic_github.com/JreeyButler/EarthLive"};

    public static class PictureUrl {
        public static final String JAPAN_HD_URL = "http://himawari8-dl.nict.go.jp/himawari8/img/D531106/2d/550/";
        public static final String JAPAN_NORMAL_URL = "http://himawari8-dl.nict.go.jp/himawari8/img/D531106/1d/550/";
        public static final String CHINA_URL = "https://www.suansuanru.club/img/";
        public static final String TEST_URL = "http://192.168.31.245/img/";
        public static final String USA_URL = "";
    }

    public static class Key {
        public static final String KEY_DATA_FROM = "data_from";
        public static final String KEY_WALLPAPER_SIZE = "wallpaper_size";
        public static final String KEY_UPDATE_CYCLE = "update_cycle";
        public static final String KEY_DATA_TRAFFIC = "expected_data_traffic";
        public static final String KEY_OPEN_SOURCE = "open_source";
        public static final String KEY_AUTO_UPDATE = "auto_update";
        public static final String KEY_WIFI_ONLY = "wifi_only";
    }
}

