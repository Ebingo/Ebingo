package com.promote.ebingo.publish.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;

import com.jch.lib.util.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.promote.ebingo.application.HttpConstant;
import com.promote.ebingo.impl.EbingoRequestParmater;
import com.promote.ebingo.util.ContextUtil;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 从服务端获取验证短信
 */
public class LoginManager {

    /**
     * 服务端返回100时，表示发送成功。返回101表示获取失败
     */
    private final String OK="100";

    /**
     * 获取验证码
     * @param context
     * @param callback
     */
    public void getYzm(Context context,String phonenum,Callback callback){
        {
            final Callback mCallback=callback;
            EbingoRequestParmater parmater=new EbingoRequestParmater(context);
            parmater.put("phonenum",phonenum);
            final ProgressDialog dialog=ProgressDialog.show(context,null,"正在获取验证码...");
            HttpUtil.post(HttpConstant.getYzm, parmater, new JsonHttpResponseHandler("utf-8") {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        if ("100".equals(response.getJSONObject("response").getString("code"))) {
                            mCallback.onSuccess();
                        } else {
                            mCallback.onFail("获取验证码失败");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        mCallback.onFail("获取验证码失败");
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    mCallback.onFail("responseString");
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    dialog.dismiss();
                }
            });
        }
    }



    public boolean isMobile(String input){
        if (TextUtils.isEmpty(input))return false;
        else return input.matches("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
    }

    public static abstract class Callback{
        /**
         * 回调方法，获取成功时调用
         */
        public abstract void onSuccess();

        /**
         * 回调方法，获取失败时调用
         */
        public void onFail(String msg){
            ContextUtil.toast(msg);
        }
    }
}
