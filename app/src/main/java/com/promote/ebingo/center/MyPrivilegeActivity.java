package com.promote.ebingo.center;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.jch.lib.util.TextUtil;
import com.promote.ebingo.R;
import com.promote.ebingo.bean.Company;
import com.promote.ebingo.util.Dimension;
import com.promote.ebingo.util.ImageUtil;
import com.promote.ebingo.util.LogCat;

import java.io.IOException;

/**
 * Created by acer on 2014/9/11.
 */
public class MyPrivilegeActivity extends FragmentActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    private PrivilegeInfoFragment[] fragments;
    private int cur;
    private TextView tv_vipType;
    private TextView tv_name;
    private ImageView iv_head;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_privilege);
        ((RadioGroup) findViewById(R.id.rb_group)).setOnCheckedChangeListener(this);
        ((TextView) findViewById(R.id.common_title_tv)).setText("我的特权");
        findViewById(R.id.common_back_btn).setOnClickListener(this);
        tv_vipType= (TextView) findViewById(R.id.tv_vipType);
        tv_name= (TextView) findViewById(R.id.tv_name);
        iv_head= (ImageView) findViewById(R.id.iv_head);
        initFragment();
        init();
    }

    private void init() {
        Company company=Company.getInstance();
        if(company.getCompanyId()==null)return;
        if(!TextUtils.isEmpty(company.getVipType())) tv_vipType.setText(company.getVipType());
        if(!TextUtils.isEmpty(company.getName())) tv_name.setText(company.getName());
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
        fragments = new PrivilegeInfoFragment[4];
        fragments[0] = new PrivilegeInfoFragment();
        fragments[0].setVipType(VipType.VISITOR);

        fragments[1] = new PrivilegeInfoFragment();
        fragments[1].setVipType(VipType.NORMAL_VIP);

        fragments[2] = new PrivilegeInfoFragment();
        fragments[2].setVipType(VipType.VIP);

        fragments[3] = new PrivilegeInfoFragment();
        fragments[3].setVipType(VipType.VVIP);

        String vipType = Company.getInstance().getVipType();
        if (TextUtils.isEmpty(vipType)) {
            getSupportFragmentManager().beginTransaction().add(R.id.info_fragment_content, fragments[0]).commit();
        } else {
            for (int i = 0; i < fragments.length; i++) {
                if (fragments[i].getVipType().equals(vipType)) {
                    getSupportFragmentManager().beginTransaction().add(R.id.info_fragment_content, fragments[i]).commit();
                    cur = i;
                    break;
                }
            }
        }

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        switch (checkedId) {
            case R.id.rb_visitor:
                changeFrag(0);
                break;
            case R.id.rb_user:
                changeFrag(1);
                break;
            case R.id.rb_vip:
                changeFrag(2);
                break;
            case R.id.rb_vvip:
                changeFrag(3);
                break;
        }
    }

    private void changeFrag(int to) {
        if (fragments[to] == fragments[cur]) {
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
        ft.commitAllowingStateLoss();
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


    enum VipType{
        VISITOR("9","游客"),NORMAL_VIP("0","普通会员"),VIP("1","VIP用户"),VVIP("2","VVIP用户");
        public String code;
        public String name;

        VipType(String code, String name) {
            this.code = code;
            this.name = name;
        }


        @Override
        public String toString() {
            return code+":"+name;
        }

    }
}
