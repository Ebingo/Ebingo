package com.promote.ebingo.util;

import android.content.Context;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.promote.ebingo.R;

/**
 * Created by acer on 2014/9/3.
 */
public class ContextUtil {
    private static Context mContext;
    private static FileUtil mFileUtil;

    /**
     * get square image options.
     *
     * @return
     */
    public static DisplayImageOptions getSquareImgOptions() {

        DisplayImageOptions squareImageOptions = new DisplayImageOptions.Builder()
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .showImageForEmptyUri(R.drawable.loading_waite)
                .showImageOnLoading(R.drawable.loading_waite)
                .showImageOnFail(R.drawable.load_failed_img)
                .cacheInMemory(true).cacheOnDisc(true).build();
        return squareImageOptions;
    }

    /**
     * get rectangle image options.
     *
     * @return
     */
    public static DisplayImageOptions getRectangleImgOptions() {

        DisplayImageOptions ractangleImgOptions = new DisplayImageOptions.Builder()
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .showImageForEmptyUri(R.drawable.loading_big_waite)
                .showImageOnLoading(R.drawable.loading_big_img)
                .showImageOnFail(R.drawable.load_failed_big_img)
                .cacheInMemory(true).cacheOnDisc(true).build();


        return ractangleImgOptions;
    }

    public static void init(Context context) {
        mContext = context;
    }

    public static void toast(String msg) {
        if (mContext == null)
            throw new RuntimeException(ContextUtil.class.getName() + " has not called init() ");
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    public static void toast(int resId) {
        if (mContext == null)
            throw new RuntimeException(ContextUtil.class.getName() + " has not called init() ");
        toast(mContext.getString(resId));
    }

    public static void toast(Object object) {
        if (mContext == null)
            throw new RuntimeException(ContextUtil.class.getName() + " has not called init() ");
        Toast.makeText(mContext, object + "", Toast.LENGTH_SHORT).show();
    }

    public static Context getContext() {
        return mContext;
    }

    public static String getString(int resId) {
        if (mContext == null)
            throw new RuntimeException(ContextUtil.class.getName() + " has not called init() ");
        return mContext.getString(resId);
    }

    /**
     * 将一个对象写入到缓存文件中
     *
     * @param name
     * @param o
     */
    public static void saveCache(String name, Object o) {
        if (mFileUtil == null) mFileUtil = new FileUtil();
        mFileUtil.saveCache(mContext, name, o);
    }

    /**
     * 从缓存中读取一个object
     *
     * @param name
     * @return
     */
    public static Object read(String name) {
        if (mFileUtil == null) mFileUtil = new FileUtil();
        return mFileUtil.readCache(mContext, name);
    }


}
