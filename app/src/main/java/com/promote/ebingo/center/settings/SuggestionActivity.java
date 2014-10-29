package com.promote.ebingo.center.settings;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

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
import com.promote.ebingo.publish.EbingoDialog;
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
        ((TextView)findViewById(R.id.commit_title_done)).setText("提 交");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commit_title_done:
                String content=getText((EditText) findViewById(R.id.edit_content));
                if (TextUtils.isEmpty(content)){
                    ContextUtil.toast("您还没有输入任何内容。");
                    return;
                }
                final Dialog dialog= DialogUtil.waitingDialog(this,"操作中...");

                EbingoRequestParmater parmater=new EbingoRequestParmater(v.getContext());
                parmater.put("company_id", Company.getInstance().getCompanyId());
                parmater.put("content", content);
                HttpUtil.post(HttpConstant.addAdvice,parmater,new EbingoHandler() {
                    @Override
                    public void onSuccess(int statusCode,  JSONObject response) {
                        EbingoDialog notice=new EbingoDialog(SuggestionActivity.this);
                        notice.setTitle("提交成功！");
                        notice.setMessage("感谢您的建议！");
                        notice.setPositiveButton(R.string.yes,notice.DEFAULT_LISTENER);
                        notice.show();
                    }

                    @Override
                    public void onFail(int statusCode, String msg) {

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
