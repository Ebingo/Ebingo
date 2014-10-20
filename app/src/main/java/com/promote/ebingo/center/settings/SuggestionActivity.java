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
import com.promote.ebingo.publish.AddTagsActivity;
import com.promote.ebingo.util.ContextUtil;
import com.promote.ebingo.util.LogCat;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by acer on 2014/9/16.
 */
public class SuggestionActivity extends BaseActivity {
    private AddTagsActivity.ScrollHandler scrollHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion);
        scrollHandler=new AddTagsActivity.ScrollHandler((android.widget.ScrollView) findViewById(R.id.scroll));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commit:
                final Dialog dialog= DialogUtil.waitingDialog(this,"操作中...");
                EditText content=(EditText) findViewById(R.id.edit_content);
                EbingoRequestParmater parmater=new EbingoRequestParmater(v.getContext());
                parmater.put("company_id", Company.getInstance().getCompanyId());
                parmater.put("content", getText(content));
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
            case R.id.edit_content:
//                scrollHandler.scrollToEnd(100);
                break;
            default:
                super.onClick(v);
        }
    }

}
