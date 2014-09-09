package com.promote.ebingo.InformationActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jch.lib.util.DialogUtil;
import com.jch.lib.util.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.promote.ebingo.R;
import com.promote.ebingo.application.HttpConstant;
import com.promote.ebingo.impl.EbingoRequestParmater;

import org.apache.http.Header;
import org.json.JSONObject;

public class BuyInfoActivity extends Activity implements View.OnClickListener{

    public static final String DEMAND_ID = "demand_id";
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

        int demandId = getIntent().getIntExtra(DEMAND_ID, -1);
        assert (demandId != -1);
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

    /**
     * 獲得詳情。
     */
    private void getInfoDetail(int demandId){

        String urlStr = HttpConstant.getInfoDetail;

        EbingoRequestParmater parmater = new EbingoRequestParmater(getApplicationContext());
        parmater.put("company_id", "0");    //TODO 測試數據
        parmater.put("info_id", demandId);

        final ProgressDialog dialog = DialogUtil.waitingDialog(BuyInfoActivity.this);
        HttpUtil.post(urlStr, parmater, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                dialog.dismiss();
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

                dialog.dismiss();
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                dialog.dismiss();
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });

    }
}
