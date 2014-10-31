package com.promote.ebingoo.publish.login;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.jch.lib.util.DialogUtil;
import com.jch.lib.util.HttpUtil;
import com.promote.ebingoo.BaseActivity;
import com.promote.ebingoo.R;
import com.promote.ebingoo.application.HttpConstant;
import com.promote.ebingoo.impl.EbingoHandler;
import com.promote.ebingoo.impl.EbingoRequestParmater;
import com.promote.ebingoo.publish.EbingoDialog;
import com.promote.ebingoo.util.ContextUtil;

import org.json.JSONObject;

public class FindPasswordActivity extends BaseActivity {
    private EditText edit_phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_passord);
        edit_phone = (EditText) findViewById(R.id.edit_phone);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                final ProgressDialog dialog= DialogUtil.waitingDialog(this,"正在发送验证邮件...");
                EbingoRequestParmater parmater = new EbingoRequestParmater(this);
                parmater.put("phone_num", edit_phone.getText().toString().trim());
                HttpUtil.post(HttpConstant.sendChangePasswordEmail, parmater, new EbingoHandler() {
                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        EbingoDialog dialog=new EbingoDialog(FindPasswordActivity.this);
                        dialog.setTitle(R.string.find_password_dialog_title);
                        dialog.setMessage(getString(R.string.find_password_dialog_message));
                        dialog.setPositiveButton(getString(R.string.i_know),dialog.DEFAULT_LISTENER);
                        dialog.show();
                    }

                    @Override
                    public void onFail(int statusCode, String msg) {
                        ContextUtil.toast(msg);
                    }

                    @Override
                    public void onFinish() {
                        dialog.dismiss();
                    }
                });
                break;
            default:
                super.onClick(v);
        }
    }
}
