package com.promote.ebingo;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * 用于展示列表的Activity，有一个默认的布局文件R.layout.activity_base_list，子类不需要设定布局文件。标题是在manifest中对应Activity的label值
 * zhuchao on 2014/9/18.
 */
public class BaseListActivity extends ListActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_list);
        setTitle(getTitle());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.common_back_btn:
                finish();
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        ((TextView) findViewById(R.id.common_title_tv)).setText(title);
    }

    @Override
    public void setTitle(int titleId) {
        ((TextView) findViewById(R.id.common_title_tv)).setText(titleId);
    }
}
