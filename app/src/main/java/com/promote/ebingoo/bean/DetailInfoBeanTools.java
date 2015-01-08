package com.promote.ebingoo.bean;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Created by ACER on 2014/9/12.
 */
public class DetailInfoBeanTools {

    private DetailInfoBean data;
    private int code;

    public static DetailInfoBean getDetailInfo(String json) {
        Type type = new TypeToken<DetailInfoBeanTools>() {
        }.getType();
        return ((DetailInfoBeanTools) new Gson().fromJson(json, type)).getData();

    }

    public DetailInfoBean getData() {

        return data;
    }
}
