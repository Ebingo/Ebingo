package com.promote.ebingo.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by acer on 2014/9/3.
 */
public class ContextUtil {
    private static Context mContext;

    public static void init(Context context){
        mContext=context;
    }

    public static void toast(String msg){
        if (mContext==null)throw  new RuntimeException(ContextUtil.class.getName()+" has not called init() ");
        Toast.makeText(mContext,msg,Toast.LENGTH_SHORT).show();
    }

    public static void toast(int resId){
        if (mContext==null)throw  new RuntimeException(ContextUtil.class.getName()+" has not called init() ");
        toast(mContext.getString(resId));
    }

    public static void toast(Object object){
        if (mContext==null)throw  new RuntimeException(ContextUtil.class.getName()+" has not called init() ");
        Toast.makeText(mContext,object+"",Toast.LENGTH_SHORT).show();
    }

    public static Context getContext(){
        return mContext;
    }

    public static String getString(int resId){
        if (mContext==null)throw  new RuntimeException(ContextUtil.class.getName()+" has not called init() ");
        return mContext.getString(resId);
    }
}
