package com.promote.ebingoo.bean;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Created by ACER on 2014/9/18.
 */
public class InterpriseInfoBeanTools {

    private InterpriseInfoBean response = null;

    public InterpriseInfoBean getResponse() {

        return response;
    }

    public static InterpriseInfoBean getInterpriseInfo(String json) {

        Type type = new TypeToken<InterpriseInfoBeanTools>() {
        }.getType();

        return ((InterpriseInfoBeanTools) new Gson().fromJson(json, type)).getResponse();
    }

}
