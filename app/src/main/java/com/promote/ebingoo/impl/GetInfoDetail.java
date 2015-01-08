package com.promote.ebingoo.impl;

import android.util.Log;

import com.jch.lib.util.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.promote.ebingoo.application.HttpConstant;
import com.promote.ebingoo.bean.DetailInfoBean;
import com.promote.ebingoo.bean.DetailInfoBeanTools;
import com.promote.ebingoo.util.LogCat;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ACER on 2014/9/12.
 */
public class GetInfoDetail {

    public static void getInfoDetail(EbingoRequestParmater parmater, final CallBack callBack) {
        String urlStr = HttpConstant.getInfoDetail;
        LogCat.i("--->", parmater + "");

        HttpUtil.post(urlStr, parmater, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i("", "--->" + response + "");
                DetailInfoBean detailInfo = null;
                try {
                    JSONObject data = response.getJSONObject("response");
                    LogCat.i("--->", response + "");
                    if (HttpConstant.CODE_OK.equals(data.getString("code"))) {

                        detailInfo = DetailInfoBeanTools.getDetailInfo(data.toString());
//                        detailInfo = JsonUtil.get(response.getJSONObject("data").toString(), DetailInfoBean.class);     //
                        if (detailInfo != null) {
                            callBack.onSuccess(detailInfo);
                        } else {
                            String msg = "获取数据错误";
                            callBack.onFailed(msg);
                            // TODO 获取数据失败。
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    callBack.onFailed("获取数据错误");
                }

                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                String msg = "获取数据失败";
                callBack.onFailed(msg);
                LogCat.i("--->", errorResponse + "");
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                LogCat.i("--->", responseString + "");
                String msg = "获取数据失败";
                callBack.onFailed(msg);
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }

    public interface CallBack {

        public void onFailed(String msg);

        public void onSuccess(DetailInfoBean detailInfoBean);
    }

}
