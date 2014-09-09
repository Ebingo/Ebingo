package com.jch.lib.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * 根据屏幕比例，计算控件显示大小。
 *
 * @author jch.
 */
public class DisplayUtil {


    /**
     * @param wm
     * @param baseY 基准height.
     * @param baseX 基准width.
     */
    private void calcuateDisplayScale(WindowManager wm, float baseY, float baseX) {
        // final WindowManager wm = (WindowManager)
        // getSystemService(Context.WINDOW_SERVICE);
        final Display disp = wm.getDefaultDisplay();
        Point mPoint = new Point();
        getSize(disp, mPoint);
        float displayWidth = mPoint.x;
        float displayHeight = mPoint.y;
        // LogCat.i("disPlay height---:" + mPoint.y + ";---display width---:"
        // + mPoint.x);
        // mWithScale = displayWidth / baseX;
        // mHeightScale = displayHeight / baseY;
    }

    @SuppressLint("NewApi")
    public static void getSize(Display display, Point outSize) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            display.getSize(outSize);
        } else {
            outSize.x = display.getWidth();
            outSize.y = display.getHeight();
        }
    }

    /**
     * @param wm
     * @param baseY
     * @param baseX
     * @return
     */
    public static float sizeYByX(WindowManager wm, float baseY, float baseX) {

        final Display disp = wm.getDefaultDisplay();
        Point mPoint = new Point();
        getSize(disp, mPoint);
        float displayWidth = mPoint.x;

        float scaleX = displayWidth / baseX;

//        return baseY * scaleX + 0.5f;
        return baseY * scaleX;
    }

    /**
     *
     *
     * @param windowManager
     * @param baseY
     * @param basex
     * @param exceptDis 去除的宽度。
     * @return
     */
    public static float sizeYByX(WindowManager windowManager,  float baseY, float basex, int exceptDis){

        final  Display display = windowManager.getDefaultDisplay();
        Point mPoint = new Point();
        getSize(display, mPoint);
        if (exceptDis > mPoint.x){
            return 0.0f;
        }

        float displayWidth = mPoint.x - exceptDis;
        float scaleX = displayWidth /basex;

        return baseY * scaleX;

    }

    /**
     * 根据屏幕宽度设置view的大小.(去除view的margin。
     *
     * @param view       要改變尺寸的view。
     * @param baseWidth  viwe的基准宽.
     * @param baseHeight view的基准高。
     */
    public static void reSizeViewByWidth(View view, int baseWidth, int baseHeight, Activity activity) {

        LinearLayout.LayoutParams pagerParams = (LinearLayout.LayoutParams) view
                .getLayoutParams();


        int marginDis = pagerParams.leftMargin + pagerParams.rightMargin;
        pagerParams.height = (int) DisplayUtil.sizeYByX(activity
                .getWindowManager(), baseHeight, baseWidth, marginDis);
        pagerParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        view.setLayoutParams(pagerParams);
    }

    /**
     * 根据屏幕宽度设置view的大小.
     * @param view   要改變尺寸的view。
     * @param baseWidth   viwe的基准宽.
     * @param baseHeight    view的基准高。
     */
    public static void reSizeViewByScreenWidth(View view, int baseWidth,int baseHeight, Activity activity){

        ViewGroup.LayoutParams pagerParams = (ViewGroup.LayoutParams) view
                .getLayoutParams();

        pagerParams.height = (int) DisplayUtil.sizeYByX(activity
                .getWindowManager(), baseHeight, baseWidth);
        pagerParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        view.setLayoutParams(pagerParams);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

}
