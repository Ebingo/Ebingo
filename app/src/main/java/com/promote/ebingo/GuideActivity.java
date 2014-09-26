package com.promote.ebingo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.promote.ebingo.application.Constant;
import com.promote.ebingo.bean.Company;
import com.promote.ebingo.util.FileUtil;
import com.promote.ebingo.util.LogCat;

import java.util.ArrayList;


public class GuideActivity extends Activity implements ViewPager.OnPageChangeListener,RadioGroup.OnCheckedChangeListener {
    ViewPager pager;
    ArrayList<View> views = new ArrayList<View>();
    RadioGroup indicator_group;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Constant.isFirstStart(getApplicationContext())) {
            setContentView(R.layout.activity_guide);
            pager = (ViewPager) findViewById(R.id.guide_pager);
            indicator_group = (RadioGroup) findViewById(R.id.indicator_group);
            indicator_group.setOnCheckedChangeListener(this);
            views.add(getImage(R.drawable.guide1));
            views.add(getImage(R.drawable.guide2));
            views.add(getImage(R.drawable.guide3));
            views.add(getImage(R.drawable.guide4));
            pager.setAdapter(new GuideAdapter());
            pager.setOnPageChangeListener(this);
            showDot(0);
        } else {
            setLoading();
        }
    }

    private ImageView getImage(int id) {
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(id);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return imageView;
    }

    private float mSavedOffset =0;
    private boolean scrollToEnd=false;
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        LogCat.i("page:" + position+" positionOffset:"+positionOffset+" positionOffsetPixels:"+positionOffsetPixels);
        if (position==3){
            float delta=mSavedOffset-positionOffset;
            scrollToEnd=(delta==0);
        }
        mSavedOffset =positionOffset;

    }

    @Override
    public void onPageSelected(int position) {
        showDot(position);
    }


    @Override
    public void onPageScrollStateChanged(int state) {
        if (state==ViewPager.SCROLL_STATE_IDLE&&pager.getCurrentItem()==3&&scrollToEnd){
            setLoading();
            Constant.savefirstStart(getApplicationContext());
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        for(int i=0;i<group.getChildCount();i++){
            if (group.getChildAt(i).getId()==checkedId){
                pager.setCurrentItem(i);
                break;
            }

        }
    }

    class GuideAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(views.get(position));
            return views.get(position);
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(views.get(position));
        }
    }

    private void showDot(int index){
        ((RadioButton)(indicator_group.getChildAt(index))).setChecked(true);
    }

    private void setLoading() {

        setContentView(R.layout.loadpage_layout);
        Company.loadInstance((Company) new FileUtil().readFile(getApplicationContext(),FileUtil.FILE_COMPANY));
        new Thread() {
            @Override
            public void run() {
                super.run();

                try {
                    sleep(2000);    //暂停两秒  可以用来加载首页数据.
                } catch (InterruptedException e) {

                }

                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == 0) {
                startActivity(new Intent(GuideActivity.this, MainActivity.class));
                GuideActivity.this.finish();
            }
        }
    };
}
