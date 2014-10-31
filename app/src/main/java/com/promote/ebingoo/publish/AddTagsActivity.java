package com.promote.ebingoo.publish;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.MultiAutoCompleteTextView;
import android.widget.ScrollView;

import com.jch.lib.util.DialogUtil;
import com.jch.lib.util.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.promote.ebingoo.BaseActivity;
import com.promote.ebingoo.R;
import com.promote.ebingoo.application.HttpConstant;
import com.promote.ebingoo.bean.HotTag;
import com.promote.ebingoo.view.TagView;
import com.promote.ebingoo.impl.EbingoRequestParmater;
import com.promote.ebingoo.util.ContextUtil;
import com.promote.ebingoo.util.JsonUtil;
import com.promote.ebingoo.util.LogCat;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by acer on 2014/9/4.
 */
public class AddTagsActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, TagView.OnTagClickListener {
    public static final String CONTENT = "content";
    private String spilt = ",";
    MultiAutoCompleteTextView edit_add_tab;
    private int tag_select_color;
    private int tag_unSelect_color;
    private List<HotTag> tagList = new LinkedList<HotTag>();
    private AutoLineLayout tagContainer;
    private List<String> savedTags;
    private ScrollHandler scrollHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tags);
        tag_unSelect_color = getResources().getColor(R.color.tag_default_color);
        tag_select_color = getResources().getColor(R.color.tag_color);
        init();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void init() {
        findViewById(R.id.commit_title_done).setOnClickListener(this);
        tagContainer = (AutoLineLayout) findViewById(R.id.tags_container);
        edit_add_tab = (MultiAutoCompleteTextView) findViewById(R.id.edit_add_tags);
        edit_add_tab.setOnClickListener(this);
        String added = getIntent().getStringExtra(CONTENT);
        if (!TextUtils.isEmpty(added)) {
            savedTags = Arrays.asList(added.split(spilt));
            if (savedTags == null) {
                savedTags = new LinkedList<String>();
            }
        } else {
            savedTags = new LinkedList<String>();
        }
        scrollHandler=new ScrollHandler((ScrollView) findViewById(R.id.scroll));
        scrollHandler.post(new Runnable() {//延迟10ms，等Activity加载完布局再获取热门标签
            @Override
            public void run() {
                getData(AddTagsActivity.this);
            }
        });
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
                    tagList.clear();
                    JsonUtil.getArray(array, HotTag.class, tagList);
                    for (HotTag tag : tagList) {
                        addTagToView(tag);
                    }
                    for (String name : savedTags) {
                        HotTag tag = new HotTag();
                        tag.setSelect(true);
                        tag.setName(name);
                        addTagToView(tag);
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
                    tagList.add(hotTag);
                    addTagToView(hotTag);
                    edit_add_tab.setText(null);
                } else {
                    toastTagIsMax();
                }
                break;
            case R.id.edit_add_tags:
                LogCat.i("--->", "edit_add_tags");
                scrollHandler.scrollToEnd(100);
                break;
            case R.id.commit_title_done:

                StringBuilder selectTags = new StringBuilder();
                int count = tagContainer.getChildCount();
                for (int i = 0; i < count; i++) {
                    HotTag temp = (HotTag) tagContainer.getChildAt(i).getTag();
                    if (temp.isSelect()) {
                        selectTags.append(temp.getName() + ",");
                    }
                }
                if (selectTags.length() > 0) {
                    selectTags.deleteCharAt(selectTags.lastIndexOf(","));
                }
                Intent data = new Intent();
                data.putExtra("result", selectTags.toString());
                setResult(RESULT_OK, data);
                finish();
                break;
        }
    }

    public static class ScrollHandler extends Handler{
        private ScrollView scrollView;
        private Runnable scroll;
        public ScrollHandler(ScrollView scrollView) {
            this.scrollView = scrollView;
        }

        public void scrollToEnd(long delay) {
            if (scroll==null){
                scroll=new Runnable() {
                    @Override
                    public void run() {

                        scrollView.fullScroll(View.FOCUS_DOWN);
                    }
                };
            }
            postDelayed(scroll,delay);
        }

    }



    /**
     * 将标签加载到视图上面
     *
     * @param tag
     */
    private void addTagToView(HotTag tag) {
        if (tag == null) return;
        int size = tagContainer.getChildCount();
        for (int i = 0; i < size; i++) {//判断标签是否已经加载过了
            TagView tagView = (TagView) tagContainer.getChildAt(i);
            HotTag temp = (HotTag) tagView.getTag();
            if (temp.getName().equals(tag.getName())) {
                temp.setSelect(true);
                tagView.setDefaultColor(tag_select_color);
                return;
            }
        }

        TagView tagView = (TagView) View.inflate(this, R.layout.sample_tag_view, null);
        tagView.setTag(tag);
        tagView.setNumber(0);
        tagView.setText(tag.getName());
        tagView.setOnTagClickListener(this);
        if (tag.isSelect()) tagView.setDefaultColor(tag_select_color);
        else tagView.setDefaultColor(tag_unSelect_color);
        tagContainer.addView(tagView);
        scrollHandler.scrollToEnd(0);
    }


    /**
     * 判断所选的标签是否超过最大值
     *
     * @return
     */
    private boolean isTagsMax() {
        int selectTagNum = 0;
        int size = tagContainer.getChildCount();
        for (int i = 0; i < size; i++) {//判断标签是否已经加载过了
            TagView tagView = (TagView) tagContainer.getChildAt(i);
            HotTag temp = (HotTag) tagView.getTag();
            if (temp.isSelect()) selectTagNum++;
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

    @Override
    public void onDelete(TagView v) {

    }

    @Override
    public void onTagClick(TagView v) {
        HotTag hotTag = (HotTag) v.getTag();
        if (hotTag.isSelect()) {//如果hotTag是选中状态，则点击后颜色更改为未选中状态
            v.setDefaultColor(tag_unSelect_color);
            hotTag.setSelect(false);
        } else if (isTagsMax()) {//如果hotTag是未选中状态，则要先判断是否超出标签数
            toastTagIsMax();
        } else {//没有超出最大标签数
            hotTag.setSelect(true);
            v.setDefaultColor(tag_select_color);
        }
    }
}
