package com.promote.ebingo.publish;

import android.text.TextUtils;

/**
 * Created by acer on 2014/9/15.
 */
public enum VipType {
    VISITOR("9", "游客"), Experience_Vip("0", "体验会员"), Standard_VIP("1", "普通会员"), Silver_VIP("2", "银牌用户"), Gold_VIP("3", "金牌会员"), Platinum_VIP("4", "铂金会员");
    public String code;
    public String name;

    VipType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    @Override
    public String toString() {
        return code;
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

    public VipType next() {
        int next = ordinal() + 1;
        if (next < VipType.values().length) {
            return VipType.values()[next];
        }
        return null;
    }

    public static VipType parse(String code) {
        if (TextUtils.isEmpty(code)) return VipType.VISITOR;
        for (VipType type : VipType.values()) {
            if (type.code.equals(code)) return type;
        }
        return null;
    }
}
