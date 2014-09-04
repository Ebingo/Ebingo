package com.promote.ebingo.publish.login;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.jch.lib.util.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.promote.ebingo.application.HttpConstant;
import com.promote.ebingo.bean.Company;
import com.promote.ebingo.impl.EbingoRequestParmater;
import com.promote.ebingo.util.ContextUtil;
import com.promote.ebingo.util.LogCat;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;
import com.promote.ebingo.R;
/**
 * Created by acer on 2014/9/2.
 */
public class LoginDialog extends Dialog implements View.OnClickListener{
    private final String LOG_TAG=getClass().getSimpleName();
    private EditText edit_phone;
    private EditText edit_password;

    public LoginDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login_dialog);
        initWidgets();
    }

    private void initWidgets() {
        edit_phone=(EditText)findViewById(R.id.edit_user_name);
        edit_password=(EditText)findViewById(R.id.edit_password);
        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.tv_reg).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login:
                doLogin(edit_phone.getText().toString().trim(),edit_password.getText().toString().trim());
                break;
            case R.id.tv_reg:
                getContext().startActivity(new Intent(getContext(),RegisterActivity.class));
                dismiss();
                break;
        }
    }
    private void doLogin(String phone,String password){
        LogCat.i(LOG_TAG,"start Login");

        EbingoRequestParmater parmater=new EbingoRequestParmater(getContext());
        parmater.put("phonenum",phone);
        parmater.put("password",password);
        HttpUtil.post(HttpConstant.login,parmater,new JsonHttpResponseHandler("utf-8"){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    JSONObject result = response.getJSONObject("response");
                    if (result ==null){
                        ContextUtil.toast("登录失败");;
                    }else{
                        ContextUtil.toast(response);;
                        Company company=Company.getInstance();
                        company.setName(result.getString("company_name"));
                        company.setCompanyId(result.getInt("company_id"));
                        company.setVipType(result.getString("viptype"));
                        company.setIsLock(result.getString("is_lock"));
                        company.setWebsite(result.getString( "website"));
                        company.setRegion(result.getString( "region"));
                        company.setImage(result.getString( "image"));
                        dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                ContextUtil.toast(errorResponse);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                ContextUtil.toast(responseString);
            }
        });
    }
}
