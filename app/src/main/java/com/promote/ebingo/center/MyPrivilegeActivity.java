package com.promote.ebingo.center;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.promote.ebingo.R;
import com.promote.ebingo.bean.Company;
import com.promote.ebingo.publish.VipType;
import com.promote.ebingo.util.Dimension;
import com.promote.ebingo.util.ImageUtil;
import com.promote.ebingo.util.LogCat;

import java.io.IOException;

/**
 * Created by acer on 2014/9/11.
 */
public class MyPrivilegeActivity extends FragmentActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    public static final String SHOW_VIP_CODE = "show_vip";
    private PrivilegeInfoFragment[] fragments;
    private int cur = 0;
    private TextView tv_vipType;
    private TextView tv_name;
    private ImageView iv_head;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_privilege);
        ((RadioGroup) findViewById(R.id.rb_group)).setOnCheckedChangeListener(this);
        ((TextView) findViewById(R.id.common_title_tv)).setText(getTitle());
        findViewById(R.id.common_back_btn).setOnClickListener(this);
        tv_vipType = (TextView) findViewById(R.id.tv_vipType);
        tv_name = (TextView) findViewById(R.id.tv_name);
        iv_head = (ImageView) findViewById(R.id.iv_head);
        initFragment();
        init();
    }

    private void init() {
        Company company = Company.getInstance();
        if (company.getCompanyId() == null) return;
        VipType vipType = VipType.getCompanyInstance();
        tv_vipType.setText(vipType.name);
        tv_vipType.setCompoundDrawables(null, null, vipType.getIcon(this), null);
        if (!TextUtils.isEmpty(company.getName())) tv_name.setText(company.getName());
        setHeadImage(company.getImageUri());
    }

    /**
     * 设置头像
     *
     * @param uri
     */
    public void setHeadImage(Uri uri) {
        if (uri == null) {
            LogCat.e("--->", "setHeadImage uriError uri=" + uri);
            return;
        }

        try {
            Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            iv_head.setImageBitmap(ImageUtil.roundBitmap(bm, (int) Dimension.dp(48)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initFragment() {
        VipType[] vipTypes = VipType.values();
        fragments = new PrivilegeInfoFragment[vipTypes.length];
        for (int i = 0; i < vipTypes.length; i++) {//初始化vipType信息
            fragments[i] = PrivilegeInfoFragment.newInstance(vipTypes[i]);
        }
        getSupportFragmentManager().beginTransaction().add(R.id.info_fragment_content, fragments[0]).commit();
        String intentVipCode = getIntent().getStringExtra(SHOW_VIP_CODE);
        VipType showType;
        if (!TextUtils.isEmpty(intentVipCode)) {
            showType = VipType.parse(intentVipCode);
        } else {
            showType = VipType.getCompanyInstance();
        }
        ((RadioGroup) findViewById(R.id.rb_group)).getChildAt(showType.ordinal()).performClick();

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        for (int i = 0; i < group.getChildCount(); i++) {
            RadioButton radioButton = (RadioButton) group.getChildAt(i);
            radioButton.clearAnimation();
            if (radioButton.getId() == checkedId) {
                TranslateAnimation translateAnimation = new TranslateAnimation(-4, 2, 0, 0);
                translateAnimation.setDuration(100);
                translateAnimation.setFillAfter(true);
                radioButton.startAnimation(translateAnimation);
                changeFrag(i);
                break;
            }
        }
    }

    private void changeFrag(int to) {
        if (to == cur) {
            return;
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (fragments[to].isAdded()) {
            ft.show(fragments[to]);
        } else {
            ft.add(R.id.info_fragment_content, fragments[to], null);
        }
        if (fragments[cur] != null && fragments[cur].isAdded()) {
            ft.hide(fragments[cur]);
        }
        ft.commit();
        cur = to;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.common_back_btn:
                finish();
                break;
        }
    }

}
