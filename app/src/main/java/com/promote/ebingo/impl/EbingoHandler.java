package com.promote.ebingo.impl;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.promote.ebingo.application.HttpConstant;
import com.promote.ebingo.util.LogCat;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 继承JsonHttpResponseHandler，这里只处理业务逻辑结果
 * Created by acer on 2014/9/16.
 */
public abstract class EbingoHandler extends JsonHttpResponseHandler {
    public EbingoHandler() {
        super("utf-8");
    }

    @Override
   final public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        LogCat.i("--->", response + "");
        try {
            response = response.getJSONObject("response");
            if (HttpConstant.CODE_OK.equals(response.getString("code"))) {
                onSuccess(statusCode, response);
            } else {
                onFail(statusCode, response + "");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    final public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
    }

    @Override
    final public void onSuccess(int statusCode, Header[] headers, String responseString) {
    }

    @Override
    final public void onFailure(int statusCode, org.apache.http.Header[] headers, java.lang.String s, java.lang.Throwable throwable) {
        onFail(statusCode, s);
    }

    @Override
    final public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
    }

    @Override
    final public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
    }

    public abstract void onSuccess(int statusCode, JSONObject response);

    public abstract void onFail(int statusCode, String msg);

    @Override
    public abstract void onFinish();
}
