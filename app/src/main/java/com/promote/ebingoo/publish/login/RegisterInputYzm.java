package com.promote.ebingoo.publish.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.jch.lib.util.DialogUtil;
import com.jch.lib.util.HttpUtil;
import com.promote.ebingoo.BaseActivity;
import com.promote.ebingoo.R;
import com.promote.ebingoo.application.HttpConstant;
import com.promote.ebingoo.bean.Company;
import com.promote.ebingoo.center.settings.EnterpriseSettingActivity;
import com.promote.ebingoo.impl.EbingoHandler;
import com.promote.ebingoo.impl.EbingoRequestParmater;
import com.promote.ebingoo.impl.VerifyCreator;
import com.promote.ebingoo.util.ContextUtil;
import com.promote.ebingoo.util.LogCat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import static android.text.InputType.TYPE_CLASS_TEXT;
import static android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD;
import static android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;
import static com.promote.ebingoo.publish.login.RegisterActivity.REQUEST_CODE;

/**
 * Created by acer on 2014/9/3.
 */
public class RegisterInputYzm extends BaseActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    /**
     * 重新获取验证码的时间间隔
     */
    private final int MAX_TIME = 20;
    /**
     * 重新获取验证码剩余时间
     */
    int timeLeft = MAX_TIME;
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            timeLeft--;
            Message msg = new Message();
            if (timeLeft <= 0) {
                this.cancel();
                msg.what = 2;
            } else {
                msg.what = 1;
                msg.arg1 = timeLeft;
            }
            buttonHandler.sendMessage(msg);
        }
    };
    Button btn_getYzm;
    ImageView verify_image;
    Handler buttonHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 1) {
                btn_getYzm.setText(msg.arg1 + "秒后\n重新获取");
                btn_getYzm.setEnabled(false);
            } else {
                btn_getYzm.setText("重新获取");
                btn_getYzm.setEnabled(true);
            }
            return false;
        }
    });
    private Timer timer = null;
    private EditText edit_yzm;
    private EditText edit_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_input_yzm);
        ((ToggleButton) findViewById(R.id.password_toggle)).setOnCheckedChangeListener(this);
        edit_yzm = (EditText) findViewById(R.id.edit_yzm);
        edit_password = (EditText) findViewById(R.id.edit_password);
        verify_image = (ImageView) findViewById(R.id.image_verify);
        btn_getYzm = (Button) findViewById(R.id.btn_next);
        findViewById(R.id.common_back_btn).setOnClickListener(this);
        findViewById(R.id.btn_next).setOnClickListener(this);
        startTimer2InvalidateButton();
        invalidateVerify();
        ((TextView) findViewById(R.id.commit_title_done)).setText(R.string.login_);
    }

    private void invalidateVerify() {
        VerifyCreator creator = VerifyCreator.getInstance();
        Bitmap bitmap = creator.createBitmap();
        String code = creator.getCode();
        verify_image.setImageBitmap(bitmap);
        verify_image.setContentDescription(code);
        verify_image.setOnClickListener(this);
    }

    private void commit() {
        final String yzm = edit_yzm.getText().toString().trim();

        if (!checkVerify(yzm)) {
            ContextUtil.toast("验证码错误！");
            return;
        }
        final String password = edit_password.getText().toString().trim();
        final String phoneNum = getIntent().getStringExtra("phonenum");

        EbingoRequestParmater parmater = new EbingoRequestParmater(this);
        parmater.put("yzm", yzm);
        parmater.put("phonenum", phoneNum);
        parmater.put("password", password);
        final ProgressDialog dialog = DialogUtil.waitingDialog(this);
        HttpUtil.post(HttpConstant.register, parmater, new EbingoHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    Company.getInstance().setCompanyId(response.getInt("company_id"));
                    Company.getInstance().setVipType(response.getString("vip_type"));
                    doLogin(phoneNum, password);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(int statusCode, String msg) {
                ContextUtil.toast("注册失败！");
            }

            @Override
            public void onFinish() {
                dialog.dismiss();
            }
        });
    }

    private void doLogin(String phone, String password) {
        new LoginManager().doLogin(phone, password, new LoginManager.Callback() {
            @Override
            public void onSuccess() {
                Intent intent = new Intent(RegisterInputYzm.this, EnterpriseSettingActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                setResult(RESULT_OK, new Intent());
                finish();
            }

            @Override
            public void onFail(String msg) {
                super.onFail(msg);
            }
        });
    }

    private boolean checkVerify(String input) {
        String code = verify_image.getContentDescription().toString().toLowerCase();
        String input_code = input.toLowerCase();
        LogCat.i("--->", "input=" + input_code + " code:" + code);
        return code.equals(input_code);
    }

    /**
     * 启动计时器，更新按钮文字
     */
    private void startTimer2InvalidateButton() {
//        timeLeft=MAX_TIME;
//        if (timer!=null){
//            timer.cancel();
//            timer=null;
//            task.cancel();
//        }
//        timer=new Timer();
//        timer.schedule(task,1000,1000);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            edit_password.setInputType(TYPE_TEXT_VARIATION_PASSWORD | TYPE_CLASS_TEXT);
        } else {
            edit_password.setInputType(TYPE_TEXT_VARIATION_VISIBLE_PASSWORD | TYPE_CLASS_TEXT);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_done: {
                commit();
                break;
            }
            case R.id.common_back_btn:
                finish();
                break;
//            case R.id.btn_next:
//                new LoginManager().getYzm(RegisterInputYzm.this, getIntent().getStringExtra("phonenum"), new LoginManager.Callback() {
//
//                    @Override
//                    public void onSuccess() {
//                        startTimer2InvalidateButton();
//                    }
//                });
//                break;
            case R.id.image_verify:
                invalidateVerify();
                break;
            case R.id.commit_title_done:
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            setResult(RESULT_OK, data);
            finish();
        } else {
            invalidateVerify();
        }
    }
}
