package com.promote.ebingo.application;

        import android.os.Environment;

/**
 * Created by ACER on 2014/8/25.
 */
public class Constant {

    /** 设置是否为debug模式。 **/
    public static boolean mReleaseAble = false;

    public static boolean isReleaseAble() {
        return mReleaseAble;
    }
    /** 磁盘缓存路径. **/
    public static final String CACHE_DIR = Environment.getExternalStorageDirectory() + "/Ebingo";
    /** 圖片緩存路徑. **/
    public static final String IMG_DIR = CACHE_DIR + "/images";
}
