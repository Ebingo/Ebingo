package com.promote.ebingoo.util;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by ACER on 2014/11/20.
 */
public abstract class BaseDialog extends Dialog {

    private int layout;

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
    /**
     * content view.
     */
    private View layoutView;

    public BaseDialog(Context context) {
        super(context);
    }

    protected BaseDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);

    }

    public void setView(View layoutView) {
        this.layoutView = layoutView;
        Window window = getWindow();
        window.setContentView(layoutView);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        calculateSize();
    }

    private void calculateSize() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        // always use the portrait dimensions to do the scaling calculations so
        // we always get a portrait shaped
        // web dialog
        int width = metrics.widthPixels < metrics.heightPixels ? metrics.widthPixels : metrics.heightPixels;
        int height = metrics.widthPixels < metrics.heightPixels ? metrics.heightPixels : metrics.widthPixels;

        int dialogWidth = Math.min(
                getScaledSize(width, metrics.density, NO_PADDING_SCREEN_WIDTH, MAX_PADDING_SCREEN_WIDTH),
                metrics.widthPixels);
        int dialogHeight = Math.min(
                getScaledSize(height, metrics.density, NO_PADDING_SCREEN_HEIGHT, MAX_PADDING_SCREEN_HEIGHT),
                metrics.heightPixels);

        getWindow().setLayout(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    /**
     * Returns a scaled size (either width or height) based on the parameters
     * passed.
     *
     * @param screenSize     a pixel dimension of the screen (either width or height)
     * @param density        density of the screen
     * @param noPaddingSize  the size at which there's no padding for the dialog
     * @param maxPaddingSize the size at which to apply maximum padding for the dialog
     * @return a scaled size.
     */
    private int getScaledSize(int screenSize, float density, int noPaddingSize, int maxPaddingSize) {
        int scaledSize = (int) ((float) screenSize / density);
        double scaleFactor;
        if (scaledSize <= noPaddingSize) {
            scaleFactor = 1.0;
        } else if (scaledSize >= maxPaddingSize) {
            scaleFactor = MIN_SCALE_FACTOR;
        } else {
            // between the noPadding and maxPadding widths, we take a linear
            // reduction to go from 100%
            // of screen size down to MIN_SCALE_FACTOR
            scaleFactor = MIN_SCALE_FACTOR + ((double) (maxPaddingSize - scaledSize))
                    / ((double) (maxPaddingSize - noPaddingSize)) * (1.0 - MIN_SCALE_FACTOR);
        }
        return (int) (screenSize * scaleFactor);
    }


}
