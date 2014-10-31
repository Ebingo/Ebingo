package com.promote.ebingoo.bean;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by ACER on 2014/9/3.
 */
public class SearchSupplyBeanTools {

    private ArrayList<SearchSupplyBean> response;

    public ArrayList<SearchSupplyBean> getSearchSupplyBeans(){
        return response;
    }

    /**
     *
     * @param json
     * @return
     */
    public static ArrayList<SearchSupplyBean> getSearchSupplyBeans(String json){

        Type type = new TypeToken<SearchSupplyBeanTools>() {
        }.getType();
        Gson gson = new Gson();

        return ((SearchSupplyBeanTools) (gson.fromJson(json, type))).getSearchSupplyBeans();
    }
}
