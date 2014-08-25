package com.jch.lib.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by jch on 2014/8/25.
 *
 * 適用于嵌套到scrollView中
 */
public class ScrollListView extends ListView{

    public ScrollListView(Context context) {
        super(context);
    }
    public ScrollListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public ScrollListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

}
