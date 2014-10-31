package com.promote.ebingoo.impl;

import com.promote.ebingoo.bean.Adv;
import com.promote.ebingoo.bean.GetIndexBeanTools;
import com.promote.ebingoo.bean.HotBean;
import com.promote.ebingoo.bean.HotCategory;

import java.util.ArrayList;

/**
 * Created by ACER on 2014/9/28.
 * <p/>
 * 首页默认加载数据。
 */
public class SimpleHomeBean {

    public static GetIndexBeanTools.GetIndexBean initSimpleHomeBean() {

        GetIndexBeanTools.GetIndexBean getIndexBean = new GetIndexBeanTools().new GetIndexBean();

        ArrayList<Adv> advs = new ArrayList<Adv>();
        Adv adv1 = new Adv();
        advs.add(adv1);
        ArrayList<HotCategory> categories = new ArrayList<HotCategory>();
        for (int i = 0; i <= 6; i++) {
            HotCategory hotCategory = new HotCategory();
            hotCategory.setImage(null);
            hotCategory.setId(0);
            hotCategory.setName(null);
            categories.add(hotCategory);
        }
        ArrayList<HotBean> hotSupplyBeans = new ArrayList<HotBean>();
        for (int i = 0; i < 3; i++) {
            HotBean hotBean = new HotBean();
            hotSupplyBeans.add(hotBean);
        }

        ArrayList<HotBean> hotDemandBeans = new ArrayList<HotBean>();
        for (int i = 0; i < 3; i++) {
            HotBean hotBean = new HotBean();
            hotDemandBeans.add(hotBean);
        }

        getIndexBean.setAds(advs);
        getIndexBean.setHot_category(categories);
        getIndexBean.setHot_supply(hotSupplyBeans);
        getIndexBean.setHot_demand(hotDemandBeans);

        return getIndexBean;
    }
}
