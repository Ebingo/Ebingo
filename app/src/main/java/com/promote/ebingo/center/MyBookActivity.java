package com.promote.ebingo.center;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.jch.lib.util.HttpUtil;
import com.promote.ebingo.BaseActivity;
import com.promote.ebingo.R;
import com.promote.ebingo.application.HttpConstant;
import com.promote.ebingo.bean.BookBean;
import com.promote.ebingo.bean.Company;
import com.promote.ebingo.impl.EbingoHandler;
import com.promote.ebingo.impl.EbingoRequestParmater;
import com.promote.ebingo.publish.EbingoDialog;
import com.promote.ebingo.publish.VipType;
import com.promote.ebingo.util.ContextUtil;
import com.promote.ebingo.util.JsonUtil;
import com.promote.ebingo.util.LogCat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 订阅标签、热门标签等
 */
public class MyBookActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener, TagView.OnTagClickListener {

    private LinearLayout tagContent;
    private MultiAutoCompleteTextView edit_tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_book);
        tagContent = (LinearLayout) findViewById(R.id.tag_content);
        TextView save = (TextView) findViewById(R.id.commit_title_done);
        edit_tag = (MultiAutoCompleteTextView) findViewById(R.id.edit_add_tags);
        save.setText("保存");
        save.setOnClickListener(this);
        edit_tag.setOnClickListener(this);
        getData();
        ((ToggleButton) findViewById(R.id.arrange)).setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.commit_title_done:
                saveData();
                break;
            case R.id.btn_add:
                BookBean bookBean = new BookBean();
                bookBean.setName(edit_tag.getText().toString().trim());
                bookBean.setNo_read(0);
                addTag(bookBean);
                edit_tag.setText(null);
                break;
            case R.id.edit_add_tags:
                ScrollView scrollView = (ScrollView) findViewById(R.id.scroll);
                scrollView.fullScroll(View.FOCUS_DOWN);
                break;
            default:
                super.onClick(v);

        }
    }

    /**
     * 获取订阅标签
     */
    private void getData() {
        EbingoRequestParmater parmater = new EbingoRequestParmater(this);
        parmater.put("company_id", Company.getInstance().getCompanyId());
        HttpUtil.post(HttpConstant.getMyTagList, parmater, new EbingoHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                ArrayList<BookBean> books = new ArrayList<BookBean>();
                try {
                    JsonUtil.getArray(response.getJSONArray("data"), BookBean.class, books);
                    for (int i = 0; i < books.size(); i++) {
                        addTag(books.get(i));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(int statusCode, String msg) {
                LogCat.i("--->", msg + "::fail");
            }

            @Override
            public void onFinish() {

            }
        });
    }

    /**
     * 保存标签
     */
    private void saveData() {
        EbingoRequestParmater parmater = new EbingoRequestParmater(this);
        parmater.put("company_id", Company.getInstance().getCompanyId());
        parmater.put("tags", getTags());
        HttpUtil.post(HttpConstant.saveTagList, parmater, new EbingoHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                ContextUtil.toast("保存成功！");
            }

            @Override
            public void onFail(int statusCode, String msg) {
            }

            @Override
            public void onFinish() {

            }
        });
    }

    /**
     * 将所有标签拼接起来，用逗号分开
     *
     * @return
     */
    private String getTags() {
        StringBuilder sb = new StringBuilder();
        int tagNumber = tagContent.getChildCount();
        for (int i = 0; i < tagNumber - 1; i++) {
            TagView tagView = (TagView) tagContent.getChildAt(i);
            sb.append(tagView.getText() + ",");
        }
        if (tagNumber >= 1) sb.append(((TagView) tagContent.getChildAt(tagNumber - 1)).getText());
        return sb.toString();
    }

    /**
     * 添加一个标签
     *
     * @param bookBean
     */
    private void addTag(BookBean bookBean) {
        int tagNum = tagContent.getChildCount();
        VipType.VipInfo info = VipType.getCompanyInstance().getVipInfo();
        int maxTagNum = info.book_tag_num;
        if (tagNum >= maxTagNum) {
            EbingoDialog.newInstance(this, EbingoDialog.DialogStyle.STYLE_CANNOT_ADD_TAG).show();
            return;
        }
        String name = bookBean.getName();
        if (TextUtils.isEmpty(name)) {
            ContextUtil.toast("请输入标签！");
            return;
        }
        TagView tagView = (TagView) View.inflate(this, R.layout.sample_tag_view, null);
        tagContent.addView(tagView);
        tagView.setText(bookBean.getName());
        tagView.setNumber(bookBean.getNo_read());
        tagView.setTag(bookBean);
        tagView.setOnTagClickListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        toggleTagViewState(isChecked);

    }

    /**
     * 切换标签状态
     *
     * @param state 是否为删除状态
     */
    private void toggleTagViewState(boolean state) {
        for (int i = 0; i < tagContent.getChildCount(); i++) {
            TagView tagView = (TagView) tagContent.getChildAt(i);
            tagView.setInDeleteState(state);
        }
    }

    @Override
    public void onDelete(TagView v) {
        tagContent.removeView(v);
    }

    @Override
    public void onTagClick(TagView v) {
//        if (v.getNumber()<=0)return;
        Intent intent = new Intent(this, TagInfoListActivity.class);
        BookBean bookBean = (BookBean) v.getTag();
        intent.putExtra(TagInfoListActivity.ID, bookBean.getId());
        intent.putExtra(TagInfoListActivity.NAME, bookBean.getName());
        startActivity(intent);
    }
}
