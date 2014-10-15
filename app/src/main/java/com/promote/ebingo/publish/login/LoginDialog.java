package com.promote.ebingo.publish.login;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import com.jch.lib.util.DialogUtil;
import com.promote.ebingo.R;
import com.promote.ebingo.application.EbingoApp;
import com.promote.ebingo.center.CenterFragment;

/**
 * 登录窗口
 */
public class LoginDialog extends Dialog implements View.OnClickListener {
    private final String LOG_TAG = getClass().getSimpleName();
    public static final int REQUEST_CODE = 1001;
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
        edit_phone = (EditText) findViewById(R.id.edit_user_name);
        edit_password = (EditText) findViewById(R.id.edit_password);
        edit_phone.setText(((EbingoApp) getContext().getApplicationContext()).getCurCompanyName());
        edit_password.setText(((EbingoApp) getContext().getApplicationContext()).getCurCompanyPwd());
        findViewById(R.id.commit_title_done).setOnClickListener(this);
        findViewById(R.id.tv_reg).setOnClickListener(this);
        findViewById(R.id.tv_forget_password).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commit_title_done:
                final ProgressDialog dialog = DialogUtil.waitingDialog(getContext());
                new LoginManager().doLogin(edit_phone.getText().toString().trim(), edit_password.getText().toString().trim(), new LoginManager.Callback() {
                    @Override
                    public void onSuccess() {
                        dialog.dismiss();
                        dismiss();
                        getContext().sendBroadcast(new Intent(LoginManager.ACTION_INVALIDATE));
                    }

                    @Override
                    public void onFail(String msg) {
                        super.onFail(msg);
                        dialog.dismiss();
                    }
                });
                break;
            case R.id.tv_reg:

                if (getOwnerActivity() != null) {
                    getOwnerActivity().startActivityForResult(new Intent(getContext(), RegisterActivity.class), REQUEST_CODE);
                }
                dismiss();
                break;
            case R.id.tv_forget_password:
                if (getOwnerActivity() != null) {
                    getOwnerActivity().startActivity(new Intent(getContext(), FindPasswordActivity.class));
                }
                dismiss();
                break;

        }
    }

    @Override
    public void onBackPressed() {
        dismiss();
    }
}
