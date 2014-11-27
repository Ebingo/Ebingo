package com.promote.ebingoo.bean;

import com.google.gson.Gson;

/**
 * Created by ACER on 2014/11/26.
 */
public class ResponseBean<T> {

    private ResponseBaseBean<T> response;

    public static ResponseBaseBean getResponse(String jsonStr) {

        Gson gson = new Gson();
        return ((ResponseBean) gson.fromJson(jsonStr, ResponseBean.class)).getResponseBaseBean();
    }

    public ResponseBaseBean<T> getResponseBaseBean() {

        return response;
    }

}
