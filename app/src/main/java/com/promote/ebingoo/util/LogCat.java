package com.promote.ebingoo.util;

import android.util.Log;

import com.promote.ebingoo.application.Constant;

/**
 * @author jch.
 */
public class LogCat {

    /**
     * LOG标题。
     */
    public static final String TAG = "ebingo";
    public static final boolean FLAG = true;  //测试环境为true,生产环境为false

    public static void e(String tag, String msg) {
        if (Constant.isReleaseAble()) {
            return;
        }
        Log.e(tag, msg);
    }

    public static void e(String msg) {

        e(TAG, msg);
    }

    public static void d(String tag, String msg) {
        if (Constant.isReleaseAble()) {
            return;
        }
        Log.d(tag, msg);
    }

    public static void d(String msg) {

        d(TAG, msg);
    }

    public static void w(String tag, String msg) {
        if (Constant.isReleaseAble()) {
            return;
        }
        Log.w(tag, msg);
    }

    public static void w(String msg) {

        w(TAG, msg);
    }

    public static void v(String tag, String msg) {
        if (Constant.isReleaseAble()) {
            return;
        }
        Log.v(tag, msg);
    }

    public static void v(String msg) {
        v(TAG, msg);
    }

    public static void i(String tag, String msg) {
        if (Constant.isReleaseAble()) {
            return;
        }
        Log.i(tag, msg);
    }

    public static void i(String msg) {
        i(TAG, msg);
    }

}
