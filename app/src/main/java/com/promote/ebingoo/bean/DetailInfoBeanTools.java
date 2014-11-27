package com.promote.ebingoo.bean;

import com.google.gson.Gson;

/**
 * Created by ACER on 2014/9/12.
 */
public class DetailInfoBeanTools {

    private DetailInfoBean response;

    public static DetailInfoBean getDetailInfo(String json) {

        return new Gson().fromJson(json, DetailInfoBeanTools.class).getResponse();
    }

    public DetailInfoBean getResponse() {

        return response;
    }
}
