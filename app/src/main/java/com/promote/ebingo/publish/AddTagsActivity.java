package com.promote.ebingo.publish;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RelativeLayout;

import com.jch.lib.util.DialogUtil;
import com.jch.lib.util.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.promote.ebingo.R;
import com.promote.ebingo.application.HttpConstant;
import com.promote.ebingo.bean.HotTag;
import com.promote.ebingo.impl.EbingoRequestParmater;
import com.promote.ebingo.util.ContextUtil;
import com.promote.ebingo.util.JsonUtil;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

/**
 * Created by acer on 2014/9/4.
 */
public class AddTagsActivity extends PublishBaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    MultiAutoCompleteTextView edit_add_tab;
    private LinkedList<HotTag> tagList = new LinkedList<HotTag>();
    private AutoLineLayout tagContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tags);
        init();
    }

    private void init() {
        findViewById(R.id.btn_done).setOnClickListener(this);
        tagContainer= (AutoLineLayout) findViewById(R.id.tags_container);
        setBackTitle("添加标签");
        edit_add_tab = (MultiAutoCompleteTextView) findViewById(R.id.edit_add_tags);

        new Handler().postDelayed(new Runnable() {//延迟10ms，等Activity加载完布局再获取热门标签
            @Override
            public void run() {
                getData(AddTagsActivity.this);
            }
        }, 10);
    }

    private void getData(Context context) {
        final Dialog dialog = DialogUtil.waitingDialog(context);
        EbingoRequestParmater parmater = new EbingoRequestParmater(context);
        HttpUtil.post(HttpConstant.getHotTags, parmater, new JsonHttpResponseHandler("utf-8") {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    JSONArray array = response.getJSONObject("response").getJSONArray("data");
                    JsonUtil.getArray(array, HotTag.class, tagList);
                    for (HotTag tag:tagList){
                        addTag(tag);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                ContextUtil.toast(responseString);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.common_back_btn:
                finish();
                break;
            case R.id.btn_add:
                String tag = edit_add_tab.getText().toString().trim();
                if (TextUtils.isEmpty(tag)) {
                    ContextUtil.toast("请输入标签");
                    break;
                }
                if (!isTagsMax()) {
                    HotTag hotTag = new HotTag();
                    hotTag.setName(tag);
                    hotTag.setSelect(true);
                    addTag(hotTag);
                    edit_add_tab.setText(null);
                } else {
                    toastTagIsMax();
                }
                break;
            case R.id.btn_done:

                StringBuilder selectTags = new StringBuilder();
                for (int i=0;i<tagList.size();i++){
                    HotTag temp=tagList.get(i);
                    if (temp.isSelect()) selectTags.append(temp.getName());
                    if(i<tagList.size()-1)selectTags.append(",");
                }

                Intent data = new Intent();
                data.putExtra("result", selectTags.toString());
                setResult(RESULT_OK, data);
                finish();
                break;
        }
    }

    private void addTag(HotTag tag){
        if (tag==null)return;
        tagList.add(tag);
        CheckBox checkBox= (CheckBox) View.inflate(this,R.layout.tag,null);
        checkBox.setTag(tag);
        checkBox.setText(tag.getName());
        checkBox.setChecked(tag.isSelect());
        checkBox.setSingleLine(false);
        tagContainer.addView(checkBox);
    }

    /**
     * 判断所选的标签是否超过最大值
     *
     * @return
     */
    private boolean isTagsMax() {
        int selectTagNum=0;
        for (HotTag tag:tagList){
            if (tag.isSelect()) {
                selectTagNum++;
            }
        }
        return selectTagNum >= 10;
    }
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        HotTag hotTag = (HotTag) buttonView.getTag();

        if (isChecked && isTagsMax()) {
            toastTagIsMax();
            buttonView.setChecked(false);
        } else {
            hotTag.setSelect(isChecked);
        }
    }

    /**
     * 提示标签数超出
     */
    private void toastTagIsMax() {
        ContextUtil.toast("最多只能添加10个标签");
    }

}
