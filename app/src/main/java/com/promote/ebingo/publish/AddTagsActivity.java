package com.promote.ebingo.publish;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.MultiAutoCompleteTextView;
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
import com.promote.ebingo.util.LogCat;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by acer on 2014/9/4.
 */
public class AddTagsActivity extends Activity implements View.OnClickListener,CompoundButton.OnCheckedChangeListener{
    MultiAutoCompleteTextView edit_add_tab;
    StringBuilder tagsStringBuilder =new StringBuilder();
    private GridView gridView;
    private List<HotTag> tagList =new ArrayList<HotTag>();
    private List<HotTag> userTagList=new ArrayList<HotTag>();
    private HotTagAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tags);
        init();
    }

    private void init() {
        gridView= (GridView) findViewById(R.id.tags_container);
        adapter=new HotTagAdapter();
        gridView.setAdapter(adapter);
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
                    tagList =JsonUtil.getArray(array, HotTag.class);
                    tagList.addAll(userTagList);
                    adapter.notifyDataSetChanged();
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
        switch (v.getId()){
            case R.id.common_back_btn:finish();break;
            case R.id.btn_add:
                String tag = edit_add_tab.getText().toString().trim();
                if (TextUtils.isEmpty(tag)) {
                    ContextUtil.toast("标签不能为空");
                    break;
                }
                if (userTagList.size()<5) {
                    HotTag hotTag=new HotTag();
                    hotTag.setName(tag);
                    tagList .removeAll(userTagList);
                    userTagList.add(hotTag);
                    tagList.addAll(userTagList);
                    adapter.notifyDataSetChanged();
                    edit_add_tab.setText(null);
                }else{
                    ContextUtil.toast("最多只能增加5个标签。");
                }
                break;
            case R.id.btn_done:

                StringBuilder userTags=new StringBuilder();
                for (HotTag hotTag:userTagList)userTags.append(hotTag.getName()+",");
                tagList.removeAll(userTagList);
                for (HotTag hotTag:tagList)if(hotTag.isSelect())userTags.append(hotTag.getName()+",");

                int lastSpiltIndex=userTags.lastIndexOf(",");
                String selectTags="";
                if (lastSpiltIndex>0){
                    selectTags =userTags.toString().substring(0,lastSpiltIndex);
                }
                Intent data=new Intent();
                data.putExtra("result", selectTags);
                setResult(RESULT_OK,data);
                finish();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        ((HotTag)buttonView.getTag()).setSelect(isChecked);
    }
    class HotTagAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return tagList.size();
        }

        @Override
        public Object getItem(int position) {
            return tagList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            HotTag tag=tagList.get(position);
            CheckBox tagBox;
            if (convertView==null){
                tagBox= (CheckBox) View.inflate(AddTagsActivity.this,R.layout.tag,null);
                tagBox.setOnCheckedChangeListener(AddTagsActivity.this);
                convertView=tagBox;
            }else{
                tagBox=(CheckBox)convertView;
            }
            tagBox.setTag(tag);
            if(userTagList.contains(tag)){
                convertView.setOnClickListener(clickDismiss);
                tagBox.setChecked(true);
                tagBox.setOnCheckedChangeListener(null);
            }
            tagBox.setText(tag.getName());
            return convertView;
        }
        View.OnClickListener clickDismiss=new View.OnClickListener(){
            @Override
            public void onClick(View v) {
               tagList.removeAll(userTagList);
               userTagList.remove(v.getTag());
               tagList.addAll(userTagList);
               adapter.notifyDataSetChanged();;
            }
        };
    }
}
