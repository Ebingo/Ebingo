package com.promote.ebingo.InformationActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.promote.ebingo.R;

public class ProductInfoActivity extends Activity implements View.OnClickListener{
    public static final String ARG_ID = "id";
    private TextView prdinfointocompanytv;
    private TextView prdinfocompanytv;
    private RelativeLayout prdinfobtmll;
    private ImageView productinfoimg;
    private TextView productinfolooknumtv;
    private LinearLayout productinfonameleftll;
    private TextView productinfonametv;
    private TextView productinfopricetv;
    private TextView productinfostartnumtv;
    private TextView productinfocitytv;
    private TextView productinfodetailtv;
    private ImageView commonbackbtn;
    private TextView commontitletv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_info);
        initialize();
    }


    private void initialize() {
        commonbackbtn = (ImageView) findViewById(R.id.common_back_btn);
        commontitletv = (TextView) findViewById(R.id.common_title_tv);
        prdinfointocompanytv = (TextView) findViewById(R.id.prd_info_into_company_tv);
        prdinfocompanytv = (TextView) findViewById(R.id.prd_info_company_tv);
        prdinfobtmll = (RelativeLayout) findViewById(R.id.prd_info_btm_ll);
        productinfoimg = (ImageView) findViewById(R.id.product_info_img);
        productinfolooknumtv = (TextView) findViewById(R.id.product_info_look_num_tv);
        productinfonameleftll = (LinearLayout) findViewById(R.id.product_info_name_left_ll);
        productinfonametv = (TextView) findViewById(R.id.product_info_name_tv);
        productinfopricetv = (TextView) findViewById(R.id.product_info_price_tv);
        productinfostartnumtv = (TextView) findViewById(R.id.product_info_start_num_tv);
        productinfocitytv = (TextView) findViewById(R.id.product_info_city_tv);
        productinfodetailtv = (TextView) findViewById(R.id.product_info_detail_tv);

        int productId = getIntent().getIntExtra(ARG_ID, -1);
        assert (productId != -1);
        //網絡訪問

        commontitletv.setText(
                (R.string.produt_info));
        commonbackbtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id){

            case R.id.common_back_btn:{

                onBackPressed();
                finish();
                break;
            }

            default:{

            }

        }

    }
}
