package com.promote.ebingo.publish.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jch.lib.util.DialogUtil;
import com.jch.lib.util.TextUtil;
import com.jch.lib.util.VaildUtil;
import com.promote.ebingo.R;
import com.promote.ebingo.application.EbingoApp;

/**
 * Created by acer on 2014/9/3.
 * <p/>
 * 登录。
 */
public class LoginActivity extends Activity implements View.OnClickListener {

    private EditText editusername;
    private EditText editpassword;
    private Button btndone;
    private TextView tvforgetpassword;
    private TextView tvreg;
    private ImageView commonbackbtn;
    private TextView commontitletv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initialize();
    }

    private void initialize() {
        commonbackbtn = (ImageView) findViewById(R.id.common_back_btn);
        commontitletv = (TextView) findViewById(R.id.common_title_tv);
        editusername = (EditText) findViewById(R.id.edit_user_name);
        editpassword = (EditText) findViewById(R.id.edit_password);
        btndone = (Button) findViewById(R.id.common_title_done);
        tvforgetpassword = (TextView) findViewById(R.id.tv_forget_password);
        tvreg = (TextView) findViewById(R.id.tv_reg);
        tvreg.setOnClickListener(this);
        commonbackbtn.setOnClickListener(this);
        commontitletv.setText(getResources().getString(R.string.login_));
        btndone.setOnClickListener(this);
        editusername.setText(((EbingoApp) getApplicationContext()).getCurCompanyName());
        editpassword.setText(((EbingoApp) getApplicationContext()).getCurCompanyPwd());
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        switch (id) {

            case R.id.common_back_btn: {
                onBackPressed();
                this.finish();
                break;
            }
            case R.id.common_title_done: {
                String message = null;
                String name = editusername.getText().toString().trim();
                String pwd = editpassword.getText().toString().trim();
                if (TextUtil.stringIsNull(name)) {
                    Toast.makeText(LoginActivity.this, "请输入账号！", Toast.LENGTH_SHORT).show();
                    return;
                }
                message = VaildUtil.validPassword(pwd);
                if (!message.equals("")) {
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                    return;
                }

                login(name, pwd);

                break;
            }

            case R.id.tv_reg: {
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
                break;

            }
            default: {

            }

        }
    }

    /**
     * 登录。
     *
     * @param name
     * @param pwd
     */
    private void login(String name, String pwd) {

        final ProgressDialog dialog = DialogUtil.waitingDialog(LoginActivity.this);
        new LoginManager().doLogin(name, pwd, new LoginManager.Callback() {
            @Override
            public void onSuccess() {
                dialog.dismiss();
                LoginActivity.this.finish();
            }

            @Override
            public void onFail(String msg) {
                super.onFail(msg);
                dialog.dismiss();
            }
        });
    }

}