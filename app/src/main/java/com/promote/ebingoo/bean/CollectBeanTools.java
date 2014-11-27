package com.promote.ebingoo.bean;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by ACER on 2014/9/12.
 */
public class CollectBeanTools {

    private ArrayList<CollectBean> response;

    public static ArrayList<CollectBean> getCollections(String json) {

        Type type = new TypeToken<CollectBeanTools>() {
        }.getType();

        return ((CollectBeanTools) new Gson().fromJson(json, type)).getResponse();
    }

    public ArrayList<CollectBean> getResponse() {
        return response;
    }
}
