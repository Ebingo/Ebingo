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

import com.jch.lib.util.DialogUtil;
import com.jch.lib.util.HttpUtil;
import com.jch.lib.util.MD5;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.promote.ebingo.R;
import com.promote.ebingo.application.HttpConstant;
import com.promote.ebingo.bean.Company;
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
    private Timer timer=null;
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
        startTimer2InvalidateButton();
    }

    private void commit(){
        EbingoRequestParmater parmater=new EbingoRequestParmater(this);
        String password=edit_password.getText().toString().trim();
        String passwordMD5=new MD5().getStrToMD5(password);
        parmater.put("yzm", edit_yzm.getText().toString().trim());
        parmater.put("phonenum", getIntent().getStringExtra("phonenum"));
        parmater.put("password",passwordMD5 );
        LogCat.i("--->",parmater.toString()+" 正在注册。。。");
        final ProgressDialog dialog= DialogUtil.waitingDialog(this);
        HttpUtil.post(HttpConstant.register,parmater,new JsonHttpResponseHandler("utf-8"){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                ContextUtil.toast(response);
                try {
                    JSONObject result=response.getJSONObject("response");
                    if (LoginManager.OK.equals(result.getString("code"))){
                        Company.getInstance().setCompanyId(result.getInt("company_id"));
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                ContextUtil.toast("注册失败！");
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dialog.dismiss();
            }
        });
    }

    /**
     * 启动计时器，更新按钮文字
     */
    private void startTimer2InvalidateButton(){
        timeLeft=MAX_TIME;
        if (timer!=null){
            timer.cancel();
            timer=null;
            task.cancel();
        }
        timer=new Timer();
        timer.schedule(task,1000,1000);
    }

    TimerTask task= new TimerTask() {
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
    };

     Handler buttonHandler=new Handler(new Handler.Callback() {
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
                new LoginManager().getYzm(RegisterInputYzm.this,getIntent().getStringExtra("phonenum"),new LoginManager.Callback(){

                @Override
                public void onSuccess() {
                   startTimer2InvalidateButton();
                }
            });
                break;
        }
    }
}
