package com.lateautumn4lin.headwolf.commons;
/*
 * Logger
 *
 * @author lateautumn4lin
 * @github https://github.com/lateautumn4lin
 * @date 2020/9/10 13:54
 */

import android.util.Log;

/**
 * The type Logger.
 */
public class Logger {
    private Logger() {
    }

    /**
     * The constant TAG.
     */
    public static final String TAG = "headwolf";

    /**
     * Logi.
     *
     * @param msg the msg
     */
    public static void logi(String msg) {
        Log.i(TAG, msg);
    }

    /**
     * Loge.
     *
     * @param msg the msg
     */
    public static void loge(String msg) {
        Log.e(TAG, msg);
    }

    /**
     * Logw.
     *
     * @param msg the msg
     */
    public static void logw(String msg) {
        Log.w(TAG, msg);
    }

    /**
     * Logd.
     *
     * @param msg the msg
     */
    public static void logd(String msg) {
        Log.d(TAG, msg);
    }

    /**
     * Logv.
     *
     * @param msg the msg
     */
    public static void logv(String msg) {
        Log.v(TAG, msg);
    }


}
