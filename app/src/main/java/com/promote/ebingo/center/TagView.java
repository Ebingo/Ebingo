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

/**
 * TODO: document your custom view class.
 */
public class TagView extends View {
    private String text;
    /**
     * 选中颜色
     */
    private int checkedColor = Color.RED;
    /**
     * 指示内容颜色
     */
    private int indicateColor = Color.WHITE;
    /**
     * 未选中状态颜色
     */
    private int defaultColor;
    private float textSize;
    private float indicateTextSize;
    private float indicateWidth;
    private float indicateHeight;
    private TextPaint mPaint;
    private TextPaint topRightPaint;
    private float mTextWidth;
    private float mTextHeight;
    private float radius;
    private int DEFAULT_RADIUS = 20;
    private float horizontal_spacing;
    private float vertical_spacing;
    private int number;
    private String numberStr;
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


    public int getCheckedColor() {
        return checkedColor;
    }

    public void setCheckedColor(int checkedColor) {
        this.checkedColor = checkedColor;
        invalidateTextPaintAndMeasurements();
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.TagView, defStyle, 0);

        text = a.getString(R.styleable.TagView_text);
        if (text == null) text = "";
        checkedColor = a.getColor(R.styleable.TagView_tagColor, checkedColor);
        horizontal_spacing = a.getDimension(R.styleable.TagView_horizontal_spacing, 0);
        vertical_spacing = a.getDimension(R.styleable.TagView_vertical_spacing, 0);
        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        textSize = a.getDimension(
                R.styleable.TagView_textSize,
                textSize);
        indicateTextSize = a.getDimension(
                R.styleable.TagView_indicateTextSize,
                textSize);
        indicateHeight= a.getDimension(
                R.styleable.TagView_indicateHeight,
                textSize);
        number = a.getInteger(R.styleable.TagView_number, 0);
        inDeleteState = a.getBoolean(R.styleable.TagView_deleteState, false);
        defaultColor = a.getColor(R.styleable.TagView_defaultColor, Color.GRAY);
        a.recycle();

        // Set up a default TextPaint object
        mPaint = new TextPaint();
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextAlign(Paint.Align.LEFT);

        topRightPaint = new TextPaint();
        topRightPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        topRightPaint.setTextAlign(Paint.Align.LEFT);

        radius = getResources().getDisplayMetrics().densityDpi * DEFAULT_RADIUS + 0.5f;
        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements();
    }

    private void invalidateTextPaintAndMeasurements() {
        mPaint.setTextSize(textSize);
        mPaint.setColor(checkedColor);
        mTextWidth = mPaint.measureText(text);
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        mTextHeight = fontMetrics.descent - fontMetrics.ascent;

        topRightPaint.setTextSize(indicateTextSize);
        if (number>100){
            numberStr="99+";
        }else{
            numberStr=number+"";
        }
        indicateWidth=topRightPaint.measureText(numberStr)*1.6f;
        if(indicateWidth<indicateHeight)indicateWidth=indicateHeight;
        topRightPaint.setColor(indicateColor);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width=MeasureSpec.getSize(widthMeasureSpec);
        int height=MeasureSpec.getSize(heightMeasureSpec);
        int widthMode=MeasureSpec.getMode(widthMeasureSpec);
        int heightMode=MeasureSpec.getMode(heightMeasureSpec);
        if (widthMode!=MeasureSpec.EXACTLY){
            width= (int) (mTextWidth+horizontal_spacing+indicateWidth/2)+1;
        }
        if (heightMode!=MeasureSpec.EXACTLY){
            height= (int) (mTextHeight+vertical_spacing+indicateHeight/2);
        }

        setMeasuredDimension(width, height);
    }

    public int getDefaultColor() {
        return defaultColor;
    }

    public void setDefaultColor(int defaultColor) {
        this.defaultColor = defaultColor;
        invalidateTextPaintAndMeasurements();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO: consider storing these as member variables to reduce
        float indicate_width_half=indicateWidth/2;
        float height_offset_half=indicateHeight/2;
        int content_width = (int) (getWidth() - indicate_width_half);
        int content_height = (int) (getHeight() - height_offset_half);
        // Draw the text.
        if (inDeleteState) {//删除状态
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(checkedColor);
            //绘制矩形框
            canvas.drawRect(0, height_offset_half, content_width, getHeight() - 2, mPaint);
            //绘制文字内容
            canvas.drawText(text, (content_width - mTextWidth) / 2, height_offset_half + content_height / 2 - getBaseLineOffset(mPaint), mPaint);

            mPaint.setStyle(Paint.Style.FILL);
            //绘制叉叉背景
            canvas.drawCircle(content_width, height_offset_half, height_offset_half, mPaint);
            float rate = (float) Math.sqrt(2);//根号2
            float startX = content_width - height_offset_half / rate + 1;
            float startY = height_offset_half + height_offset_half / rate - 1;
            float length = (height_offset_half * 2) / rate - 2;
//            LogCat.i("--->", "rectX=" + rect_w + " startX=" + startX + " startY=" + startY + " length=" + length);
            //绘制叉叉，两条线段
            canvas.drawLine(startX, startY, startX + length, startY - length, topRightPaint);
            canvas.drawLine(startX, startY - length, startX + length, startY, topRightPaint);
        } else {//画数字
            mPaint.setColor(defaultColor);
            mPaint.setStyle(Paint.Style.STROKE);
            //绘制大矩形框
            canvas.drawRect(1, height_offset_half, content_width, getHeight() - 2, mPaint);
            //绘制文字内容
            canvas.drawText(text, (content_width - mTextWidth) / 2, height_offset_half + content_height / 2 - getBaseLineOffset(mPaint), mPaint);
            if (number > 0) {//绘制新消息数
                topRightPaint.setStyle(Paint.Style.FILL);
                topRightPaint.setColor(checkedColor);
                //绘制右上角矩形框
                canvas.drawRect(content_width - indicate_width_half, 0, content_width + indicate_width_half, 2 * height_offset_half, topRightPaint);

                topRightPaint.setStyle(Paint.Style.STROKE);
                topRightPaint.setColor(Color.BLACK);
                canvas.drawRect(content_width - indicate_width_half, 0, content_width + indicate_width_half, 2 * height_offset_half, topRightPaint);
//              canvas.drawCircle(rect_w, radius, radius, mPaint);
                float x = content_width - topRightPaint.measureText(numberStr) / 2;
                topRightPaint.setColor(indicateColor);
                canvas.drawText(numberStr, x, height_offset_half - getBaseLineOffset(topRightPaint), topRightPaint);
            }
        }
    }

    /**
     * 获取Paint的baseLine偏移量
     */
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
