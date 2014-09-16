package com.promote.ebingo.center.settings;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.jch.lib.util.DialogUtil;
import com.jch.lib.util.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.promote.ebingo.BaseActivity;
import com.promote.ebingo.R;
import com.promote.ebingo.application.HttpConstant;
import com.promote.ebingo.bean.Company;
import com.promote.ebingo.impl.EbingoHandler;
import com.promote.ebingo.impl.EbingoRequestParmater;
import com.promote.ebingo.util.ContextUtil;
import com.promote.ebingo.util.LogCat;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by acer on 2014/9/16.
 */
public class SuggestionActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commit:
                final Dialog dialog= DialogUtil.waitingDialog(this,"操作中...");
                EbingoRequestParmater parmater=new EbingoRequestParmater(v.getContext());
                parmater.put("company_id", Company.getInstance().getCompanyId());
                parmater.put("content", getContent());
                HttpUtil.post(HttpConstant.addAdvice,parmater,new EbingoHandler() {
                    @Override
                    public void onSuccess(int statusCode,  JSONObject response) {
                        ContextUtil.toast("提交成功！");
                    }

                    @Override
                    public void onFail(int statusCode, String msg) {
                        ContextUtil.toast("操作失败！");
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

    public String getContent() {
        return getText((EditText) findViewById(R.id.content));
    }
}
