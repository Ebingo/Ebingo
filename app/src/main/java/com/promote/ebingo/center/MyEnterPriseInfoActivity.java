package com.promote.ebingo.center;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jch.lib.util.ImageManager;
import com.jch.lib.util.TextUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.promote.ebingo.BaseActivity;
import com.promote.ebingo.R;
import com.promote.ebingo.bean.Company;
import com.promote.ebingo.publish.VipType;
import com.promote.ebingo.publish.login.LoginActivity;

/**
 * Created by acer on 2014/9/16.
 */
public class MyEnterPriseInfoActivity extends BaseActivity {
    private TextView tv_companyName;
    private TextView tv_region_top;
    private TextView tv_vipType;
    private TextView tv_region;
    private TextView tv_website;
    private TextView tv_email;
    private TextView tv_phone;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_enterprise_activity);
        init();
        if (Company.getInstance().getCompanyId() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setData();
    }

    private void init() {
        tv_companyName = (TextView) findViewById(R.id.tv_companyName);
        tv_region_top = (TextView) findViewById(R.id.tv_region_top);
        tv_vipType = (TextView) findViewById(R.id.tv_vipType);
        tv_region = (TextView) findViewById(R.id.tv_region);
        tv_website = (TextView) findViewById(R.id.tv_website);
        tv_email = (TextView) findViewById(R.id.tv_email);
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        image = (ImageView) findViewById(R.id.image);
    }

    private void setData() {
        Company company = Company.getInstance();
        if (!TextUtils.isEmpty(company.getName())) tv_companyName.setText(company.getName());
        tv_region_top.setText(company.getRegion());
        tv_vipType.setText(VipType.nameOf(company.getVipType()));
        tv_region.setText(company.getRegion());
        tv_website.setText(company.getWebsite());
        tv_email.setText(company.getEmail());
        tv_phone.setText(company.getHeadPhone());
        if (!TextUtils.isEmpty(company.getImage())) ImageManager.load(company.getImage(), image);
    }


}
