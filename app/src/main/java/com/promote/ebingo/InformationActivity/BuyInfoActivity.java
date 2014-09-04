package com.promote.ebingo.InformationActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.promote.ebingo.R;

public class BuyInfoActivity extends Activity implements View.OnClickListener{

    private TextView buyinfointocompanytv;
    private TextView buyinfocompanytv;
    private RelativeLayout buyinfobtmll;
    private TextView buyinfonametv;
    private TextView buynumtv;
    private TextView buyinfopublishtimetv;
    private TextView publishlooknumtv;
    private TextView buyinfodetailtv;
    private Button buyInfocontactphonetv;
    private ImageView commonbackbtn;
    private TextView commontitletv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_info);
        initialize();
    }


    private void initialize() {

        commonbackbtn = (ImageView) findViewById(R.id.common_back_btn);
        commontitletv = (TextView) findViewById(R.id.common_title_tv);
        buyinfointocompanytv = (TextView) findViewById(R.id.buy_info_into_company_tv);
        buyinfocompanytv = (TextView) findViewById(R.id.buy_info_company_tv);
        buyinfobtmll = (RelativeLayout) findViewById(R.id.buy_info_btm_ll);
        buyinfonametv = (TextView) findViewById(R.id.buy_info_name_tv);
        buynumtv = (TextView) findViewById(R.id.buy_num_tv);
        buyinfopublishtimetv = (TextView) findViewById(R.id.buy_info_publish_time_tv);
        publishlooknumtv = (TextView) findViewById(R.id.publish_look_num_tv);
        buyinfodetailtv = (TextView) findViewById(R.id.buy_info_detail_tv);
        buyInfocontactphonetv = (Button) findViewById(R.id.buy_Info_contact_phone_tv);

        commonbackbtn.setOnClickListener(this);
        commontitletv.setText(getString(R.string.title_buy_detail));
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id){
            case R.id.common_back_btn:{

                onBackPressed();
                this.finish();
                break;
            }
            default:{

            }

        }

    }
}
