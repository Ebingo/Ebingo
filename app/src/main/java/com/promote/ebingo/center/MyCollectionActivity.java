package com.promote.ebingo.center;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.promote.ebingo.R;

public class MyCollectionActivity extends ActionBarActivity implements View.OnClickListener {

    private ListView mycollv;
    private ImageView commonbackbtn;
    private TextView commontitletv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cellection);
        initialize();
    }


    private void initialize() {

        commonbackbtn = (ImageView) findViewById(R.id.common_back_btn);
        commontitletv = (TextView) findViewById(R.id.common_title_tv);
        mycollv = (ListView) findViewById(R.id.mycol_lv);

        commontitletv.setText(getString(R.string.my_collect));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.common_back_btn: {

                onBackPressed();
                finish();
                break;
            }

        }

    }
}
