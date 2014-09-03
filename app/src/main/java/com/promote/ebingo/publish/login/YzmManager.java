package com.promote.ebingo.publish.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import com.jch.lib.util.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.promote.ebingo.application.HttpConstant;
import com.promote.ebingo.impl.EbingoRequestParmater;
import com.promote.ebingo.util.ContextUtil;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by acer on 2014/9/3.
 */
public class YzmManager {
    public void getYzm(Context context){
        {
            EbingoRequestParmater parmater=new EbingoRequestParmater(context);
            final ProgressDialog dialog=ProgressDialog.show(context,null,"正在获取验证码...");
            HttpUtil.post(HttpConstant.getYzm, parmater, new JsonHttpResponseHandler("utf-8") {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    dialog.dismiss();
                }
            });
        }
    }
    public void onFinish(boolean success){

    }
}
