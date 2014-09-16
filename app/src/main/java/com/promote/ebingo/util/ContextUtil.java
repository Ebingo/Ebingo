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

/**
 * Created by acer on 2014/9/3.
 */
public class ContextUtil {
    private static Context mContext;

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
     * 拨打电话
     * @param context
     * @param number
     */
    public static void dialNumber(final Activity context, final String number) {
        if (TextUtils.isEmpty(number) || number.equals(VaildUtil.validPhone(number))) return;
        DialogInterface.OnClickListener l = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
                        context.startActivity(intent);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("拨打电话")
                .setMessage("是否拨打" + number + "?")
                .setPositiveButton("拨打", null).setNegativeButton("取消", null).show();
    }
}
