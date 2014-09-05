package com.promote.ebingo.publish;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

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

import java.util.ArrayList;

/**
 * Created by acer on 2014/9/4.
 */
public class AddTagsActivity extends Activity implements View.OnClickListener,CompoundButton.OnCheckedChangeListener{
    MultiAutoCompleteTextView edit_add_tab;
    StringBuilder hotTags =new StringBuilder();
    private int addedTag=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tags);
        init();
    }

    private void init() {
        findViewById(R.id.common_back_btn).setOnClickListener(this);
        findViewById(R.id.btn_done).setOnClickListener(this);
        ((TextView)findViewById(R.id.common_title_tv)).setText("添加标签");
        edit_add_tab= (MultiAutoCompleteTextView) findViewById(R.id.edit_add_tags);

        new Handler().postDelayed(new Runnable() {//延迟100ms，等Activity加载完布局再获取热门标签
            @Override
            public void run() {
                getData(AddTagsActivity.this);
            }
        },100);
    }

    private void getData(Context context){
        final Dialog dialog= DialogUtil.waitingDialog(context);
        EbingoRequestParmater parmater=new EbingoRequestParmater(context);
        HttpUtil.post(HttpConstant.getHotTags,parmater,new JsonHttpResponseHandler("utf-8"){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    JSONArray array=response.getJSONObject("response").getJSONArray("data");
                    ArrayList<HotTag> hotTags=JsonUtil.getArray(array, HotTag.class);
                    addHotTags(hotTags);
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

    /**
     * 添加热门标签
     * @param tags
     */
    private void addHotTags(ArrayList<HotTag> tags){

        TableLayout  tags_container= (TableLayout) findViewById(R.id.tags_container);
        CheckBox tagBox;
        TableRow tagRow=null;
        for (int i=0;i<tags.size();i++){
            HotTag tag=tags.get(i);
           final int column=i%5;
            if(column==0){
                tagRow= inflateNewTable();
                tags_container.addView(tagRow);
            }
           tagBox= (CheckBox) tagRow.getChildAt(column);
           tagBox.setVisibility(View.VISIBLE);
           tagBox.setTag(tag);
           tagBox.setText(tag.getName());
        }
    }

    /**
     * 用户添加的标签
     */
    private void addUserTags(String tag){
        addedTag++;
        TableLayout  tags_container= (TableLayout) findViewById(R.id.tags_container);
        TableRow lastRow= (TableRow) tags_container.getChildAt(tags_container.getChildCount()-1);
        int visibleChildrenCount=0;
        int firstInVisibleChild=-1;//第一个不可见的View
        int ChildCount=lastRow.getChildCount();
        for (int i=0;i<ChildCount;i++){//获取lastRow可见子View数目
            if(lastRow.getChildAt(i).getVisibility()==View.VISIBLE){
                visibleChildrenCount++;
            }else if(firstInVisibleChild==-1){
                firstInVisibleChild=i;
            }
        }
        if (visibleChildrenCount>=ChildCount){
            lastRow= inflateNewTable();
            tags_container.addView(lastRow);
            firstInVisibleChild=0;
        }
        CheckBox tagBox= (CheckBox) lastRow.getChildAt(firstInVisibleChild);
        tagBox.setText(tag);
        tagBox.setChecked(true);
        tagBox.setVisibility(View.VISIBLE);
        tagBox.setOnCheckedChangeListener(null);
        tagBox.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                v.setVisibility(View.INVISIBLE);
                addedTag--;
            }
        });
    }

    /**
     * 从XML中加载一个TableRow
     * @return
     */
    private TableRow inflateNewTable(){
        TableRow tagRow= (TableRow) getLayoutInflater().inflate(R.layout.tag_row,null);
        for (int i=0;i<tagRow.getChildCount();i++){
            CheckBox child=(CheckBox) tagRow.getChildAt(i);
            child.setVisibility(View.INVISIBLE);
            child.setOnCheckedChangeListener(this);
        }
        return tagRow;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.common_back_btn:finish();break;
            case R.id.btn_add:
                if (addedTag<5) {
                    String tag = edit_add_tab.getText().toString().trim();
                    addUserTags(tag);
                    edit_add_tab.setText(null);
                }else{
                    ContextUtil.toast("最多只能增加5个标签。");
                }
                break;
            case R.id.btn_done:
                Intent data=new Intent();
                data.putExtra("result", hotTags.toString()+edit_add_tab.getText().toString());
                setResult(RESULT_OK,data);
                finish();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        String selectedTag=buttonView.getText().toString();
        if (isChecked){
            hotTags.append(selectedTag + ",");
        }else{
            hotTags.replace(hotTags.indexOf(selectedTag),selectedTag.length(),"");
        }
    }
}
