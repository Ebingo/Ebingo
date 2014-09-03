package com.promote.ebingo.util;

import android.content.res.Resources;
import android.util.TypedValue;

/**
 * Created by acer on 2014/9/2.
 */
public class Dimension {
    public static float dp(float value){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value,Resources.getSystem().getDisplayMetrics());
    }
    public static float sp(float value){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value,Resources.getSystem().getDisplayMetrics());
    }
}
