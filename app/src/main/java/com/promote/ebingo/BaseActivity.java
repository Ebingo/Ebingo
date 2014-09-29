package com.promote.ebingo;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by jch. on 2014/8/27.
 */
public class BaseActivity extends FragmentActivity implements View.OnClickListener {

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        try {
            ((TextView) findViewById(R.id.common_title_tv)).setText(getTitle());
        } catch (NullPointerException e) {
            throw new IllegalStateException("BaseActivity must include a TextView with id:common_title_tv");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.common_back_btn:
                finish();
                break;
        }
    }

    protected void toActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }

    protected String getText(TextView textView) {
        if (textView == null) return null;
        return textView.getText().toString().trim();
    }

    @Override
    public void setTitle(CharSequence title) {
        ((TextView) findViewById(R.id.common_title_tv)).setText(title);
    }

    @Override
    public void setTitle(int titleId) {
        ((TextView) findViewById(R.id.common_title_tv)).setText(titleId);
    }
    protected ImageView findImage(int id){
        return (ImageView) findViewById(id);
    }
    protected EditText findEdit(int id){
        return (EditText) findViewById(id);
    }
}
