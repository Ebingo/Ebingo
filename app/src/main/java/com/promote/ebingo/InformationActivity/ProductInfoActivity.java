package com.promote.ebingo.InformationActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jch.lib.util.DialogUtil;
import com.jch.lib.util.HttpUtil;
import com.jch.lib.util.ImageManager;
import com.jch.lib.util.TextUtil;
import com.promote.ebingo.R;
import com.promote.ebingo.application.HttpConstant;
import com.promote.ebingo.bean.CallRecord;
import com.promote.ebingo.bean.Company;
import com.promote.ebingo.bean.DetailInfoBean;
import com.promote.ebingo.center.CallRecordActivity;
import com.promote.ebingo.center.MyCollectionActivity;
import com.promote.ebingo.impl.EbingoHandler;
import com.promote.ebingo.impl.EbingoRequestParmater;
import com.promote.ebingo.impl.GetInfoDetail;
import com.promote.ebingo.util.ContextUtil;
import com.promote.ebingo.util.LogCat;

import org.json.JSONException;
import org.json.JSONObject;

public class ProductInfoActivity extends Activity implements View.OnClickListener {
    public static final String ARG_ID = "id";
    public static final String ARG_COLLECT_ID = "collectId";
    private ImageView commonbackbtn;
    private TextView commontitletv;
    private TextView prdinfointocompanytv;
    private TextView prdinfocompanytv;
    private RelativeLayout prdinfobtmll;
    private ImageView productinfoimg;
    private WebView productinfoiweb;
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
    private int collectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_info);
        initialize();
    }

    private void initialize() {
        collectId = getIntent().getIntExtra(ARG_COLLECT_ID, -1);
        commonbackbtn = (ImageView) findViewById(R.id.common_back_btn);
        commontitletv = (TextView) findViewById(R.id.common_title_tv);

        prdinfointocompanytv = (TextView) findViewById(R.id.prd_info_into_company_tv);//进入公司
        prdinfocompanytv = (TextView) findViewById(R.id.prd_info_company_tv);//公司名
        prdinfobtmll = (RelativeLayout) findViewById(R.id.prd_info_btm_ll);
        productinfoimg = (ImageView) findViewById(R.id.product_info_img);
        productinfoiweb = (WebView) findViewById(R.id.product_info_web);
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
        commontitletv.setText(R.string.title_product_info);
        commonbackbtn.setOnClickListener(this);
        productinfotelcb.setOnClickListener(this);
        prdinfointocompanytv.setOnClickListener(this);
        productinfocollectcb.setOnClickListener(this);

    }

    private void setData(DetailInfoBean infoBean) {
        LogCat.i("--->"," infoBean.getCompany_id="+infoBean.getCompany_id());
        LogCat.i("--->"," Company.getInstance().getCompanyId()="+Company.getInstance().getCompanyId());
        LogCat.i("--->"," productinforlll="+productinforlll);
        if (infoBean.getCompany_id().equals(Company.getInstance().getCompanyId()))productinforlll.setVisibility(View.GONE);
        else productinforlll.setVisibility(View.VISIBLE);
        prdinfocompanytv.setText(infoBean.getCompany_name());
        pi_title_tv.setText(infoBean.getTitle());
        pi_price_tv.setText(TextUtils.isEmpty(infoBean.getPrice()) ? "0" : infoBean.getPrice() + "");
        productinfolooknumtv.setText(infoBean.getRead_num() + "");
        if (infoBean.getType() == 1) {
            pi_min_sell_num.setText(infoBean.getMin_sell_num() + ""+infoBean.getUnit());
        } else {
            pi_min_sell_num.setText(infoBean.getMin_sell_num() + "");
        }
        productinfocollectcb.setChecked(infoBean.getInwishlist() == 1);
        productinfocitytv.setText(infoBean.getRegion());

        if (!TextUtils.isEmpty(infoBean.getUrl_3d())) {
            productinfoiweb.setVisibility(View.VISIBLE);
            productinfoimg.setVisibility(View.GONE);
            productinfoiweb.getSettings().setJavaScriptEnabled(true);
            productinfoiweb.loadUrl(infoBean.getUrl_3d());
        } else {
            productinfoimg.setVisibility(View.VISIBLE);
            productinfoiweb.setVisibility(View.GONE);
            ImageManager.load(infoBean.getImage(), productinfoimg);
        }
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
            case R.id.prd_info_into_company_tv: {

                Intent intent = new Intent(this, InterpriseInfoActivity.class);
                intent.putExtra(InterpriseInfoActivity.ARG_ID, mDetailInfoBean.getCompany_id());
                intent.putExtra(InterpriseInfoActivity.ARG_NAME, mDetailInfoBean.getCompany_name());
                startActivity(intent);
                break;
            }
            case R.id.product_info_tel_cb:
                CallRecord record = new CallRecord();
                record.setCall_id(Company.getInstance().getCompanyId());
                record.setInfoId(mDetailInfoBean.getInfo_id());
                record.setTo_id(mDetailInfoBean.getCompany_id());
                record.setPhone_num(mDetailInfoBean.getPhone_num());
                CallRecordActivity.CallRecordManager.dialNumber(this, record);
                break;
            case R.id.product_info_collect_cb:
                if (productinfocollectcb.isChecked()) {
                    addCollection(mDetailInfoBean.getInfo_id());
                } else {
                    cancelCollection(collectId);
                }
                break;
            default: {
            }
        }
    }

    /**
     * 添加收藏
     *
     * @param id
     */
    private void addCollection(int id) {
        EbingoRequestParmater parmater = new EbingoRequestParmater(getApplicationContext());
        parmater.put("company_id", Company.getInstance().getCompanyId());
        parmater.put("info_id", id);
        HttpUtil.post(HttpConstant.addToWishlist, parmater, new EbingoHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                ContextUtil.toast("添加收藏成功！");
                try {
                    collectId = response.getJSONObject("data").getInt("wishlistid");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(int statusCode, String msg) {
                ContextUtil.toast("添加收藏失败！" + msg);
                productinfocollectcb.setChecked(false);
            }

            @Override
            public void onFinish() {

            }
        });
    }

    /**
     * 取消收藏
     *
     * @param id
     */
    private void cancelCollection(int id) {
        EbingoRequestParmater parmater = new EbingoRequestParmater(getApplicationContext());
        parmater.put("company_id", Company.getInstance().getCompanyId());
        parmater.put("wishlistid", id);
        HttpUtil.post(HttpConstant.cancleWishlist, parmater, new EbingoHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                ContextUtil.toast("取消收藏成功！");
            }

            @Override
            public void onFail(int statusCode, String msg) {
                ContextUtil.toast("取消收藏失败！" + msg);
                productinfocollectcb.setChecked(true);
            }

            @Override
            public void onFinish() {

            }
        });
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
