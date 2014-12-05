package com.promote.ebingoo.impl;

import android.content.Context;

import com.google.gson.Gson;
import com.jch.lib.util.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.promote.ebingoo.application.HttpConstant;
import com.promote.ebingoo.bean.HotKey;
import com.promote.ebingoo.bean.ResponseBaseBean;

import org.apache.http.Header;
import org.json.JSONObject;

/**
 * Created by ACER on 2014/11/12.
 */
public class SearchKeyRequest {

    private ResponseBaseBean<HotKey> response;

    /**
     * 获得搜索关键词。
     *
     * @param context
     */
    public static void getHotSearchKeyWords(Context context, final SearchKeyCallBack callBack) {

        HttpUtil.post(HttpConstant.getHotSearchKeywords, new EbingoRequestParmater(context), new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                ResponseBaseBean<HotKey> responseBaseBean = getHotKeyfromJson(response.toString());
                if (responseBaseBean != null && responseBaseBean.getCode() == 100) {
                    callBack.onSuccess(responseBaseBean.getData());
                } else {
                    callBack.onFailure("网络连接错误");
                }

                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                callBack.onFailure(throwable.getMessage());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                callBack.onFailure(responseString);
            }
        });
    }

    public static ResponseBaseBean<HotKey> getHotKeyfromJson(String jsonStr) {

        Gson gson = new Gson();
        ResponseBaseBean<HotKey> response = ((SearchKeyRequest) gson.fromJson(jsonStr, SearchKeyRequest.class)).getResponse();
        return response;
    }

    public ResponseBaseBean<HotKey> getResponse() {

        return this.response;
    }

    public interface SearchKeyCallBack {

        public void onSuccess(HotKey hotKey);

        public void onFailure(String msg);

    }

}
