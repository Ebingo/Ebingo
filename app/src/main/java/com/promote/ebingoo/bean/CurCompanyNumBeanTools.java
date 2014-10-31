package com.promote.ebingoo.bean;

import com.google.gson.Gson;

/**
 * Created by ACER on 2014/9/11.
 */
public class CurCompanyNumBeanTools extends ResponseBaseBean<CurrentCompanyBaseNumBean> {

    public static CurCompanyNumBeanTools getCurConpanyNumBeanTools(String json) {
        return new Gson().fromJson(json, CurCompanyNumBeanTools.class);
    }
}
