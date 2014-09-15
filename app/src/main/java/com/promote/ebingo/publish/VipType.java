package com.promote.ebingo.publish;

/**
 * Created by acer on 2014/9/15.
 */
public enum VipType {
    VISITOR("9", "游客"), NORMAL_VIP("0", "普通会员"), VIP("1", "VIP用户"), VVIP("2", "VVIP用户");
    public String code;
    public String name;

    VipType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    @Override
    public String toString() {
        return code ;
    }

    /**
     * 判断当前vip类型是否具有发布供应信息特权
     *
     * @param vipCode
     * @return
     */
    public static boolean canPublishSupply(String vipCode) {
        return !(vipCode == null || "".equals(vipCode) || "0".equals(vipCode) || "9".equals(vipCode));
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

    public static VipType parse(String code){
        for (VipType type : VipType.values()) {
            if (type.code.equals(code)) return type;
        }
       return null;
    }
}
