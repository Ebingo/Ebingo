package com.promote.ebingo.center;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.promote.ebingo.R;

/**
 * Created by acer on 2014/10/21.
 */
public class BuyTagActivity extends MyBookActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView save = (TextView) findViewById(R.id.commit_title_done);
        TextView buyTag = (TextView) findViewById(R.id.tv_tag_add);
        save.setText("购买");
        buyTag.setTag(getTitle());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add:
                break;
            case R.id.commit_title_done:
                break;
            default:super.onClick(v);
        }

    }
}
