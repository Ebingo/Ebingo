package com.promote.ebingo.center;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.promote.ebingo.R;

/**
 * TODO: document your custom view class.
 */
public class TagView extends View {
    private String text; // TODO: use a default from R.string...
    private int textColor = Color.RED; // TODO: use a default from R.color...
    private float mExampleDimension = 0; // TODO: use a default from R.dimen...

    private TextPaint mPaint;
    private float mTextWidth;
    private float mTextHeight;
    private float radius;
    private int DEFAULT_RADIUS = 20;
    private float horizontal_spacing;
    private float vertical_spacing;

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

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.TagView, defStyle, 0);

        text = a.getString(
                R.styleable.TagView_text);
        if (text == null) text = "";
        textColor = a.getColor(
                R.styleable.TagView_tagColor,
                textColor);
        horizontal_spacing = a.getDimension(R.styleable.TagView_horizontal_spacing, 0);
        vertical_spacing = a.getDimension(R.styleable.TagView_vertical_spacing, 0);
        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        mExampleDimension = a.getDimension(
                R.styleable.TagView_textSize,
                mExampleDimension);


        a.recycle();

        // Set up a default TextPaint object
        mPaint = new TextPaint();
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextAlign(Paint.Align.LEFT);
        radius = getResources().getDisplayMetrics().densityDpi * DEFAULT_RADIUS + 0.5f;
        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements();
    }

    private void invalidateTextPaintAndMeasurements() {
        mPaint.setTextSize(mExampleDimension);
        mPaint.setColor(textColor);
        mTextWidth = mPaint.measureText(text);
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        mTextHeight = fontMetrics.descent - fontMetrics.ascent;
        radius = (int) (mTextHeight / 2.5);
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
        float offset_y=mTextHeight/2+rect_h/2;
        float textBaseY = (rect_h-mTextHeight)/2+radius;
        canvas.drawText(text,
                (rect_w - mTextWidth) / 2,
                textBaseY ,
                mPaint);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(rect_w, radius, radius, mPaint);
        // Draw the example drawable on top of the text.
    }

    /**
     * Gets the example string attribute value.
     *
     * @return The example string attribute value.
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the view's example string attribute value. In the example view, this string
     * is the text to draw.
     *
     * @param exampleString The example string attribute value to use.
     */
    public void setText(String exampleString) {
        text = exampleString;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example color attribute value.
     *
     * @return The example color attribute value.
     */
    public int getExampleColor() {
        return textColor;
    }

    /**
     * Sets the view's example color attribute value. In the example view, this color
     * is the font color.
     *
     * @param exampleColor The example color attribute value to use.
     */
    public void setExampleColor(int exampleColor) {
        textColor = exampleColor;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example dimension attribute value.
     *
     * @return The example dimension attribute value.
     */
    public float getExampleDimension() {
        return mExampleDimension;
    }

    /**
     * Sets the view's example dimension attribute value. In the example view, this dimension
     * is the font size.
     *
     * @param exampleDimension The example dimension attribute value to use.
     */
    public void setExampleDimension(float exampleDimension) {
        mExampleDimension = exampleDimension;
        invalidateTextPaintAndMeasurements();
    }

}
