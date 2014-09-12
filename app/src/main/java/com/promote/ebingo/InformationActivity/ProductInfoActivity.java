package com.promote.ebingo.InformationActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jch.lib.util.DialogUtil;
import com.promote.ebingo.R;
import com.promote.ebingo.application.HttpConstant;
import com.promote.ebingo.bean.Company;
import com.promote.ebingo.bean.DetailInfoBean;
import com.promote.ebingo.impl.EbingoRequestParmater;
import com.promote.ebingo.impl.GetInfoDetail;

public class ProductInfoActivity extends Activity implements View.OnClickListener {
    public static final String ARG_ID = "id";
    private ImageView commonbackbtn;
    private TextView commontitletv;
    private TextView prdinfointocompanytv;
    private TextView prdinfocompanytv;
    private RelativeLayout prdinfobtmll;
    private ImageView productinfoimg;
    private CheckBox productinfotelcb;
    private CheckBox productinfocollectcb;
    private RelativeLayout productinforlll;
    private TextView productinfonametv;
    private TextView productinfopricetv;
    private TextView productinfostartnumtv;
    private LinearLayout productinfopricell;
    private TextView productinfolooknumtv;
    private LinearLayout productinfonameleftll;
    private TextView productinfocitytv;

    private DetailInfoBean mDetailInfoBean = null;

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
        productinfotelcb = (CheckBox) findViewById(R.id.product_info_tel_cb);
        productinfocollectcb = (CheckBox) findViewById(R.id.product_info_collect_cb);
        productinforlll = (RelativeLayout) findViewById(R.id.product_info_rl_ll);
        productinfonametv = (TextView) findViewById(R.id.product_info_name_tv);
        productinfopricetv = (TextView) findViewById(R.id.product_info_price_tv);
        productinfostartnumtv = (TextView) findViewById(R.id.product_info_start_num_tv);
        productinfopricell = (LinearLayout) findViewById(R.id.product_info_price_ll);
        productinfolooknumtv = (TextView) findViewById(R.id.product_info_look_num_tv);
        productinfonameleftll = (LinearLayout) findViewById(R.id.product_info_name_left_ll);
        productinfocitytv = (TextView) findViewById(R.id.product_info_city_tv);
        int productId = getIntent().getIntExtra(ARG_ID, -1);
        assert (productId != -1);
        //網絡訪問

        commontitletv.setText(
                (R.string.title_product_info));
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

            default: {

            }

        }

    }

    /**
     * 獲得詳情。
     */
    private void getInfoDetail(int demandId) {

        String urlStr = HttpConstant.getInfoDetail;

        EbingoRequestParmater parmater = new EbingoRequestParmater(getApplicationContext());
        Company company = Company.getInstance();
        if (company.getCompanyId() != null) {
            parmater.put("company_id", company.getCompanyId());
        } else {
            parmater.put("company_id", "0");    //游客
        }
        parmater.put("info_id", demandId);

        final ProgressDialog dialog = DialogUtil.waitingDialog(ProductInfoActivity.this);
        GetInfoDetail.getInfoDetail(parmater, new GetInfoDetail.CallBack() {
            @Override
            public void onFailed(String msg) {
                //nodata

                dialog.dismiss();
            }

            @Override
            public void onSuccess(DetailInfoBean detailInfoBean) {
                mDetailInfoBean = detailInfoBean;
                dialog.dismiss();
            }
        });

    }


}
