package com.promote.ebingo.InformationActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jch.lib.util.DialogUtil;
import com.jch.lib.util.HttpUtil;
import com.promote.ebingo.R;
import com.promote.ebingo.application.Constant;
import com.promote.ebingo.application.HttpConstant;
import com.promote.ebingo.bean.CallRecord;
import com.promote.ebingo.bean.Company;
import com.promote.ebingo.bean.CompanyVipInfo;
import com.promote.ebingo.bean.DetailInfoBean;
import com.promote.ebingo.center.CallRecordActivity;
import com.promote.ebingo.impl.EbingoRequestParmater;
import com.promote.ebingo.impl.GetInfoDetail;
import com.promote.ebingo.publish.EbingoDialog;
import com.promote.ebingo.publish.PublishEditActivity;
import com.promote.ebingo.publish.VipType;
import com.promote.ebingo.publish.login.LoginDialog;
import com.promote.ebingo.util.ContextUtil;

/**
 * 求购信息详情。
 */
public class BuyInfoActivity extends Activity implements View.OnClickListener {

    public static final String DEMAND_ID = "demand_id";
    private TextView buyinfointocompanytv;
    private TextView buyinfocompanytv;
    private RelativeLayout buyinfobtmll;
    private TextView buyinfonametv;
    private TextView buynumtv;
    private TextView buyinfopublishtimetv;
    private TextView publishlooknumtv;
    private TextView productinfoDetailtv;
    private WebView buyinfodetailwv;
    private Button buyInfocontactphonetv;
    private ImageView commonbackbtn;
    /**
     * 企业vip标签. *
     */
    private ImageView mIntVipImg = null;
    private TextView commontitletv;
    private DetailInfoBean mDetailInfoBean = null;

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
        mIntVipImg = (ImageView) findViewById(R.id.buy_info_btm_img);
        buyinfopublishtimetv = (TextView) findViewById(R.id.buy_info_publish_time_tv);
        publishlooknumtv = (TextView) findViewById(R.id.publish_look_num_tv);
        buyinfodetailwv = (WebView) findViewById(R.id.buy_info_detail_wv);
        productinfoDetailtv = (TextView) findViewById(R.id.buy_info_detail_tv);
        buyInfocontactphonetv = (Button) findViewById(R.id.buy_info_contact_phone_tv);

        int demandId = getIntent().getIntExtra(DEMAND_ID, -1);
        assert (demandId != -1);
        commonbackbtn.setOnClickListener(this);
        commontitletv.setText(getString(R.string.title_buy_detail));
        buyInfocontactphonetv.setOnClickListener(this);
        buyinfobtmll.setOnClickListener(this);
        getInfoDetail();
    }


    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id) {
            case R.id.common_back_btn: {

                onBackPressed();
                this.finish();
                break;
            }

            case R.id.buy_info_contact_phone_tv: {

                if (mDetailInfoBean != null) {
                    Company company = Company.getInstance();
                    if (company == null || company.getCompanyId() == null) {//用户没有登录。
                        LoginDialog loginDialog = new LoginDialog(BuyInfoActivity.this);
                        loginDialog.setOwnerActivity(this);
                        loginDialog.setCanceledOnTouchOutside(true);
                        loginDialog.setCancelable(true);
                        loginDialog.show();
                    } else if (!Company.getInstance().getVipInfo().callDemand()) {//不是vip会员.
                        EbingoDialog dialog = EbingoDialog.newInstance(this, EbingoDialog.DialogStyle.STYLE_TO_PRIVILEGE);
                        dialog.setMessage(getString(R.string.cannot_call_demand, VipType.Standard_VIP.name));
                        dialog.show();
                    } else {
                        CallRecord record = new CallRecord();
                        record.setPhone_num(mDetailInfoBean.getPhone_num());
                        record.setTo_id(mDetailInfoBean.getCompany_id());
                        record.setCall_id(Company.getInstance().getCompanyId());
                        record.setInfoId(mDetailInfoBean.getInfo_id());
                        record.setContacts(mDetailInfoBean.getContacts());
                        CallRecordActivity.CallRecordManager.dialNumber(this, record);
                    }
                }

                break;
            }

            case R.id.buy_info_btm_ll: {
                VipType companyVip = VipType.getCompanyInstance();
                if (companyVip.compareTo(VipType.Standard_VIP) < 0) {
                    EbingoDialog dialog = EbingoDialog.newInstance(this, EbingoDialog.DialogStyle.STYLE_TO_PRIVILEGE);
                    dialog.setMessage(getString(R.string.can_scan_demand_company_info, VipType.getCompanyInstance().name));
                    dialog.show();
                } else if (mDetailInfoBean != null && HttpUtil.isNetworkConnected(getApplicationContext())) {
                    Intent intent = new Intent(BuyInfoActivity.this, InterpriseInfoActivity.class);
                    intent.putExtra(InterpriseInfoActivity.ARG_ID, mDetailInfoBean.getCompany_id());
                    intent.putExtra(InterpriseInfoActivity.ARG_NAME, mDetailInfoBean.getCompany_name());
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.go_company_info_toast), Toast.LENGTH_SHORT).show();
                }

                break;

            }
