/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jch.d2code.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;

import com.google.zxing.ResultPoint;
import com.jch.d2code.R;
import com.jch.d2code.camera.CameraManager;

import java.util.Collection;
import java.util.HashSet;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder
 * rectangle and partial transparency outside it, as well as the laser scanner
 * animation and result points.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class ViewfinderView extends View {

    private static final int[] SCANNER_ALPHA = {0, 64, 128, 192, 255, 192,
            128, 64};
    private static final long ANIMATION_DELAY = 60L;
    private static final int OPAQUE = 0xFF;

    private final Paint paint;
    private final Paint laserPaint;
    private final int maskColor;
    private final int resultColor;
    private final int frameColor;
    private final int laserColor;
    private final int resultPointColor;
    private final int cornerColor;
    private final float TEXT_MARGIN_TOP;
    private Bitmap resultBitmap;
    private int scannerAlpha;
    private float saved_line;
    private Collection<ResultPoint> possibleResultPoints;
    private Collection<ResultPoint> lastPossibleResultPoints;
    private Xfermode xfermode;
    private Interpolator interpolator;
    private String text;

    // This constructor is used when the class is built from an XML resource.
    public ViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Initialize these once for performance rather than calling them every
        // time in onDraw().
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setTextAlign(Paint.Align.CENTER);

        laserPaint = new Paint(Paint.DITHER_FLAG);
        laserPaint.setMaskFilter(new BlurMaskFilter(20, BlurMaskFilter.Blur.NORMAL));
        Resources resources = getResources();
        maskColor = resources.getColor(R.color.viewfinder_mask);
        resultColor = resources.getColor(R.color.result_view);
        frameColor = resources.getColor(R.color.viewfinder_frame);
        laserColor = resources.getColor(R.color.viewfinder_laser);
        resultPointColor = resources.getColor(R.color.possible_result_points);
        cornerColor = resources.getColor(R.color.corner_color);
        scannerAlpha = 0;
        possibleResultPoints = new HashSet<ResultPoint>(5);
        xfermode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
        interpolator = new OvershootInterpolator();
        text = resources.getString(R.string.code_2_notice);

        paint.setTextSize(15 * resources.getDisplayMetrics().scaledDensity + 0.5f);
        TEXT_MARGIN_TOP = resources.getDisplayMetrics().density * 40 + 0.5f;

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public void onDraw(Canvas canvas) {
        Rect frame = CameraManager.get().getFramingRect();
        if (frame == null) {
            return;
        }
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        // Draw the exterior (i.e. outside the framing rect) darkened
        paint.setColor(resultBitmap != null ? resultColor : maskColor);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, width, height, paint);

        if (resultBitmap != null) {
            // Draw the opaque result bitmap over the scanning rectangle
            paint.setAlpha(OPAQUE);
            canvas.drawBitmap(resultBitmap, frame.left, frame.top, paint);
        } else {

            // Draw a two pixel solid black border inside the framing rect


            paint.setStyle(Paint.Style.FILL);
            paint.setXfermode(xfermode);
            canvas.drawRect(frame.left, frame.top, frame.right, frame.bottom, paint);
            paint.setXfermode(null);

            paint.setColor(frameColor);
            paint.setStrokeWidth(0);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(frame.left, frame.top, frame.right, frame.bottom, paint);
            paint.setColor(0xffffffff);
            canvas.drawText(text, frame.left + frame.width() / 2, frame.bottom + TEXT_MARGIN_TOP, paint);
            // Draw a red "laser scanner" line through the middle to show
            // decoding is active
            paint.setColor(laserColor);
            paint.setAlpha(SCANNER_ALPHA[scannerAlpha]);
            scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.length;

            saved_line = (saved_line + 13) % frame.height();
            int laserPosition = (int) (frame.top + saved_line * interpolator.getInterpolation(saved_line / (float) frame.height()));
            paint.setStyle(Paint.Style.FILL);
            RectF rect = new RectF(frame.left + 3, laserPosition, frame.right - 3, laserPosition + 4);
            canvas.drawOval(rect, paint);

            paint.setAlpha(0xff);
            paint.setColor(cornerColor);
            paint.setStrokeWidth(4);
            drawCorner(canvas, frame);
            Collection<ResultPoint> currentPossible = possibleResultPoints;
            Collection<ResultPoint> currentLast = lastPossibleResultPoints;
            if (currentPossible.isEmpty()) {
                lastPossibleResultPoints = null;
            } else {
                possibleResultPoints = new HashSet<ResultPoint>(5);
                lastPossibleResultPoints = currentPossible;
                paint.setAlpha(OPAQUE);
                paint.setColor(resultPointColor);
                for (ResultPoint point : currentPossible) {
                    canvas.drawCircle(frame.left + point.getX(), frame.top
                            + point.getY(), 6.0f, paint);
                }
            }
            if (currentLast != null) {
                paint.setAlpha(OPAQUE / 2);
                paint.setColor(resultPointColor);
                for (ResultPoint point : currentLast) {
                    canvas.drawCircle(frame.left + point.getX(), frame.top
                            + point.getY(), 3.0f, paint);
                }
            }

            // Request another update at the animation interval, but only
            // repaint the laser line,
            // not the entire viewfinder mask.
            postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top,
                    frame.right, frame.bottom);
        }
    }


    private void drawCorner(Canvas canvas, Rect frame) {
        int corner = frame.width() / 6;
        drawAngle(canvas, frame.left - 1, frame.top - 1, frame.left - 1 + corner, frame.top - 1);
        drawAngle(canvas, frame.right + 1, frame.top - 1, frame.right + 1, frame.top + corner - 1);
        drawAngle(canvas, frame.right + 1, frame.bottom + 1, frame.right - corner + 1, frame.bottom + 1);
        drawAngle(canvas, frame.left - 1, frame.bottom + 1, frame.left - 1, frame.bottom + 1 - corner);
    }

    private void drawAngle(Canvas canvas, float startX, float startY, float toX, float toY) {
        canvas.drawLine(startX, startY, toX, toY, paint);
        canvas.save();
        canvas.rotate(90, startX, startY);
        canvas.drawLine(startX, startY, toX, toY, paint);
        canvas.restore();
    }

    public void drawViewfinder() {
        resultBitmap = null;
        invalidate();
    }

    /**
     * Draw a bitmap with the result points highlighted instead of the live
     * scanning display.
     *
     * @param barcode An image of the decoded barcode.
     */
    public void drawResultBitmap(Bitmap barcode) {
        resultBitmap = barcode;
        invalidate();
    }

    public void addPossibleResultPoint(ResultPoint point) {
        possibleResultPoints.add(point);
    }

    private class LaserInterpolator implements Interpolator {

        @Override
        public float getInterpolation(float input) {
            return input * input - input + 1;
        }
    }

}
