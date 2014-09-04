package com.promote.ebingo.publish.login;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.jch.lib.util.DialogUtil;
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
               final ProgressDialog dialog= DialogUtil.waitingDialog(getContext());
                new LoginManager().doLogin(edit_phone.getText().toString().trim(), edit_password.getText().toString().trim(), new LoginManager.Callback() {
                    @Override
                    public void onSuccess() {
                        dialog.dismiss();
                        dismiss();
                    }

                    @Override
                    public void onFail(String msg) {
                        super.onFail(msg);
                        dialog.dismiss();
                    }
                });
                break;
            case R.id.tv_reg:
                getContext().startActivity(new Intent(getContext(),RegisterActivity.class));
                dismiss();
                break;
        }
    }
}