//            case R.id.buy_info_into_company_tv: {
//                Intent intent = new Intent(this, InterpriseInfoActivity.class);
//                intent.putExtra(InterpriseInfoActivity.ARG_ID, mDetailInfoBean.getCompany_id());
//                intent.putExtra(InterpriseInfoActivity.ARG_NAME, mDetailInfoBean.getCompany_name());
//                startActivity(intent);
//                break;
//            }
            default: {

            }

        }

    }

    /**
     * 獲得詳情。
     */
    private void getInfoDetail() {

        EbingoRequestParmater parmater = new EbingoRequestParmater(getApplicationContext());
        Company company = Company.getInstance();
        if (company.getCompanyId() != null) {
            parmater.put("company_id", company.getCompanyId());
        } else {
            parmater.put("company_id", "0");    //游客
        }

        parmater.put("info_id", getIntent().getIntExtra(DEMAND_ID, -1));
        final Dialog dialog = DialogUtil.waitingDialog(this);
        GetInfoDetail.getInfoDetail(parmater, new GetInfoDetail.CallBack() {
            @Override
            public void onFailed(String msg) {
                //nodata
                dialog.dismiss();
            }

            @Override
            public void onSuccess(DetailInfoBean detailInfoBean) {
                mDetailInfoBean = detailInfoBean;
                initData();
                dialog.dismiss();
            }
        });

    }

    /**
     * 填充详细信息数据.
     */
    private void initData() {
        Integer sellerId = mDetailInfoBean.getCompany_id();
        if (sellerId == null) {
            //根据发布信息的公司id是否为空，来判断信息是否被删除。
            EbingoDialog.newInstance(this, EbingoDialog.DialogStyle.STYLE_INFO_DELETED).show();
            return;
        }

        //如果发布信息的公司id与本公司相同，隐藏拨号按钮
        if (mDetailInfoBean.getCompany_id().equals(Company.getInstance().getCompanyId())) {
            buyInfocontactphonetv.setVisibility(View.GONE);
        }

        //如果审核不通过就弹出提示
        if (!Constant.VERIFY_PASS.equals(mDetailInfoBean.getVerify_result())) {
            popError(mDetailInfoBean.getVerify_reason());
        }
        //设置求购信息标题
        buyinfonametv.setText(mDetailInfoBean.getTitle());
        //设置求购数量
        buynumtv.setText(String.valueOf(mDetailInfoBean.getBuy_num()));
        //设置发布时间
        buyinfopublishtimetv.setText(mDetailInfoBean.getCreate_time());
        //设置浏览次数
        publishlooknumtv.setText(String.valueOf(mDetailInfoBean.getRead_num()));
        //如果为体验会员就不显示公司名称
        VipType vipType = VipType.getCompanyInstance();
        if (vipType.compareTo(VipType.Experience_Vip) <= 0&&!mDetailInfoBean.getCompany_id().equals(Company.getInstance().getCompanyId())) {
            buyinfocompanytv.setText("****公司");
        } else {
            buyinfocompanytv.setText(mDetailInfoBean.getCompany_name());
        }
        mIntVipImg.setImageDrawable(ContextUtil.getVipImgByType(getResources(), mDetailInfoBean.getVip_type()));

        String description = mDetailInfoBean.getDescription();
        //根据是否为html文本，来用不同的控件显示
        if (ContextUtil.isHtml(description)) {
            productinfoDetailtv.setVisibility(View.GONE);
            buyinfodetailwv.setVisibility(View.VISIBLE);
            buyinfodetailwv.getSettings().setJavaScriptEnabled(true);
            buyinfodetailwv.loadDataWithBaseURL(HttpConstant.getRootUrl(), description, "text/html", "UTF-8", "about:blank");

        } else {
            buyinfodetailwv.setVisibility(View.GONE);
            productinfoDetailtv.setVisibility(View.VISIBLE);
            productinfoDetailtv.setText(description);
        }
    }

    private void popError(String msg) {
        final TextView tv_warn = (TextView) findViewById(R.id.tv_warn);
        tv_warn.setText(msg);
        tv_warn.setVisibility(View.VISIBLE);
        tv_warn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BuyInfoActivity.this, PublishEditActivity.class);
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
