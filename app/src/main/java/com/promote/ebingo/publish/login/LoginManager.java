package com.promote.ebingo.publish.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;

import com.jch.lib.util.DialogUtil;
import com.jch.lib.util.HttpUtil;
import com.jch.lib.util.MD5;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.promote.ebingo.application.HttpConstant;
import com.promote.ebingo.bean.Company;
import com.promote.ebingo.impl.EbingoRequestParmater;
import com.promote.ebingo.util.ContextUtil;
import com.promote.ebingo.util.LogCat;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 从服务端获取验证短信
 */
public class LoginManager {

    /**
     * 服务端返回100时，表示发送成功。返回101表示获取失败
     */
    public static final String OK="100";
    public static final String FAIL="101";
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
            final ProgressDialog dialog=DialogUtil.waitingDialog(context);
            HttpUtil.post(HttpConstant.getYzm, parmater, new JsonHttpResponseHandler("utf-8") {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        if (OK.equals(response.getJSONObject("response").getString("code"))) {
                            mCallback.onSuccess();
                        } else {
                            mCallback.onFail("获取验证码失败"+response);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        mCallback.onFail("获取验证码失败"+response);
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    mCallback.onFail("String:"+responseString);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    mCallback.onFail("JSONObject:"+errorResponse);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    mCallback.onFail("JSONArray:"+errorResponse);
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

    public void doLogin(String phone,String password,final Callback callback){

        EbingoRequestParmater parmater=new EbingoRequestParmater(ContextUtil.getContext());
        parmater.put("phonenum",phone);
        parmater.put("password",new MD5().getStrToMD5(password));
        HttpUtil.post(HttpConstant.login,parmater,new JsonHttpResponseHandler("utf-8"){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    JSONObject result = response.getJSONObject("response");
                    String str=null;
                    try {
                        str=result.getString("code");
                    }catch (Exception e){

                    }
                    if (result ==null|| FAIL.equals(str)){
                        callback.onFail(""+response);
                    }else{
                        callback.onSuccess();
                        ContextUtil.toast(response);;
                        Company company=Company.getInstance();
                        company.setName(result.getString("company_name"));
                        company.setCompanyId(result.getInt("company_id"));
                        company.setVipType(result.getString("viptype"));
                        company.setIsLock(result.getString("is_lock"));
                        company.setWebsite(result.getString( "website"));
                        company.setRegion(result.getString( "region"));
                        company.setImage(result.getString( "image"));
                    }
                } catch (JSONException e) {
                    callback.onFail(response+"");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                callback.onFail(errorResponse+"");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                callback.onFail(responseString + "");
            }
        });
    }

    public void setDefaultUser(){

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
            ContextUtil.toast("FAIL:"+msg);
        }
    }
}
