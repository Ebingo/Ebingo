package com.promote.ebingo.impl;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.promote.ebingo.R;
import com.promote.ebingo.application.HttpConstant;
import com.promote.ebingo.util.ContextUtil;
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
                onFail(statusCode, response.getString("msg"));
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
        showError(statusCode);
    }

    @Override
    final public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
        showError(statusCode);
    }

    @Override
    final public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
        showError(statusCode);
    }

    private void showError(int statusCode) {
        String net_error = ContextUtil.getString(R.string.net_error);
        onFail(statusCode, net_error);
        ContextUtil.toast(net_error);
    }

    /**
     * 当返回code=100时，调用此方法
     *
     * @param statusCode
     * @param response
     */
    public abstract void onSuccess(int statusCode, JSONObject response);

    /**
     * 当code!=100时，调用此方法
     *
     * @param statusCode
     * @param msg
     */
    public abstract void onFail(int statusCode, String msg);

    @Override
    public abstract void onFinish();
}
