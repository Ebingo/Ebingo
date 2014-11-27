package com.promote.ebingoo.bean;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by ACER on 2014/9/3.
 */
public class SearchInterpriseBeanTools {

    private ArrayList<SearchInterpriseBean> response;

    public static ArrayList<SearchInterpriseBean> getSearchTypeBeans(String json) {

        Gson gson = new Gson();
        Type type = new TypeToken<SearchInterpriseBeanTools>() {
        }.getType();

        return ((SearchInterpriseBeanTools) (gson.fromJson(json, type))).getSearchInterprise();

    }

    public ArrayList<SearchInterpriseBean> getSearchInterprise() {
        return response;
    }

}
