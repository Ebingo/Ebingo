package com.promote.ebingo.InformationActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jch.lib.util.DialogUtil;
import com.jch.lib.util.HttpUtil;
import com.jch.lib.util.ImageManager;
import com.promote.ebingo.R;
import com.promote.ebingo.application.Constant;
import com.promote.ebingo.application.HttpConstant;
import com.promote.ebingo.bean.CallRecord;
import com.promote.ebingo.bean.Company;
import com.promote.ebingo.bean.DetailInfoBean;
import com.promote.ebingo.center.CallRecordActivity;
import com.promote.ebingo.impl.EbingoHandler;
import com.promote.ebingo.impl.EbingoRequestParmater;
import com.promote.ebingo.impl.GetInfoDetail;
import com.promote.ebingo.publish.EbingoDialog;
import com.promote.ebingo.publish.PublishEditActivity;
import com.promote.ebingo.publish.VipType;
import com.promote.ebingo.publish.login.LoginDialog;
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
    private TextView productinfoDetailtv;
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
        productinfoDetailtv = (TextView) findViewById(R.id.product_info_detail_tv);
        int productId = getIntent().getIntExtra(ARG_ID, -1);
        assert (productId != -1);
        //網絡訪問
        getInfoDetail(productId);
        commontitletv.setText(R.string.title_product_info);
        commonbackbtn.setOnClickListener(this);
        productinfotelcb.setOnClickListener(this);
        prdinfointocompanytv.setOnClickListener(this);
        productinfocollectcb.setOnClickListener(this);
        prdinfobtmll.setOnClickListener(this);

    }

    private void setData(DetailInfoBean infoBean) {
        if (!Constant.VERIFY_PASS.equals(infoBean.getVerify_result())) {
            popError(infoBean.getVerify_reason());
        }
        if (infoBean.getCompany_id().equals(Company.getInstance().getCompanyId())) {
            productinforlll.setVisibility(View.GONE);
        } else productinforlll.setVisibility(View.VISIBLE);
        prdinfocompanytv.setText(infoBean.getCompany_name());
        pi_title_tv.setText(infoBean.getTitle());
        pi_price_tv.setText(TextUtils.isEmpty(infoBean.getPrice()) ? "0" : infoBean.getPrice() + "");
        productinfolooknumtv.setText(infoBean.getRead_num() + "");
        if (infoBean.getUnit() != null) {
            pi_min_sell_num.setText(infoBean.getMin_sell_num() + "" + infoBean.getUnit());
        } else {
            pi_min_sell_num.setText(infoBean.getMin_sell_num() + "");
        }
        productinfocollectcb.setChecked(infoBean.getInwishlist() == 1);
        productinfocitytv.setText(infoBean.getRegion());

        if (!TextUtils.isEmpty(infoBean.getUrl_3d())) {//如果有3d图片，就只显示3D图片
            productinfoiweb.setVisibility(View.VISIBLE);
            productinfoimg.setVisibility(View.GONE);
            productinfoiweb.getSettings().setJavaScriptEnabled(true);
            productinfoiweb.loadUrl(infoBean.getUrl_3d());
        } else {
            productinfoimg.setVisibility(View.VISIBLE);
            productinfoiweb.setVisibility(View.GONE);
            ImageManager.load(infoBean.getImage(), productinfoimg);
        }
        String description = infoBean.getDescription();
        if (ContextUtil.isHtml(description)) {//根据是否为html文本，来用不同的控件显示
            productinfoDetailtv.setVisibility(View.GONE);
            productinfoDetailwv.setVisibility(View.VISIBLE);
            productinfoDetailwv.getSettings().setJavaScriptEnabled(true);
            productinfoDetailwv.loadDataWithBaseURL(HttpConstant.getRootUrl(), infoBean.getDescription(), "text/html", "UTF-8", "about:blank");
        } else {
            productinfoDetailwv.setVisibility(View.GONE);
            productinfoDetailtv.setVisibility(View.VISIBLE);
            productinfoDetailtv.setText(description);
        }
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
            case R.id.prd_info_btm_ll: {
                if (HttpUtil.isNetworkConnected(getApplicationContext()) && mDetailInfoBean != null) {
                    Intent intent = new Intent(this, InterpriseInfoActivity.class);
                    intent.putExtra(InterpriseInfoActivity.ARG_ID, mDetailInfoBean.getCompany_id());
                    intent.putExtra(InterpriseInfoActivity.ARG_NAME, mDetailInfoBean.getCompany_name());
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.go_company_info_toast), Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case R.id.product_info_tel_cb:
                Company company=Company.getInstance();
                if (company == null || company.getCompanyId() == null) {//用户没有登录。
                    LoginDialog loginDialog = new LoginDialog(ProductInfoActivity.this);
                    loginDialog.setOwnerActivity(this);
                    loginDialog.setCanceledOnTouchOutside(true);
                    loginDialog.setCancelable(true);
                    loginDialog.show();
                }else if (VipType.getCompanyInstance().getVipInfo().canDialSupply()) {
                    CallRecord record = new CallRecord();
                    record.setCall_id(Company.getInstance().getCompanyId());
                    record.setInfoId(mDetailInfoBean.getInfo_id());
                    record.setTo_id(mDetailInfoBean.getCompany_id());
                    record.setPhone_num(mDetailInfoBean.getPhone_num());
                    CallRecordActivity.CallRecordManager.dialNumber(this, record);
                }else{
                    EbingoDialog.newInstance(this, EbingoDialog.DialogStyle.STYLE_CANNOT_DIAL);
                }
                break;
            case R.id.product_info_collect_cb:
                if (productinfocollectcb.isChecked()) {
                    addCollection(mDetailInfoBean.getInfo_id());
                } else {
                    cancelCollection(mDetailInfoBean.getWishlist_id());
                }
                break;
            case R.id.prd_info_into_company_tv: {
                Intent intent = new Intent(this, InterpriseInfoActivity.class);
                intent.putExtra(InterpriseInfoActivity.ARG_ID, mDetailInfoBean.getCompany_id());
                intent.putExtra(InterpriseInfoActivity.ARG_NAME, mDetailInfoBean.getCompany_name());
                startActivity(intent);
                break;
            }

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
                    mDetailInfoBean.setWishlist_id(response.getJSONObject("data").getInt("wishlistid"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(int statusCode, String msg) {
                LogCat.w("--->",msg);
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
    private void getInfoDetail(int productId) {
        LogCat.i("--->", "info_id" + productId);
        EbingoRequestParmater parmater = new EbingoRequestParmater(getApplicationContext());
        Company company = Company.getInstance();
        if (company.getCompanyId() != null) {
            parmater.put("company_id", company.getCompanyId());
        } else {
            parmater.put("company_id", "0");    //游客
        }
        parmater.put("info_id", productId);

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


    private void popError(String result) {
        final TextView tv_warn = (TextView) findViewById(R.id.tv_warn);
        tv_warn.setText(result);
        tv_warn.setVisibility(View.VISIBLE);
        tv_warn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductInfoActivity.this, PublishEditActivity.class);
                intent.putExtra(PublishEditActivity.INFO, mDetailInfoBean);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
