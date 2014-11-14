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
import com.promote.ebingoo.bean.HotActivitys;
import com.promote.ebingoo.util.ContextUtil;

/**
 * Created by ACER on 2014/11/14.
 */
public class SpecialEventsLayout extends LinearLayout implements View.OnClickListener {

    private Context mContext;

    private View mContentView;
    /**
     * 第一个活动图标位。
     */
    private ImageView homeevent1iv;
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

    int rightMargin;
    int leftMargin;


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
//
//        ViewTreeObserver viewTreeObserver = mContentView.getViewTreeObserver();
//        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                mContentView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//                mContentWidth = mContentView.getWidth();
//                mScale = mContentWidth / baseImgSize.x;
//
//                calculateEventSize(point1, homeevent1iv);
//                calculateEventSize(poin2, homeevent2iv);
//                calculateEventSize(point3, homeevent3iv);
//            }
//        });

//        calculateEventSize(point1, homeevent1iv);
//        calculateEventSize(poin2, homeevent2iv);
//        calculateEventSize(point3, homeevent3iv);


    }

    public void calculateView(Activity activity) {
        DisplayUtil.resizeViewByScreenWidth(homeevent1iv, point1.x, point1.y, rightMargin + leftMargin, (Activity) activity);
        DisplayUtil.resizeViewByScreenWidth(homeevent2iv, poin2.x, poin2.y, rightMargin + leftMargin, (Activity) activity);
        DisplayUtil.resizeViewByScreenWidth(homeevent3iv, point3.x, point3.y, rightMargin + leftMargin, (Activity) activity);
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
        initHotActivity(activityData.getLeft().getImage(), homeevent2iv, ContextUtil.getRectangleImgOptions());
        initHotActivity(activityData.getBottom().getImage(), homeevent3iv, ContextUtil.getRectangleImgOptions());
    }

    private void initHotActivity(String imageUrl, ImageView img, DisplayImageOptions options) {
        ImageManager.load(imageUrl, img, options);
        img.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }
}
