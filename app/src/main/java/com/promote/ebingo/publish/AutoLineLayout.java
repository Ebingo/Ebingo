package com.promote.ebingo.publish;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
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

        int leftOffset = getPaddingLeft();
        int topOffset = getPaddingTop();
        int rightOffset = getPaddingRight();
        int remainWidth = mWidth - leftOffset - rightOffset;

        Rect frame = new Rect(leftOffset, topOffset, 0, 0);

        for (int i = 0; i < mCount; i++) {

            final View child = getChildAt(i);
            child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            int childWidth = child.getMeasuredWidth();

            remainWidth -= childWidth;  //将每次子控件宽度进行统计叠加，如果大于设定的高度则需要换行，高度即Top坐标也需重新设置

            if (remainWidth<0) {
                remainWidth = mWidth - leftOffset - rightOffset-childWidth;
                frame.left = leftOffset;
                frame.top = frame.bottom  + 5;
            }
            remainWidth-=ITEM_SPACING;

            frame.right = frame.left + child.getMeasuredWidth();
            frame.bottom = frame.top + child.getMeasuredHeight();

            map.put(child, new Rect(frame));
            frame.left = frame.right + ITEM_SPACING;
        }
        setMeasuredDimension(mWidth, frame.bottom+getPaddingBottom());
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
                child.layout(rect.left , rect.top, rect.right, rect.bottom);
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
