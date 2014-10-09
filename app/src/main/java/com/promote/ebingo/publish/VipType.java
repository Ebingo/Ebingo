package com.promote.ebingo.publish;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.promote.ebingo.R;

/**
 * Created by acer on 2014/9/15.
 */
public enum VipType {
    VISITOR("9", "游客", 0),
    Experience_Vip("0", "体验会员", 0),
    Standard_VIP("1", "普通会员", R.drawable.standard),
    Silver_VIP("2", "银牌用户", R.drawable.silver),
    Gold_VIP("3", "金牌会员", R.drawable.gold),
    Platinum_VIP("4", "铂金会员", R.drawable.platinum);

    public String code;
    public String name;
    public int drawableId;

    VipType(String code, String name, int drawableId) {
        this.code = code;
        this.name = name;
        this.drawableId = drawableId;
    }

    @Override
    public String toString() {
        return code;
    }

    /**
     * 获取vipType对应的图标
     * @param context
     * @return
     */
    public Drawable getIcon(Context context) {
        Drawable drawable = null;
        try {
            drawable = context.getResources().getDrawable(drawableId);
        } catch (Exception e) {

        }
        return drawable;
    }

    /**
     * 根据vipCode获取vip名
     *
     * @param vipCode
     * @return
     */
    public static String nameOf(String vipCode) {
        for (VipType type : VipType.values()) {
            if (type.code.equals(vipCode)) return type.name;
        }
        return null;
    }

    /**
     * 根据vip的code，获得相应的vipType。
     *
     * @param code
     * @return 如果不存在对应的vipType，返回空
     */
    public static VipType parse(String code) {
        if (TextUtils.isEmpty(code)) return VipType.VISITOR;
        for (VipType type : VipType.values()) {
            if (type.code.equals(code)) return type;
        }
        return null;
    }
}
