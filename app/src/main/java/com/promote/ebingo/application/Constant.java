package com.promote.ebingo.application;

import android.content.Context;
import android.graphics.Point;
import android.os.Environment;

import com.jch.lib.util.SharedPreferenceUtil;

/**
 * Created by ACER on 2014/8/25.
 */
public class Constant {
    public static final String WAITING = "0";
    public static final String PASS = "1";
    public static final String FAIL = "2";
    /**
     * 设置是否为debug模式。 *
     */
    public static boolean mReleaseAble = false;

    public static boolean isReleaseAble() {
        return mReleaseAble;
    }

    private static final String FIRSTSTART = "first_start";

    /**
     * 磁盘缓存路径. *
     */
    public static final String CACHE_DIR = Environment.getExternalStorageDirectory() + "/Ebingo";
    /**
     * 圖片緩存路徑. *
     */
    public static final String IMG_DIR = CACHE_DIR + "/images";

    public static final int DB_VERSION = 1;
    /**
     * 程序大图的尺寸。 *
     */
    public static final Point imageSize = new Point(720, 258);

    /**
     * 将第一次运行程序表示保存到本地.
     *
     * @param context
     */
    public static void savefirstStart(Context context) {
        SharedPreferenceUtil.saveBoolean(context, FIRSTSTART, false);
    }

    /**
     * 获得程序是否是第一次启动.
     *
     * @param context
     * @return
     */
    public static boolean isFirstStart(Context context) {


        return SharedPreferenceUtil.getBoolean(context, FIRSTSTART, true);
    }
}
