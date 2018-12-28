package com.dipper.earthlive.util;

/**
 * @author Dipper
 * @date 2018/12/1
 * @email dipper.difference@gmail.com
 */
public interface DownloadCallback {

    /**
     * 成功下载一张图片
     *
     * @param fileName 图片名称
     */
    void downloadSucceed(String fileName);

    /**
     * 下载一张图片失败
     *
     * @param fileName 图片名称
     */
    void downloadFailed(String fileName);

}
