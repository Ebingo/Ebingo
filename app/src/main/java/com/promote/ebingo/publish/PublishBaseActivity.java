package com.promote.ebingo.publish;

import android.app.Activity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.promote.ebingo.R;

/**
 * Created by acer on 2014/9/10.
 */
public class PublishBaseActivity extends Activity implements View.OnClickListener{

    protected void setBackTitle(String title){
        findViewById(R.id.common_back_btn).setOnClickListener(this);
        ((TextView)findViewById(R.id.common_title_tv)).setText(title);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.common_back_btn:
                finish();
                break;
        }
    }

    protected ImageView findImage(int id){
        return (ImageView) findViewById(id);
    }
    protected EditText findEdit(int id){
        return (EditText) findViewById(id);
    }
}
