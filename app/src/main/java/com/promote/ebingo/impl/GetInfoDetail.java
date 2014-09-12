package com.promote.ebingo.impl;

import com.jch.lib.util.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.promote.ebingo.application.HttpConstant;
import com.promote.ebingo.bean.DetailInfoBean;
import com.promote.ebingo.bean.DetailInfoBeanTools;

import org.apache.http.Header;
import org.json.JSONObject;

/**
 * Created by ACER on 2014/9/12.
 */
public class GetInfoDetail {

    public interface CallBack {

        public void onFailed(String msg);

        public void onSuccess(DetailInfoBean detailInfoBean);
    }

    public static void getInfoDetail(EbingoRequestParmater parmater, final CallBack callBack) {
        String urlStr = HttpConstant.getInfoDetail;


        HttpUtil.post(urlStr, parmater, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                DetailInfoBean detailInfo = DetailInfoBeanTools.getDetailInfo(response.toString());
                if (detailInfo != null) {
                    callBack.onSuccess(detailInfo);
                } else {

                    String msg = "获取数据错误";
                    callBack.onFailed(msg);
                    // TODO 获取数据失败。
                }

                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                String msg = "获取数据失败";
                callBack.onFailed(msg);
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                String msg = "获取数据失败";
                callBack.onFailed(msg);
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }

}
