package com.promote.ebingoo.publish.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.promote.ebingoo.BaseActivity;
import com.promote.ebingoo.R;
import com.promote.ebingoo.util.ContextUtil;

/**
 * Created by acer on 2014/9/2.
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    public static final int REQUEST_CODE = 1001;
    EditText edit_phone;
    private String protocolStr = null;
    //是否阅读协议
    private boolean isRead = false;
    private CheckBox protocolcb;
    private TextView protocoltv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_get_yzm);
        edit_phone = (EditText) findViewById(R.id.edit_phone);
        ((TextView) findViewById(R.id.commit_title_done)).setText(R.string.login_);
        initialize();
        initialize();
        initialize();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next: {
                final String phonenum = edit_phone.getText().toString().trim();//用户输入的手机号
                LoginManager manager = new LoginManager();
                if (manager.isMobile(phonenum) && isRead) {
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
                } else if (!isRead) {
                    ContextUtil.toast(getResources().getString(R.string.read_pro_warn));

                } else {
                    ContextUtil.toast("请输入正确的手机号！");
                }

                break;
            }

            case R.id.commit_title_done:
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

    private void initialize() {

        protocolcb = (CheckBox) findViewById(R.id.protocol_cb);
        protocolcb.setOnCheckedChangeListener(this);
        protocoltv = (TextView) findViewById(R.id.protocol_tv);


        appendTvSpannable();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        isRead = isChecked;
    }

    private void appendTvSpannable() {
        SpannableString ss = new SpannableString(getString(R.string.agreet));
        ss.setSpan(new ReadProtocolMethod(), 2, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        protocoltv.setText(ss);
        protocoltv.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private class ReadProtocolMethod extends ClickableSpan {
        @Override
        public void onClick(View widget) {

            ProtocolDialog protocolDialog = ProtocolDialog.build(RegisterActivity.this);
            protocolDialog.setProTocolHtm("file:///android_asset/protocol.htm");
            protocolDialog.show();
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setColor(getResources().getColor(R.color.right_blue));
        }


    }
}
