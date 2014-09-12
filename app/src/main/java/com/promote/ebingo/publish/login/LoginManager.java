package com.promote.ebingo.publish.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;

import com.jch.lib.util.DialogUtil;
import com.jch.lib.util.HttpUtil;
import com.jch.lib.util.MD5;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.promote.ebingo.application.EbingoApp;
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
     * 获取验证码
     *
     * @param context
     * @param callback
     */
    public void getYzm(Context context, String phonenum, Callback callback) {
        {
            final Callback mCallback = callback;
            EbingoRequestParmater parmater = new EbingoRequestParmater(context);
            parmater.put("phonenum", phonenum);
            final ProgressDialog dialog = DialogUtil.waitingDialog(context);
            HttpUtil.post(HttpConstant.getYzm, parmater, new JsonHttpResponseHandler("utf-8") {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        if (HttpConstant.CODE_OK.equals(response.getJSONObject("response").getString("code"))) {
                            mCallback.onSuccess();
                        } else {
                            mCallback.onFail("获取验证码失败" + response);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        mCallback.onFail("获取验证码失败" + response);
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    mCallback.onFail("String:" + responseString);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    mCallback.onFail("JSONObject:" + errorResponse);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    mCallback.onFail("JSONArray:" + errorResponse);
                }


                @Override
                public void onFinish() {
                    super.onFinish();
                    dialog.dismiss();
                }
            });
        }
    }


    public static boolean isMobile(String input) {
        if (TextUtils.isEmpty(input)) return false;
        else return input.matches("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
    }

    public void doLogin(final String phone, final String password, final Callback callback) {

        EbingoRequestParmater parmater = new EbingoRequestParmater(ContextUtil.getContext());
//        final String md5Pwd = new MD5().getStrToMD5(password);
        parmater.put("phonenum", phone);
        parmater.put("password", password);
        HttpUtil.post(HttpConstant.login, parmater, new JsonHttpResponseHandler("utf-8") {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    JSONObject result = response.getJSONObject("response");

                    if (HttpConstant.CODE_OK.equals(result.getString("code"))) {
                        callback.onSuccess();
                        LogCat.i(response+"");
                        JSONObject data=result.getJSONObject("data");
                        Company company = Company.getInstance();
                        company.setName(data.getString("company_name"));
                        company.setCompanyId(data.getInt("company_id"));
                        company.setVipType(data.getString("viptype"));
                        company.setIsLock(data.getString("is_lock"));
                        company.setWebsite(data.getString("website"));
                        company.setRegion(data.getString("region"));
                        company.setImage(data.getString("image"));
                        company.setRegion(data.getString("region"));
                        ((EbingoApp) ContextUtil.getContext()).saveCurCompanyName(phone);
                        ((EbingoApp) ContextUtil.getContext()).saveCurCompanyPwd(password);
                    } else {
                        callback.onFail("" + response);
                    }
                } catch (JSONException e) {
                    callback.onFail(response + "");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                callback.onFail(errorResponse + "");
            }

        });
    }

    public void setDefaultUser() {

    }

    public static abstract class Callback {
        /**
         * 回调方法，获取成功时调用
         */
        public abstract void onSuccess();

        /**
         * 回调方法，获取失败时调用
         */
        public void onFail(String msg) {
            ContextUtil.toast("FAIL:" + msg);
        }
    }
}
