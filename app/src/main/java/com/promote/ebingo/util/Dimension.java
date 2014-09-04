package com.promote.ebingo.util;

import android.content.res.Resources;
import android.util.TypedValue;
import android.widget.TextView;

/**
 * Created by acer on 2014/9/2.
 */
public class Dimension {
    /**
     * 将pixels值转化为dp值
     * @param value
     * @return
     */
    public static float dp(float value){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value,Resources.getSystem().getDisplayMetrics());
    }
    public static float sp(float value){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value,Resources.getSystem().getDisplayMetrics());
    }
}
