package com.promote.ebingo.InformationActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jch.lib.util.DialogUtil;
import com.jch.lib.util.TextUtil;
import com.promote.ebingo.R;
import com.promote.ebingo.application.HttpConstant;
import com.promote.ebingo.bean.Company;
import com.promote.ebingo.bean.DetailInfoBean;
import com.promote.ebingo.center.MyCollectionActivity;
import com.promote.ebingo.impl.EbingoRequestParmater;
import com.promote.ebingo.impl.GetInfoDetail;
import com.promote.ebingo.util.ContextUtil;

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
    private TextView pi_title_tv;
    private TextView pi_price_tv;
    private TextView pi_min_sell_num;
    private LinearLayout productinfopricell;
    private TextView productinfolooknumtv;
    private LinearLayout productinfonameleftll;
    private TextView productinfocitytv;
    private WebView productinfoDetailwv;

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

        prdinfointocompanytv = (TextView) findViewById(R.id.prd_info_into_company_tv);//进入公司
        prdinfocompanytv = (TextView) findViewById(R.id.prd_info_company_tv);//公司名
        prdinfobtmll = (RelativeLayout) findViewById(R.id.prd_info_btm_ll);
        productinfoimg = (ImageView) findViewById(R.id.product_info_img);
        productinfotelcb = (CheckBox) findViewById(R.id.product_info_tel_cb);//电话咨询
        productinfocollectcb = (CheckBox) findViewById(R.id.product_info_collect_cb);//收藏
        productinforlll = (RelativeLayout) findViewById(R.id.product_info_rl_ll);
        pi_title_tv = (TextView) findViewById(R.id.product_info_name_tv);//title
        pi_price_tv = (TextView) findViewById(R.id.product_info_price_tv);
        pi_min_sell_num = (TextView) findViewById(R.id.product_info_start_num_tv);
        productinfopricell = (LinearLayout) findViewById(R.id.product_info_price_ll);
        productinfolooknumtv = (TextView) findViewById(R.id.product_info_look_num_tv);
        productinfonameleftll = (LinearLayout) findViewById(R.id.product_info_name_left_ll);
        productinfocitytv = (TextView) findViewById(R.id.product_info_city_tv);
        productinfoDetailwv = (WebView) findViewById(R.id.product_info_detail_wv);
        int productId = getIntent().getIntExtra(ARG_ID, -1);
        assert (productId != -1);
        //網絡訪問
        getInfoDetail(productId);
        commontitletv.setText(
                (R.string.title_product_info));
        commonbackbtn.setOnClickListener(this);
        productinfotelcb.setOnClickListener(this);
        productinfocollectcb.setOnClickListener(this);
        prdinfointocompanytv.setOnClickListener(this);
    }


    private void setData(DetailInfoBean infoBean) {
        prdinfocompanytv.setText(infoBean.getCompany_name());
        pi_title_tv.setText(infoBean.getTitle());
        pi_price_tv.setText(TextUtils.isEmpty(infoBean.getPrice()) ? "0" : infoBean.getPrice() + "");
        productinfolooknumtv.setText(infoBean.getRead_num() + "");
        if (infoBean.getType() == 1) {
            pi_min_sell_num.setText(infoBean.getMin_sell_num() + "");
        } else {
            pi_min_sell_num.setText(infoBean.getMin_sell_num() + "");
        }
        productinfocitytv.setText(infoBean.getRegion());
        productinfoDetailwv.getSettings().setJavaScriptEnabled(true);
        productinfoDetailwv.loadDataWithBaseURL(HttpConstant.getRootUrl(), infoBean.getDescription(), "text/html", "UTF-8", "about:blank");
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
            case R.id.prd_info_into_company_tv:
//                productinfoDetailwv.loadUrl(mDetailInfoBean);
                break;
            case R.id.product_info_tel_cb:
                ContextUtil.dialNumber(this,mDetailInfoBean.getPhone_num());
                break;
            case R.id.product_info_collect_cb:
                Intent intent=new Intent(this, MyCollectionActivity.class);
                startActivity(intent);
                break;
            default: {
            }
        }
    }



    /**
     * 獲得詳情。
     */
    private void getInfoDetail(int demandId) {

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
                setData(detailInfoBean);
                dialog.dismiss();
            }
        });

    }


}
