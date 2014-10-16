package com.promote.ebingo.publish.login;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.jch.lib.util.DialogUtil;
import com.jch.lib.util.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.promote.ebingo.application.HttpConstant;
import com.promote.ebingo.bean.Company;
import com.promote.ebingo.bean.CompanyVipInfo;
import com.promote.ebingo.impl.EbingoHandler;
import com.promote.ebingo.impl.EbingoRequestParmater;
import com.promote.ebingo.publish.VipType;
import com.promote.ebingo.util.FileUtil;
import com.promote.ebingo.util.ContextUtil;
import com.promote.ebingo.util.ImageUtil;
import com.promote.ebingo.util.JsonUtil;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 从服务端获取验证短信
 */
public class LoginManager {
    public static final String ACTION_INVALIDATE = "com.promote.ebingo.center.ACTION_INVALIDATE";

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
            HttpUtil.post(HttpConstant.getYzm, parmater, new EbingoHandler() {
                @Override
                public void onSuccess(int statusCode, JSONObject response) {
                    mCallback.onSuccess();
                }

                @Override
                public void onFail(int statusCode, String msg) {
                    mCallback.onFail(msg);
                }

                @Override
                public void onFinish() {
                    dialog.dismiss();
                }
            });
        }
    }

    /**
     * 校验手机号
     *
     * @param input
     * @return
     */
    public static boolean isMobile(String input) {
        String a = "^1[34578][0-9]\\d{8}";
        String b = "^((13[0-9])|(14[0-9])|(15[^4,\\D])|(18[0-9])|(17[0-9]))\\d{8}$";
        if (TextUtils.isEmpty(input)) return false;
        else return input.matches(a);
    }

    /**
     * 校验固话
     *
     * @param input
     * @return
     */
    public static boolean isPhone(String input) {
        if (TextUtils.isEmpty(input)) return false;
        else return input.matches("\\d{3}-\\d{8}|\\d{4}-\\d{7}|\\d{4}-\\d{8}");
    }


    public void doLogin(final String phone, final String password, final Callback callback) {

        final EbingoRequestParmater parmater = new EbingoRequestParmater(ContextUtil.getContext());
//        final String md5Pwd = new MD5().getStrToMD5(password);
        parmater.put("phonenum", phone);
        parmater.put("password", password);
        HttpUtil.post(HttpConstant.login, parmater, new EbingoHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                JSONObject data = null;
                try {
                    data = response.getJSONObject("data");
                    Company company = JsonUtil.get(data.toString(), Company.class);
                    Company.loadInstance(company);

                    FileUtil.saveFile(ContextUtil.getContext(), FileUtil.FILE_COMPANY, company);
                    ContextUtil.saveCurCompanyName(phone);
                    ContextUtil.saveCurCompanyPwd(password);
                    parmater.put("company_id", Company.getInstance().getCompanyId());
                    HttpUtil.post(HttpConstant.getCompanyVipInfo, parmater, new EbingoHandler() {
                        @Override
                        public void onSuccess(int statusCode, JSONObject response) {
                            CompanyVipInfo vipInfo = null;
                            try {
                                vipInfo = JsonUtil.get(String.valueOf(response.getJSONObject("data")), CompanyVipInfo.class);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            VipType companyVip = VipType.getCompanyInstance();
                            switch (companyVip) {
                                case VISITOR: {//禁用：发布求购信息、拨打供应电话
                                    vipInfo.setPublishDemandInfo(false);
                                    vipInfo.setCallSupply(false);
                                }
                                case Experience_Vip: {//禁用查看求购信息、拨打求购电话
                                    vipInfo.setCanLookDemandCompany(false);
                                    vipInfo.setCallDemand(false);
                                }
                                case Standard_VIP: {

                                    break;
                                }
                            }
                            Company.getInstance().setVipInfo(vipInfo);
                            callback.onSuccess();
                        }

                        @Override
                        public void onFail(int statusCode, String msg) {
                            callback.onFail(msg);
                        }

                        @Override
                        public void onFinish() {

                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFail(int statusCode, String msg) {
                callback.onFail(msg + "");
            }

            @Override
            public void onFinish() {

            }
        });

    }

    /**
     * 获取头像并保存在本地
     *
     * @param url
     * @param handler
     */
    private void loadHeadImage(final String url, final Handler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = ImageUtil.getImageFromWeb(url);
                Uri uri = ImageUtil.saveBitmap(bitmap, "company_image.png");
                Company.getInstance().setImageUri(uri);
                handler.sendMessage(handler.obtainMessage(1, uri));
            }
        }).start();

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
            ContextUtil.toast("" + msg);
        }
    }
}
