package com.promote.ebingoo.util;

import android.text.TextUtils;

/**
 * Created by acer on 2014/10/24.
 */
public class FormatUtil {
    public static final String DEFAULT_PRICE = "面议";

    public static String formatPrice(String price) {
        return formatPrice(price, null);
    }

    public static String formatPrice(String price, String unit) {
        String fmPrice;
        if (TextUtils.isEmpty(price) || "0".equals(price)) {
            fmPrice = DEFAULT_PRICE;
        } else if (TextUtils.isEmpty(unit)) {
            fmPrice = new String(price);
        } else {
            fmPrice = new String(price + "/" + unit);
        }
        return fmPrice;
    }

    public static String formatSellNum(String numStr, String unit) {
        String fmNum;
        try {
            int num = Integer.parseInt(numStr);
            fmNum = new String(num + unit);
        } catch (NumberFormatException e) {
            fmNum = new String(numStr);
        }

        return fmNum;
    }
}
