package com.promote.ebingo.impl;

import com.promote.ebingo.bean.Adv;
import com.promote.ebingo.bean.GetIndexBeanTools;
import com.promote.ebingo.bean.HotBean;
import com.promote.ebingo.bean.HotCategory;

import java.util.ArrayList;

/**
 * Created by ACER on 2014/9/28.
 * <p/>
 * 首页默认加载数据。
 */
public class SimleHomeBean {

    public static GetIndexBeanTools.GetIndexBean initSimpleHomeBean() {

        GetIndexBeanTools.GetIndexBean getIndexBean = new GetIndexBeanTools().new GetIndexBean();

        ArrayList<Adv> advs = new ArrayList<Adv>();
        Adv adv1 = new Adv();
        advs.add(adv1);
        ArrayList<HotCategory> categories = new ArrayList<HotCategory>();
        for (int i = 0; i <= 3; i++) {
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
