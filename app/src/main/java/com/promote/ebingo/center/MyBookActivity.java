package com.promote.ebingo.center;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
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

import static com.promote.ebingo.publish.AddTagsActivity.ScrollHandler;
/**
 * 订阅标签、热门标签等
 */
public class MyBookActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener, TagView.OnTagClickListener {

    private LinearLayout tagContent;
    private MultiAutoCompleteTextView edit_tag;
    private ToggleButton toggleButton;
    private int tag_remain = 0;//剩余标签数
    private ScrollHandler scrollHandler;
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
        tag_remain = Company.getInstance().getVipInfo().getTag_num();
        toggleButton = (ToggleButton) findViewById(R.id.arrange);
        toggleButton.setOnCheckedChangeListener(this);
        scrollHandler=new ScrollHandler((ScrollView) findViewById(R.id.scroll));
        scrollHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getData();
            }
        },100);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.commit_title_done:
                saveData();
                break;
            case R.id.btn_add: {
                int tagNum = tagContent.getChildCount();
                LogCat.i("--->", "addTag(" + tag_remain + ")");
                if (tag_remain <= 0) {
                    EbingoDialog dialog = EbingoDialog.newInstance(this, EbingoDialog.DialogStyle.STYLE_TO_PRIVILEGE);
//                    dialog.setNeutralButton(R.string.purchase,new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            Intent intent=new Intent().setClass(MyBookActivity.this,BuyTagActivity.class);
//                            startActivity(intent);
//                        }
//                    });
                    dialog.setMessage(getString(R.string.vip_add_tag, VipType.getCompanyInstance().name, tagNum));
                    dialog.show();
                    return;
                }

                String tagName = edit_tag.getText().toString().trim();

                if (isUniqueTag(tagName)) {
                    BookBean bookBean = new BookBean();
                    bookBean.setName(tagName);
                    bookBean.setNo_read(0);

                    addTag(bookBean);
                    tag_remain--;
                    edit_tag.setText(null);
                } else {
                    ContextUtil.toast(String.format("已加过\"%s\"，请勿重复添加！", tagName));
                }

            }
            break;
            case R.id.edit_add_tags:
                scrollHandler.scrollToEnd(100);
                break;
            default:
                super.onClick(v);

        }
    }

    private boolean isUniqueTag(String tagName) {
        int count = tagContent.getChildCount();
        for (int i = 0; i < count; i++) {
            TagView tagView = (TagView) tagContent.getChildAt(i);
            BookBean book = (BookBean) tagView.getTag();
            if (tagName.equals(book.getName())) return false;
        }
        return true;
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
                    LogCat.w(e.getLocalizedMessage());
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
                Company.getInstance().getVipInfo().setTag_num(tag_remain);
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
        if (toggleButton.isChecked()) {
            toggleTagViewState(true);
        }
        scrollHandler.scrollToEnd(0);
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
            if (state) {
                Animation anim;
                if (tagView.getAnimation() == null) {
                    RotateAnimation rotateAnimation = new RotateAnimation(-3, 3, tagView.getWidth() / 2, tagView.getHeight() / 2);
                    rotateAnimation.setDuration(100);
                    rotateAnimation.setInterpolator(new DecelerateInterpolator());
                    rotateAnimation.setRepeatCount(Animation.INFINITE);
                    rotateAnimation.setRepeatMode(Animation.REVERSE);
                    anim = rotateAnimation;
                } else {
                    anim = tagView.getAnimation();
                }
                tagView.startAnimation(anim);

            } else {
                tagView.clearAnimation();
            }
            tagView.setInDeleteState(state);
        }
    }


    @Override
    public void onDelete(TagView v) {
        v.clearAnimation();
        tagContent.removeView(v);
        tag_remain++;
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
