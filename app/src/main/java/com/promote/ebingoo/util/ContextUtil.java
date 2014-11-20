package com.promote.ebingoo.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.promote.ebingoo.R;
import com.promote.ebingoo.application.Constant;
import com.promote.ebingoo.application.EbingoApp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by acer on 2014/9/3.
 */
public class ContextUtil {
    private static Context mContext;

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

    public static DisplayImageOptions getCircleImgOptions() {
        DisplayImageOptions circleImgOptions = new DisplayImageOptions.Builder()
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .showImageForEmptyUri(R.drawable.circle_loading)
                .showImageOnLoading(R.drawable.circle_loading)
                .showImageOnFail(R.drawable.circle_loading)
                .cacheInMemory(true).cacheOnDisc(true).build();

        return circleImgOptions;

    }

    public static void init(Context context) {
        mContext = context;
    }

    public static void toast(int resId) {
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
        FileUtil.saveCache(mContext, name, o);
    }

    /**
     * 从缓存中读取一个object
     *
     * @param name
     * @return
     */
    public static Object read(String name) {
        LogCat.d("read---- :" + name);
        return FileUtil.readCache(mContext, name);
    }


    public static void cleanCurComany() {
        ((EbingoApp) mContext).cleanCurComany();
    }

    public static String getCurCompanyPwd() {
        return ((EbingoApp) mContext).getCurCompanyPwd();
    }

    public static String getCurCompanyName() {
        return ((EbingoApp) mContext).getCurCompanyName();
    }

    public static void saveCurCompanyPwd(String pwd) {
        ((EbingoApp) mContext).saveCurCompanyPwd(pwd);
    }

    public static void saveCurCompanyName(String name) {
        ((EbingoApp) mContext).saveCurCompanyName(name);
    }

    /**
     * 暂时只是简单的根据是否以"<"开头，以">"结尾来判断是否为html
     *
     * @return
     */
    public static boolean isHtml(String str) {
        if (str == null) return false;
        String htmlStr = new String(str);

        String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; // 定义script的正则表达式
        String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; // 定义style的正则表达式
        String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式

        Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
        Matcher m_script = p_script.matcher(htmlStr);
        htmlStr = m_script.replaceAll(""); // 过滤script标签

        Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
        Matcher m_style = p_style.matcher(htmlStr);
        htmlStr = m_style.replaceAll(""); // 过滤style标签

        Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
        Matcher m_html = p_html.matcher(htmlStr);
        htmlStr = m_html.replaceAll(""); // 过滤html标签
        return !str.equals(htmlStr);
    }

    /**
     * 根据会员类型获得会员等级图标. *
     */
    public static Drawable getVipImgByType(Resources res, int vipType) {

        switch (vipType) {
            case Constant.VIP_OVER: {
                return res.getDrawable(R.drawable.vip_practice_over);
            }
            case Constant.VIP_PRACTICE: {
                return res.getDrawable(R.drawable.vip_practice);
            }
            case Constant.VIP_1: {
                return res.getDrawable(R.drawable.vip_1_member);
            }
            case Constant.VIP_2: {
                return res.getDrawable(R.drawable.vip_2_member);
            }
            case Constant.VIP_3: {
                return res.getDrawable(R.drawable.vip_3_member);
            }
            case Constant.VIP_4: {
                return res.getDrawable(R.drawable.vip_4_member);
            }
            default: {
                return null;
            }

        }

    }
}
