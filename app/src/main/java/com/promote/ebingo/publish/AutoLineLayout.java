package com.promote.ebingo.publish;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.promote.ebingo.util.LogCat;

import java.util.Hashtable;

/**
 * Created by acer on 2014/9/11.
 */
public class AutoLineLayout extends LinearLayout {
    public static final int ITEM_SPACING = 8;
    private Hashtable<View, Rect> map = new Hashtable<View, Rect>();

    public AutoLineLayout(Context context) {
        super(context);
    }

    public AutoLineLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int mWidth = MeasureSpec.getSize(widthMeasureSpec);
        int mCount = getChildCount();

        int mX = 0;
        int mY = 0;
        Rect rect = new Rect(0, 0, 5, 0);


        int j = 0;

        for (int i = 0; i < mCount; i++) {
            final View child = getChildAt(i);

            child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            // 此处增加onlayout中的换行判断，用于计算所需的高度
            int child_w = child.getMeasuredWidth() + ITEM_SPACING;
            int child_h = child.getMeasuredHeight();
            mX += child_w;  //将每次子控件宽度进行统计叠加，如果大于设定的高度则需要换行，高度即Top坐标也需重新设置

            rect.left = getPosition(i - j, i);
            rect.right = rect.left + child.getMeasuredWidth();
            LogCat.i("--->", "mX=" + mX + ",mWidth=" + mWidth);
            if (mX >= mWidth) {
                mX = child_w;
                mY += child_h;
                j = i;
                rect.left = 0;
                rect.right = rect.left + child.getMeasuredWidth();
                rect.top = mY + 5;
                //PS：如果发现高度还是有问题就得自己再细调了
            }
            rect.bottom = rect.top + child.getMeasuredHeight();
            mY = rect.top;  //每次的高度必须记录 否则控件会叠加到一起
            map.put(child, new Rect(rect));
        }
        setMeasuredDimension(mWidth, rect.bottom);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(1, 1); // default of 1px spacing
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // TODO Auto-generated method stub

        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            Rect rect = map.get(child);
            if (rect != null) {
                child.layout(rect.left, rect.top, rect.right, rect.bottom);
            } else {
                LogCat.i("MyLayout", "error");
            }
        }
    }

    public int getPosition(int row, int childIndex) {
        if (row > 0) {
            return getPosition(row - 1, childIndex - 1) + getChildAt(childIndex - 1).getMeasuredWidth() + ITEM_SPACING;
        }
        return getPaddingLeft();
    }
}
