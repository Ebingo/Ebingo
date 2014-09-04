package com.promote.ebingo.publish.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.jch.lib.util.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.promote.ebingo.R;
import com.promote.ebingo.application.HttpConstant;
import com.promote.ebingo.impl.EbingoRequestParmater;
import com.promote.ebingo.util.ContextUtil;
import com.promote.ebingo.util.LogCat;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by acer on 2014/9/2.
 */
public class RegisterActivity extends Activity implements View.OnClickListener{
    private final String logTag = getClass().getSimpleName();
    EditText edit_phone;
    private final String OK="100";
    private final String FAIL="101";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_get_yzm);
        edit_phone=(EditText)findViewById(R.id.edit_phone);
        findViewById(R.id.common_back_btn).setOnClickListener(this);
    }
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn_getYZM:
            {
                final String phonenum=edit_phone.getText().toString().trim();//用户输入的手机号
                LoginManager manager=new LoginManager();
                if(manager.isMobile(phonenum)){
                    manager.getYzm(RegisterActivity.this,phonenum,new LoginManager.Callback() {
                        @Override
                        public void onSuccess() {
                            {
                                Intent intent=new Intent(RegisterActivity.this, RegisterInputYzm.class);
                                intent.putExtra("phonenum",phonenum);
                                startActivityForResult(intent, 100);
                            }
                        }

                    });
                }else{
                    ContextUtil.toast("请输入正确的手机号！");
                }

                break;
            }
            case R.id.common_back_btn:
                finish();
                break;
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==100&&resultCode==RESULT_OK){
               finish();
        }
    }
}
