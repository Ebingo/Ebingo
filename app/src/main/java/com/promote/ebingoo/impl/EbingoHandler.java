package com.promote.ebingoo.impl;

import android.text.TextUtils;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.promote.ebingoo.R;
import com.promote.ebingoo.application.HttpConstant;
import com.promote.ebingoo.util.ContextUtil;
import com.promote.ebingoo.util.LogCat;

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
                showError(statusCode, response.getString("msg"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            showError(statusCode, e.getLocalizedMessage());
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
        if (s != null) {
            showError(statusCode, "访问出错了");
        } else {
            showError(statusCode, null);
        }
        LogCat.w("EbingooHandler error:" + s);
    }

    @Override
    final public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
        LogCat.w("EbingooHandler error:" + errorResponse);
        if (errorResponse != null) {
            showError(statusCode, "数据异常");
        } else {
            showError(statusCode, null);
        }
        LogCat.i("--->", errorResponse + "");
    }

    @Override
    final public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
        LogCat.w("EbingooHandler error:" + errorResponse);
        if (errorResponse != null) {
            showError(statusCode, "数据异常");
        } else {
            showError(statusCode, null);
        }

        LogCat.i("--->", errorResponse + "");
    }

    private void showError(int statusCode, String msg) {
        if (TextUtils.isEmpty(msg)) {
            msg = ContextUtil.getString(R.string.net_error);
        }
        onFail(statusCode, msg);
        ContextUtil.toast(msg);
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
