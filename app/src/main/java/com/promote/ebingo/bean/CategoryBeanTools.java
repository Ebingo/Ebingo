package com.promote.ebingo.bean;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by ACER on 2014/9/5.
 */
public class CategoryBeanTools {
    private ArrayList<CategoryBeen> response;

    public ArrayList<CategoryBeen> getResponse() {
        return response;
    }

    public void setResponse(ArrayList<CategoryBeen> response) {
        this.response = response;
    }


    public static ArrayList<CategoryBeen> getCategories(String json){
        Gson gson = new Gson();
        Type type =new  TypeToken<CategoryBeanTools>(){}.getType();

        return ((CategoryBeanTools)gson.fromJson(json, type)).getResponse();
    }

}
