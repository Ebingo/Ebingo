package com.promote.ebingoo.bean;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by ACER on 2014/9/3.
 */
public class SearchDemandBeanTools {

    private ArrayList<SearchDemandBean> response;

    public ArrayList<SearchDemandBean> getSearchDemands(){
        return response;
    }

    public static ArrayList<SearchDemandBean> getSearchDemands(String json){


        Type type = new TypeToken<SearchDemandBeanTools>() {
        }.getType();

        Gson gson = new Gson();

        return ((SearchDemandBeanTools)(gson.fromJson(json, type))).getSearchDemands();
    }

}
