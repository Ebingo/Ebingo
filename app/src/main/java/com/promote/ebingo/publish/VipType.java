package com.promote.ebingo.publish;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.promote.ebingo.R;
import com.promote.ebingo.application.Constant;
import com.promote.ebingo.bean.Company;

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
     *
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
     * 获取当前vip所拥有的特权信息
     * @return
     */
    public VipInfo getVipInfo() {
        VipInfo info = new VipInfo();
        switch (this) {
            case VISITOR: {

                break;
            }

            case Experience_Vip: {
                info.dial_permission = VipInfo.DIAL_SUPPLY_TEL;
                info.supply_max_num = 10;
                info.demand_max_num = Long.MAX_VALUE;
                info.book_tag_num = 1;
                info.display_3d = 0;
                info.webService = false;
                info.email_book_info = false;
                break;
            }

            case Standard_VIP: {
                info.dial_permission = VipInfo.DIAL_DEMAND_TEL | VipInfo.DIAL_SUPPLY_TEL;
                info.supply_max_num = 20;
                info.demand_max_num = Long.MAX_VALUE;
                info.book_tag_num = 5;
                info.display_3d = 0;
                info.webService = false;
                info.email_book_info = false;
                break;
            }

            case Silver_VIP: {
                info.dial_permission = VipInfo.DIAL_DEMAND_TEL | VipInfo.DIAL_SUPPLY_TEL;
                info.supply_max_num = 30;
                info.demand_max_num = Long.MAX_VALUE;
                info.book_tag_num = 10;
                info.display_3d = 0;
                info.webService = false;
                info.email_book_info = false;
                break;
            }

            case Gold_VIP: {
                info.dial_permission = VipInfo.DIAL_DEMAND_TEL | VipInfo.DIAL_SUPPLY_TEL;
                info.supply_max_num = 50;
                info.demand_max_num = Long.MAX_VALUE;
                info.book_tag_num = 20;
                info.display_3d = 1;
                info.webService = true;
                info.email_book_info = false;
                break;
            }

            case Platinum_VIP: {
                info.dial_permission = VipInfo.DIAL_DEMAND_TEL | VipInfo.DIAL_SUPPLY_TEL;
                info.supply_max_num = Long.MAX_VALUE;
                info.demand_max_num = Long.MAX_VALUE;
                info.book_tag_num = Integer.MAX_VALUE;
                info.display_3d = 5;
                info.webService = true;
                info.email_book_info = true;
                break;
            }

        }
        return info;
    }

    /**
     * 根据vipCode获取vip名
     *
     * @param vipCode
     * @return
     */
    public static String nameOf(String vipCode) {
        return parse(vipCode).name;
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

    /**
     * 获取当前公司VIP类型
     * @return
     */
    public static VipType getCompanyInstance(){
        return parse(Company.getInstance().getVipType());
    }

    /**
     * VIP信息
     */
    public static class VipInfo {
        /**
         * 拨打求购电话
         */
        public static int DIAL_DEMAND_TEL = 1;
        /**
         * 拨打供应电话
         */
        public static int DIAL_SUPPLY_TEL = 2;
        /**
         * 拨打电话权限,0代表不能拨打
         */
        public int dial_permission = 0;
        /**
         * 发布供应信息最大条数
         */
        public long supply_max_num = 0;
        /**
         * 发布供应信息最大条数
         */
        public long demand_max_num = 0;
        /**
         * 订阅标签数
         */
        public int book_tag_num = 0;
        /**
         * 3D展示
         */
        public int display_3d = 0;
        /**
         * web站功能
         */
        public boolean webService = false;
        /**
         * 标签信息邮件推送
         */
        public boolean email_book_info = false;

        public boolean canDialSupply() {
            return (dial_permission & DIAL_SUPPLY_TEL) != 0;
        }

        public boolean canDialDemand() {
            return (dial_permission & DIAL_DEMAND_TEL) != 0;
        }

        /**
         * 判断该用户是否有权利拨打 电话
         * @param type 参照Constant.PUBLISH_SUPPLY，Constant.PUBLISH_DEMAND
         * @return true能拨打 false不能拨打
         */
        public boolean canDial(String type){
            if (Constant.PUBLISH_DEMAND.equals(type)){
                return canDialDemand();
            }else if (Constant.PUBLISH_SUPPLY.equals(type)){
                return canDialSupply();
            }
            return false;
        }
    }
}
