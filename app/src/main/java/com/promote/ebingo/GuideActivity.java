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

import com.promote.ebingo.application.Constant;
import com.promote.ebingo.util.LogCat;

import java.util.ArrayList;


public class GuideActivity extends Activity implements ViewPager.OnPageChangeListener {
    ViewPager pager;
    ArrayList<View> views = new ArrayList<View>();

    @Override
    protected void onCreate(Bundle savedInstanceState)

    {
        super.onCreate(savedInstanceState);
        if (Constant.isFirstStart(getApplicationContext())) {
            setContentView(R.layout.activity_guide);
            pager = (ViewPager) findViewById(R.id.guide_pager);
            views.add(getImage(R.drawable.guide1));
            views.add(getImage(R.drawable.guide2));
            views.add(getImage(R.drawable.guide3));
            views.add(getImage(R.drawable.guide4));
            pager.setAdapter(new GuideAdapter());
            pager.setOnPageChangeListener(this);
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

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        LogCat.i("page:" + position);
    }

    @Override
    public void onPageSelected(int position) {
        if (position == 3) {
            Constant.savefirstStart(getApplicationContext());
            setLoading();
        }
    }


    @Override
    public void onPageScrollStateChanged(int state) {


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

    private void setLoading() {

        setContentView(R.layout.loadpage_layout);

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
