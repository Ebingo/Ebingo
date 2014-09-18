package com.promote.ebingo.center;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.promote.ebingo.R;
import com.promote.ebingo.util.LogCat;

/**
 * TODO: document your custom view class.
 */
public class TagView extends View {
    private String text;
    private int baseColor = Color.RED;
    private int numberColor = Color.WHITE;
    private float textSize;

    private TextPaint mPaint;
    private TextPaint mNumberPaint;
    private float mTextWidth;
    private float mTextHeight;
    private float radius;
    private int DEFAULT_RADIUS = 20;
    private float horizontal_spacing;
    private float vertical_spacing;
    private int number;
    private boolean inDeleteState = false;

    public TagView(Context context) {
        super(context);
        init(null, 0);
    }

    public TagView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public TagView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    public boolean isInDeleteState() {
        return inDeleteState;
    }

    public void setInDeleteState(boolean inDeleteState) {
        this.inDeleteState = inDeleteState;
        invalidateTextPaintAndMeasurements();
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
        invalidateTextPaintAndMeasurements();
    }

    public float getVertical_spacing() {
        return vertical_spacing;
    }

    public void setVertical_spacing(float vertical_spacing) {
        this.vertical_spacing = vertical_spacing;
        invalidateTextPaintAndMeasurements();
    }

    public float getHorizontal_spacing() {
        return horizontal_spacing;
    }

    public void setHorizontal_spacing(float horizontal_spacing) {
        this.horizontal_spacing = horizontal_spacing;
        invalidateTextPaintAndMeasurements();
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
        invalidateTextPaintAndMeasurements();
    }


    public int getBaseColor() {
        return baseColor;
    }

    public void setBaseColor(int baseColor) {
        this.baseColor = baseColor;
        invalidateTextPaintAndMeasurements();
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.TagView, defStyle, 0);

        text = a.getString(R.styleable.TagView_text);
        if (text == null) text = "";
        baseColor = a.getColor(R.styleable.TagView_tagColor, baseColor);
        horizontal_spacing = a.getDimension(R.styleable.TagView_horizontal_spacing, 0);
        vertical_spacing = a.getDimension(R.styleable.TagView_vertical_spacing, 0);
        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        textSize = a.getDimension(
                R.styleable.TagView_textSize,
                textSize);


        a.recycle();

        // Set up a default TextPaint object
        mPaint = new TextPaint();
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextAlign(Paint.Align.LEFT);

        mNumberPaint = new TextPaint();
        mNumberPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mNumberPaint.setTextAlign(Paint.Align.LEFT);

        radius = getResources().getDisplayMetrics().densityDpi * DEFAULT_RADIUS + 0.5f;
        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements();
    }

    private void invalidateTextPaintAndMeasurements() {
        mPaint.setTextSize(textSize);
        mPaint.setColor(baseColor);
        mTextWidth = mPaint.measureText(text);
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        mTextHeight = fontMetrics.descent - fontMetrics.ascent;
        radius = (int) (mTextHeight / 2.5);

        mNumberPaint.setTextSize(textSize - 2);
        mNumberPaint.setColor(numberColor);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = (int) (getSize(widthMeasureSpec, (int) mTextWidth) + horizontal_spacing);
        int height = (int) (getSize(heightMeasureSpec, (int) mTextHeight) + vertical_spacing);
        setMeasuredDimension(width, height);
    }

    private int getSize(int measureSpec, int contentSize) {
        int size = MeasureSpec.getSize(measureSpec);
        int mode = MeasureSpec.getMode(measureSpec);
        switch (mode) {
            case MeasureSpec.UNSPECIFIED:
                size = (int) (contentSize + radius);
                break;
            case MeasureSpec.AT_MOST:
                size = (int) (contentSize + radius);
                break;
            case MeasureSpec.EXACTLY:
                break;
        }
        return size;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO: consider storing these as member variables to reduce
        int rect_w = (int) (getWidth() - radius);
        int rect_h = (int) (getHeight() - radius);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(0, radius, rect_w, getHeight() - 2, mPaint);
        // Draw the text.
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        canvas.drawText(text, (rect_w - mTextWidth) / 2, radius + rect_h / 2 - getBaseLineOffset(mPaint), mPaint);

        mPaint.setStyle(Paint.Style.FILL);
        if (inDeleteState) {//画叉叉
            canvas.drawCircle(rect_w, radius, radius, mPaint);
            float rate = (float) Math.sqrt(2);
            float startX = rect_w - radius / rate + 1;
            float startY = radius + radius / rate - 1;
            float length = (radius * 2) / rate - 2;
//            LogCat.i("--->", "rectX=" + rect_w + " startX=" + startX + " startY=" + startY + " length=" + length);
            canvas.drawLine(startX, startY, startX + length, startY - length, mNumberPaint);
            canvas.drawLine(startX, startY - length, startX + length, startY, mNumberPaint);
        } else if (number > 0) {//画数字
            canvas.drawCircle(rect_w, radius, radius, mPaint);
            float x = rect_w - mPaint.measureText(number + "") / 2;
            canvas.drawText(number + "", x, radius - getBaseLineOffset(mNumberPaint), mNumberPaint);
        }
    }

    private float getBaseLineOffset(Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return (fm.ascent + fm.descent) / 2;
    }

    public String getText() {
        return text;
    }


    public void setText(String exampleString) {
        text = exampleString;
        invalidateTextPaintAndMeasurements();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return true;
            case MotionEvent.ACTION_UP:
                return performClick();
        }
        return super.onTouchEvent(event);
    }

    public OnTagClickListener getOnTagClickListener() {
        return onTagClickListener;
    }

    public void setOnTagClickListener(OnTagClickListener onTagClickListener) {
        this.onTagClickListener = onTagClickListener;
    }

    private OnTagClickListener onTagClickListener;

    public boolean performClick() {
        if (onTagClickListener != null) {
            if (inDeleteState) onTagClickListener.onDelete(this);
            else onTagClickListener.onTagClick(this);
            return true;
        }
        return false;
    }

    /**
     * 删除接口
     */
    public interface OnTagClickListener {
        public void onDelete(TagView v);

        public void onTagClick(TagView v);
    }
}
