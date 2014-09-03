package com.promote.ebingo.publish.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.jch.lib.util.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.promote.ebingo.R;
import com.promote.ebingo.application.HttpConstant;
import com.promote.ebingo.impl.EbingoRequestParmater;
import com.promote.ebingo.util.ContextUtil;
import com.promote.ebingo.util.LogCat;

import android.os.Handler;
import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import static android.text.InputType.*;
/**
 * Created by acer on 2014/9/3.
 */
public class RegisterInputYzm extends Activity implements CompoundButton.OnCheckedChangeListener,View.OnClickListener{

    /**
     * 重新获取验证码的时间间隔
     */
    private final int MAX_TIME=20;
    /**
     * 重新获取验证码剩余时间
     */
    int timeLeft=MAX_TIME;

    private EditText edit_yzm;
    private EditText edit_password;
    Button btn_getYzm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_input_yzm);
        ((ToggleButton)findViewById(R.id.password_toggle)).setOnCheckedChangeListener(this);
        edit_yzm=(EditText)findViewById(R.id.edit_yzm);
        edit_password=(EditText)findViewById(R.id.edit_password);
        btn_getYzm=(Button)findViewById(R.id.btn_getYZM);
        findViewById(R.id.common_back_btn).setOnClickListener(this);
        findViewById(R.id.btn_getYZM).setOnClickListener(this);
    }

    private void commit(){
        final String password=edit_password.getText().toString().trim();
        final String yzm=edit_yzm.getText().toString().trim();
        EbingoRequestParmater parmater=new EbingoRequestParmater(this);
        parmater.put("yzm",yzm);
        parmater.put("password",password);
        HttpUtil.post(HttpConstant.register,parmater,new JsonHttpResponseHandler("utf-8"){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Toast.makeText(getApplicationContext(),response.toString(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(getApplicationContext(),responseString,Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 启动计时器，更新按钮文字
     */
    private void startTimer2InvalidateButton(){

        final Handler buttonHandler=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what==1){
                    btn_getYzm.setText(msg.arg1+"秒后\n重新获取");
                    btn_getYzm.setEnabled(false);
                }else{
                    btn_getYzm.setText("重新获取");
                    btn_getYzm.setEnabled(true);
                }
                return false;
            }
        });

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                timeLeft--;
                Message msg=new Message();
                if(timeLeft<=0){
                    this.cancel();
                    msg.what=2;
                }else{
                    msg.what=1;
                    msg.arg1=timeLeft;
                }
                buttonHandler.sendMessage(msg);
            }
        },0,1000);

    }
    /**
     * 从后台获取验证码
     */
    private void getYzm() {
        EbingoRequestParmater parmater=new EbingoRequestParmater(this);
        final ProgressDialog dialog=ProgressDialog.show(RegisterInputYzm.this,null,"正在获取验证码...");
        HttpUtil.post(HttpConstant.getYzm, parmater, new JsonHttpResponseHandler("utf-8") {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    if ("100".equals(response.getJSONObject("response").getString("code"))) {
                        startActivityForResult(new Intent(RegisterInputYzm.this, RegisterInputYzm.class), 100);
                    } else {
                        ContextUtil.toast("获取验证码失败");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFinish() {
                super.onFinish();
                dialog.dismiss();
            }
        });
    }
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
            edit_password.setInputType(TYPE_TEXT_VARIATION_PASSWORD|TYPE_CLASS_TEXT);
        }else{
            edit_password.setInputType(TYPE_TEXT_VARIATION_VISIBLE_PASSWORD|TYPE_CLASS_TEXT);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_done:
                commit();
                break;
            case R.id.common_back_btn:
                finish();
                break;
            case R.id.btn_getYZM:
                getYzm();
                break;
        }
    }
}
