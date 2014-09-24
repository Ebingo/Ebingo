package com.promote.ebingo.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import com.jch.lib.util.VaildUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by acer on 2014/9/3.
 */
public class ContextUtil {
    private static Context mContext;
    private static CacheUtil mCacheUtil;

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

    public static void save(String name, Object o) {
        if (mCacheUtil == null) mCacheUtil = new CacheUtil();
        mCacheUtil.save(name, o);
    }

    public static Object read(String name) {
        if (mCacheUtil == null) mCacheUtil = new CacheUtil();
        return mCacheUtil.read(name);
    }

    private static class CacheUtil {
        public void save(String name, Object o) {
            FileOutputStream os = null;
            try {
                os = mContext.openFileOutput(name, Context.MODE_PRIVATE);
                ObjectOutputStream oos = new ObjectOutputStream(os);
                oos.writeObject(o);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        public Object read(String name) {
            Object obj = null;
            try {
                FileInputStream is = mContext.openFileInput(name);
                ObjectInputStream ois = new ObjectInputStream(is);
                obj = ois.readObject();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return obj;
        }
    }
}
