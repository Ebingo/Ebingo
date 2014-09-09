package com.promote.ebingo.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by acer on 2014/9/5.
 */
public class JsonUtil {
    public static <T> T get(String json,Class<T> cls){
        Type type = new TypeToken<T>() {
        }.getType();
        return (T)(new Gson().fromJson(json, type));
    }


    public static  <T> ArrayList <T>  getArray(JSONArray array,Class<T> cls) throws JSONException {
        ArrayList<T> list=new ArrayList<T>();
        Gson gson=new Gson();

        for (int i=0;i<array.length();i++){
            T t=gson.fromJson(array.get(i).toString(),cls);
            list.add(t);
        }
        return list;
    }
}
