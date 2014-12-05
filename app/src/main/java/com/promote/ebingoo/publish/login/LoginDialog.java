package com.promote.ebingoo.publish.login;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import com.jch.lib.util.DialogUtil;
import com.promote.ebingoo.R;
import com.promote.ebingoo.application.EbingoApp;

/**
 * 登录窗口
 */
public class LoginDialog extends Dialog implements View.OnClickListener {
    public static final int REQUEST_CODE = 1001;
    private final String LOG_TAG = getClass().getSimpleName();
    private EditText edit_phone;
    private EditText edit_password;
    private int[] lineSelector;
    private View.OnFocusChangeListener fcl = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            View divider_user = findViewById(R.id.divider_user_name);
            View divider_password = findViewById(R.id.divider_password);
            switch (v.getId()) {
                case R.id.edit_user_name:
                    divider_user.setBackgroundColor(lineSelector[1]);
                    divider_password.setBackgroundColor(lineSelector[0]);
                    break;
                case R.id.edit_password:
                    divider_password.setBackgroundColor(lineSelector[1]);
                    divider_user.setBackgroundColor(lineSelector[0]);
                    break;
            }
        }
    };
    private LoginResult mLoginResult = null;

    public LoginDialog(Context context) {
        super(context);
    }

    /**
     * 设置LoginResult
     *
     * @param loginResult
     */
    public void setLoginResultCallback(LoginResult loginResult) {
        this.mLoginResult = loginResult;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login_dialog);
        initWidgets();
    }

    private void initWidgets() {
        lineSelector = new int[]{getContext().getResources().getColor(R.color.center_divider), 0xff069dd4};
        edit_phone = (EditText) findViewById(R.id.edit_user_name);
        edit_password = (EditText) findViewById(R.id.edit_password);
        edit_password.setOnFocusChangeListener(fcl);
        edit_phone.setOnFocusChangeListener(fcl);
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
                        if (mLoginResult != null) {
                            mLoginResult.loginResult(true);
                        }
                    }

                    @Override
                    public void onFail(String msg) {
                        super.onFail(msg);
                        dialog.dismiss();
                        if (mLoginResult != null) {
                            mLoginResult.loginResult(false);
                        }
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

    /**
     * 登录结果。*
     */
    public interface LoginResult {

        public void loginResult(boolean loginResult);
    }
}
