package com.promote.ebingoo.home;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jch.lib.util.DisplayUtil;
import com.jch.lib.util.ImageManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.promote.ebingoo.R;
import com.promote.ebingoo.bean.HotActivity;
import com.promote.ebingoo.bean.HotActivitys;
import com.promote.ebingoo.util.ContextUtil;

/**
 * Created by ACER on 2014/11/14.
 */
public class SpecialEventsLayout extends LinearLayout implements View.OnClickListener {

    /**
     * 第一个活动图标位的大小。
     */
    private static final Point point1 = new Point(350, 140);
    /**
     * 第二个活动图标位的大小。
     */
    private static final Point poin2 = new Point(250, 140);
    /**
     * 第三个活动图标的大小。
     */
    private static final Point point3 = new Point(600, 140);
    /**
     * 基本活动图标的大小。
     */
    private static final Point baseImgSize = new Point(600, 140);
    int rightMargin;
    int leftMargin;
    private Context mContext;
    private View mContentView;
    /**
     * 第一个活动图标位。
     */
    private ImageView homeevent1iv;
    private double mContentWidth;
    /**
     * 图片放缩比。
     */
    private double mScale;
    /**
     * 第二个活动图标位。
     */
    private ImageView homeevent2iv;
    /**
     * 第三个活动图标位。
     */
    private ImageView homeevent3iv;
    private SpecialEventOnlickListener specialEventClickListener;

    public SpecialEventsLayout(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public SpecialEventsLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {

        mContentView = LayoutInflater.from(mContext).inflate(R.layout.home_specialevent_layout, null);

        initialize();
        addView(mContentView);
    }

    /**
     * 初始化view。
     */
    private void initialize() {

        homeevent1iv = (ImageView) mContentView.findViewById(R.id.home_event1_iv);
        homeevent2iv = (ImageView) mContentView.findViewById(R.id.home_event2_iv);
        homeevent3iv = (ImageView) mContentView.findViewById(R.id.home_event3_iv);
        LinearLayout contentView = (LinearLayout) mContentView.findViewById(R.id.home_specialevent_content);

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) contentView.getLayoutParams();
        rightMargin = params.rightMargin;
        leftMargin = params.leftMargin;
    }

    public void calculateView(Activity activity) {
        float scale = DisplayUtil.getScaledByWidth(activity.getWindowManager(), baseImgSize.x, rightMargin + leftMargin);
        makeScaledView(scale, homeevent1iv, point1);
        makeScaledView(scale, homeevent2iv, poin2);
        makeScaledView(scale, homeevent3iv, point3);
    }

    private void makeScaledView(float scaled, View view, Point baseSize) {

        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = (int) (baseSize.y * scaled);
        params.width = (int) (baseSize.x * scaled);
        view.setLayoutParams(params);

    }

    public void setSpecialEventClickListener(SpecialEventOnlickListener specialEventClickListener) {
        this.specialEventClickListener = specialEventClickListener;
    }

    /**
     * 计算imageView放缩后的大小.
     *
     * @param piont
     * @param img
     */
    private void calculateEventSize(Point piont, ImageView img) {
        Point imgSize = new Point();
        imgSize.x = (int) (piont.x * mScale);
        imgSize.y = (int) (piont.y * mScale);
        ViewGroup.LayoutParams params = img.getLayoutParams();
        params.width = imgSize.x;
        params.height = imgSize.y;
        img.setLayoutParams(params);
    }

    /**
     * 设置推荐活动的数据
     *
     * @param activityData
     */
    public void setHotAcitivityData(HotActivitys activityData) {

        initHotActivity(activityData.getRight().getImage(), homeevent1iv, ContextUtil.getRectangleImgOptions());
        homeevent1iv.setTag(activityData.getRight());
        initHotActivity(activityData.getLeft().getImage(), homeevent2iv, ContextUtil.getRectangleImgOptions());
        homeevent2iv.setTag(activityData.getLeft());
        initHotActivity(activityData.getBottom().getImage(), homeevent3iv, ContextUtil.getRectangleImgOptions());
        homeevent3iv.setTag(activityData.getBottom());

    }

    private void initHotActivity(String imageUrl, ImageView img, DisplayImageOptions options) {
        ImageManager.load(imageUrl, img, options);
        img.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        HotActivity hotActivity = (HotActivity) v.getTag();
        if (hotActivity != null) {
            specialEventClickListener.onEventClickListener((ImageView) v, hotActivity.getType(), hotActivity.getContent());
        }
//        switch (v.getId()) {
//
//
//            case R.id.home_event1_iv: {
//
//
//                break;
//            }
//            case R.id.home_event2_iv: {
//
//
//                break;
//            }
//
//            case R.id.home_event3_iv: {
//
//
//                break;
//            }
//
//
//        }
    }


    public interface SpecialEventOnlickListener {

        public void onEventClickListener(ImageView view, int activityType, String content);

    }
}
