package com.promote.ebingo.InformationActivity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jch.lib.view.PagerSlidingTabStrip;
import com.promote.ebingo.R;

/**
 * 企业详情.
 */
public class InterpriseInfoActivity extends FragmentActivity implements View.OnClickListener{

    public static final String ARG_ID = "id";
    private PagerSlidingTabStrip interpriseinfotab;
    private ViewPager interpriseinfovp;
    /** 公司詳情,tab頁標題。 **/
    private String[] tabStr = null;
    private ImageView commonbackbtn;
    private TextView commontitletv;
    private MyPagerAdapter myPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interprise_info);
        initialize();
    }

    private void initialize() {
        commonbackbtn = (ImageView) findViewById(R.id.common_back_btn);
        commontitletv = (TextView) findViewById(R.id.common_title_tv);
        interpriseinfotab = (PagerSlidingTabStrip) findViewById(R.id.interprise_info_tab);
        interpriseinfovp = (ViewPager) findViewById(R.id.interprise_info_vp);

        tabStr = getResources().getStringArray(R.array.interprise_info_tab);
        myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        interpriseinfovp.setAdapter(myPagerAdapter);
        interpriseinfotab.setViewPager(interpriseinfovp);
        commonbackbtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id){

            case R.id.common_back_btn:{

                onBackPressed();
                finish();
            }

            default:{

            }

        }
    }


    /**
     * ViewPager adapter.
     */
    private class MyPagerAdapter extends FragmentPagerAdapter{

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabStr[position];
        }

        @Override
        public Fragment getItem(int position) {

            Fragment fragment = null;
            int index = position % tabStr.length;
            if (index == 0){
                fragment = InterpriseMainFragment.newInstance(tabStr[index]);
            }else if (index == 1){
                fragment = InterpriseDynamicFragment.newInstance(tabStr[index]);
            }else {
                fragment = SupplyDemandInfoFragment.newInstance(tabStr[index]);
            }

            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
        }

        @Override
        public int getCount() {
            return tabStr.length;
        }
    }


}
