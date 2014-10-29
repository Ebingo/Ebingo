package com.promote.ebingo;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.jch.lib.util.HttpUtil;
import com.promote.ebingo.application.Constant;
import com.promote.ebingo.bean.Company;
import com.promote.ebingo.center.settings.VersionManager;
import com.promote.ebingo.publish.login.LoginActivity;
import com.promote.ebingo.publish.login.LoginManager;
import com.promote.ebingo.publish.login.RegisterActivity;
import com.promote.ebingo.util.ContextUtil;
import com.promote.ebingo.util.FileUtil;
import com.promote.ebingo.util.LogCat;

import java.util.ArrayList;


public class GuideActivity extends Activity implements ViewPager.OnPageChangeListener, RadioGroup.OnCheckedChangeListener {
    ViewPager pager;
    ArrayList<View> views = new ArrayList<View>();
    final int LOGIN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)

    {
        super.onCreate(savedInstanceState);
        if (Constant.isFirstStart(getApplicationContext())) {
            setContentView(R.layout.activity_guide);
            pager = (ViewPager) findViewById(R.id.guide_pager);
//            ((RadioGroup) findViewById(R.id.indicator_group)).setOnCheckedChangeListener(this);
            views.add(getImage(R.drawable.guide1));
            views.add(getImage(R.drawable.guide2));
            views.add(getImage(R.drawable.guide3));
            views.add(getImage(R.drawable.guide4));
            views.add(getLastPage());
            pager.setAdapter(new GuideAdapter());
            pager.setOnPageChangeListener(this);
            showDot(0);
        } else {
            setLoading();
        }
        VersionManager.requestVersionCode(this, false);
    }

    /**
     * 获取导航的最后一页
     *
     * @return
     */
    private View getLastPage() {
        View v = View.inflate(this, R.layout.guide_last_page, null);
        View.OnClickListener lastPageListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_reg: {
                        Intent intent = new Intent(GuideActivity.this, RegisterActivity.class);
                        startActivityForResult(intent, RegisterActivity.REQUEST_CODE);
                        break;
                    }
                    case R.id.btn_login: {
                        Intent intent = new Intent(GuideActivity.this, MainActivity.class);
                        startActivity(intent);
                        Constant.savefirstStart(getApplicationContext());
                        finish();
                        break;
                    }
                }
            }
        };

        v.findViewById(R.id.btn_reg).setOnClickListener(lastPageListener);
        v.findViewById(R.id.btn_login).setOnClickListener(lastPageListener);
        return v;
    }

    /**
     * 获取每个导航页
     *
     * @param id 图片的资源ID
     * @return
     */
    private ImageView getImage(int id) {
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(id);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return imageView;
    }

    private float mSavedOffset = 0;
    private boolean scrollToEnd = false;

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        LogCat.i("page:" + position + " positionOffset:" + positionOffset + " positionOffsetPixels:" + positionOffsetPixels);
        if (position == views.size() - 1) {
            float delta = mSavedOffset - positionOffset;
            scrollToEnd = (delta == 0);
        }
        mSavedOffset = positionOffset;
    }

    @Override
    public void onPageSelected(int position) {
        showDot(position);
    }


    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_IDLE && pager.getCurrentItem() == pager.getChildCount() - 1 && scrollToEnd) {
//            loginBackground();
            Constant.savefirstStart(getApplicationContext());
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        for (int i = 0; i < group.getChildCount(); i++) {
            if (group.getChildAt(i).getId() == checkedId) {
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

    private void showDot(int index) {
//        ((RadioButton) (((RadioGroup) findViewById(R.id.indicator_group)).getChildAt(index))).setChecked(true);
    }

    private void setLoading() {
        setContentView(R.layout.loadpage_layout);

        loginBackground();
    }

    private void loginBackground() {
        String psw = ContextUtil.getCurCompanyPwd();
        String companyName = ContextUtil.getCurCompanyName();
        if (!TextUtils.isEmpty(psw) && !TextUtils.isEmpty(companyName) && HttpUtil.isNetworkConnected(this)) {
            new LoginManager().doLogin(companyName, psw, new LoginManager.Callback() {
                @Override
                public void onSuccess() {
                    toMainActivityDelay(500);
                }

                @Override
                public void onFail(String msg) {
                    toMainActivityDelay(500);
                }

            });
        } else {
            //如果使用用户名密码登陆失败，就直接加载上一次保存的公司信息
            Company.loadInstance((Company) new FileUtil().readFile(getApplicationContext(), FileUtil.FILE_COMPANY));
            toMainActivityDelay(500);
        }
    }

    private void toMainActivityDelay(long time) {
        LogCat.w("--->", String.format("Login success,toMainActivityDelay delay %d ms...", (int) time));
        LogCat.i("--->", "Company:" + Company.getInstance());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(GuideActivity.this, MainActivity.class));
                GuideActivity.this.finish();
            }
        }, time);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RegisterActivity.REQUEST_CODE: {
                if (resultCode == RESULT_OK) {
                    startActivity(new Intent(GuideActivity.this, MainActivity.class));
                    GuideActivity.this.finish();
                    Constant.savefirstStart(getApplicationContext());
                }
                break;
            }
        }
    }
}
