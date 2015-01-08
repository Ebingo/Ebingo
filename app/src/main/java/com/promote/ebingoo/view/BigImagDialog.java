package com.promote.ebingoo.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.promote.ebingoo.util.ContextUtil;

/**
 * Created by ACER on 2015/1/8.
 */
public class BigImagDialog extends Dialog {


    private ImageView img;

    private String imgUrl;

    private Bitmap imgBitmap;

    private View view;

    // the minimum scaling factor for the web dialog (50% of screen size)
    private static final double MIN_SCALE_FACTOR = 0.5;
    // width below which there are no extra margins
    private static final int NO_PADDING_SCREEN_WIDTH = 480;
    // width beyond which we're always using the MIN_SCALE_FACTOR
    private static final int MAX_PADDING_SCREEN_WIDTH = 800;
    // height below which there are no extra margins
    private static final int NO_PADDING_SCREEN_HEIGHT = 800;
    // height beyond which we're always using the MIN_SCALE_FACTOR
    private static final int MAX_PADDING_SCREEN_HEIGHT = 1280;


    public BigImagDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        img = new ImageView(getContext());
//        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
//        Point point = new Point();
//        DisplayUtil.resizeViewByScreenWidth(point, imgBitmap.getWidth(), imgBitmap.getHeight(), 60, getOwnerActivity());
//        img.setLayoutParams(new ViewGroup.LayoutParams(point.x, point.y));
//        getWindow().setLayout(point.x, point.y);
//        img.setImageBitmap(imgBitmap);
        getWindow().setGravity(Gravity.CENTER);
        setContentView(view);
    }

    private void loadImg() {

        ImageLoader.getInstance().displayImage(imgUrl, img, ContextUtil.getCircleImgOptions(), new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {
            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {


            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });

    }

    /**
     * 设置imageVeiw.
     *
     * @param imgUrl
     */
    public void setImageView(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void setImageView(Bitmap bitmap) {
        imgBitmap = bitmap;
    }

    public BigImagDialog setView(View view) {
        this.view = view;
        return this;
    }


}
