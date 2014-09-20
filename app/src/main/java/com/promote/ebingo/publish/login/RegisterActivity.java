package com.promote.ebingo.publish.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.promote.ebingo.BaseActivity;
import com.promote.ebingo.R;
import com.promote.ebingo.util.ContextUtil;

/**
 * Created by acer on 2014/9/2.
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener {
    private final String logTag = getClass().getSimpleName();
    EditText edit_phone;
    public static final int REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_get_yzm);
        edit_phone = (EditText) findViewById(R.id.edit_phone);
        ((TextView) findViewById(R.id.common_title_done)).setText(R.string.login_);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next: {
                final String phonenum = edit_phone.getText().toString().trim();//用户输入的手机号
                LoginManager manager = new LoginManager();
                if (manager.isMobile(phonenum)) {
                    manager.getYzm(RegisterActivity.this, phonenum, new LoginManager.Callback() {
                        @Override
                        public void onSuccess() {
                            {
                                Intent intent = new Intent(RegisterActivity.this, RegisterInputYzm.class);
                                intent.putExtra("phonenum", phonenum);
                                startActivityForResult(intent, REQUEST_CODE);
                            }
                        }

                    });
                } else {
                    ContextUtil.toast("请输入正确的手机号！");
                }

                break;
            }
            case R.id.common_title_done:
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                break;
            default:
                super.onClick(v);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            setResult(RESULT_OK, data);
            finish();
        }
    }
}
