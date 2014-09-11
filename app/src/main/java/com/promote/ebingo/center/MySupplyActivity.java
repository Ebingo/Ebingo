package com.promote.ebingo.center;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.promote.ebingo.R;

public class MySupplyActivity extends ActionBarActivity implements View.OnClickListener {

    private ListView mysupplylv;
    private ImageView commonbackbtn;
    private TextView commontitletv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_supply);
        initialize();
    }


    private void initialize() {
        commonbackbtn = (ImageView) findViewById(R.id.common_back_btn);
        commontitletv = (TextView) findViewById(R.id.common_title_tv);
        mysupplylv = (ListView) findViewById(R.id.mysupply_lv);

        commontitletv.setText(getResources().getString(R.string.my_supply));
        commonbackbtn.setOnClickListener(this);
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

            case R.id.common_title_tv: {
                break;
            }
            default: {

            }


        }

    }
}
