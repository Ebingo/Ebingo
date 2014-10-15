package com.promote.ebingo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.promote.ebingo.bean.Company;
import com.promote.ebingo.center.CenterFragment;
import com.promote.ebingo.find.FindFragment;
import com.promote.ebingo.home.HomeFragment;
import com.promote.ebingo.publish.PublishFragment;
import com.promote.ebingo.publish.login.LoginDialog;
import com.promote.ebingo.publish.login.LoginManager;
import com.promote.ebingo.util.ContextUtil;
import com.promote.ebingo.util.LogCat;


public class MainActivity extends FragmentActivity implements RadioGroup.OnCheckedChangeListener, CenterFragment.OnFragmentInteractionListener, PublishFragment.PublishCallback, HomeFragment.HomeFragmentListener {

    public static final String ARG_PUBLISH_TYPE = "publish_type";
    private RadioButton mainrb;
    private RadioButton findrb;
    private RadioButton publishrb;
    private RadioButton centerrb;
    private RadioGroup mainrg;

    private HomeFragment mMainFramgent = null;

    private FindFragment mFindFrament = null;
    private PublishFragment mPublishFragment = null;
    private CenterFragment mCenterFragment = null;
    /**
     * 正在显示的fragment. *
     */
    private Fragment mCurFragment = null;

    private FragmentManager mFM = null;
    /**
     * activty内容容器 *
     */
    private FrameLayout maincontentfl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogCat.i("--->", "onCreate");
        setContentView(R.layout.activity_main);
        initialize();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        LogCat.i("--->", "onRestoreInstanceState");
        Company.loadInstance((Company) savedInstanceState.getSerializable("company"));
        sendBroadcast(new Intent(LoginManager.ACTION_INVALIDATE));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("company", Company.getInstance());
        LogCat.i("--->", "onSaveInstanceState");
    }

    private void initialize() {

        mainrb = (RadioButton) findViewById(R.id.main_rb);
        findrb = (RadioButton) findViewById(R.id.find_rb);
        publishrb = (RadioButton) findViewById(R.id.publish_rb);
        centerrb = (RadioButton) findViewById(R.id.center_rb);
        mainrg = (RadioGroup) findViewById(R.id.main_rg);
        maincontentfl = (FrameLayout) findViewById(R.id.main_content_fl);

        mainrg.setOnCheckedChangeListener(this);
        //启动程序是默认加载第一个tab.
        mMainFramgent = HomeFragment.newInstance(null, null);
        mFM = getSupportFragmentManager();
        FragmentTransaction ft = mFM.beginTransaction();
        ft.add(R.id.main_content_fl, mMainFramgent, "main");
        ft.commitAllowingStateLoss();
        mCurFragment = mMainFramgent; //记录当前frag。
    }


    /**
     * 隐藏显示相应的frag，并将设置当前的fragment。
     *
     * @param showFrag 将要显示的frag
     * @param hideFrag 要隐藏的frag。
     */
    private void changeFrag(Fragment showFrag, Fragment hideFrag) {
        if (showFrag == hideFrag) {
            LogCat.e(MainActivity.class.getName() + ":changeFrag:showFrag=hideFragment");
            return;
        }
        FragmentTransaction ft = mFM.beginTransaction();
        if (showFrag.isAdded()) {
            ft.show(showFrag);

        } else {
            ft.add(R.id.main_content_fl, showFrag, null);

        }
        if (hideFrag != null && hideFrag.isAdded()) {
            ft.hide(hideFrag);
        }
        ft.commitAllowingStateLoss();
        mCurFragment = showFrag;

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        switch (checkedId) {

            case R.id.main_rb: {

                if (mMainFramgent == null) {
                    mMainFramgent = HomeFragment.newInstance(null, null);
                }
                changeFrag(mMainFramgent, mCurFragment);

                break;
            }
            case R.id.find_rb: {

                if (mFindFrament == null) {
                    mFindFrament = FindFragment.newInstance(null, null);
                }
                changeFrag(mFindFrament, mCurFragment);
                break;
            }
            case R.id.publish_rb: {

                if (mPublishFragment == null) {
                    mPublishFragment = PublishFragment.newInstance(null, null);
                    mPublishFragment.setCallback(this);
                }
                changeFrag(mPublishFragment, mCurFragment);
                break;
            }
            case R.id.center_rb: {

                if (mCenterFragment == null) {
                    mCenterFragment = CenterFragment.newInstance(null, null);
                }

                changeFrag(mCenterFragment, mCurFragment);

                break;
            }
            default: {
                break;
            }

        }

    }

    private void setDimAmount(float alpha, float dimAmount) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = alpha;
        lp.dimAmount = dimAmount;

        getWindow().setAttributes(lp);

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogCat.i("--->", "requestCode:" + requestCode + " Result ok?:" + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (mPublishFragment != null && mPublishFragment.isMyRequest(requestCode)) {
            LogCat.i("--->", "mPublishFragment->onActivityResult");
            mPublishFragment.onActivityResult(requestCode, resultCode, data);
        } else if (requestCode == LoginDialog.REQUEST_CODE && resultCode == RESULT_OK) {
            if (mCenterFragment == null) {
                mCenterFragment = new CenterFragment();
            }
            changeFrag(mCenterFragment, mCurFragment);
            centerrb.setChecked(true);
        } else if (requestCode == BaseFragment.TO_SCAN) {
            if (!mMainFramgent.isHidden()) {
                mMainFramgent.onActivityResult(requestCode, resultCode, data);
            } else if (!mFindFrament.isHidden()) {
                mFindFrament.onActivityResult(requestCode, resultCode, data);
            }
        }


    }

    @Override
    public void onLoginCancel() {
        mainrb.setChecked(true);
    }

    private long lastTime = 0;

    @Override
    public void onBackPressed() {
        long curTime = System.currentTimeMillis();
        if (curTime - lastTime < 1500) {
            super.onBackPressed();
        } else {
            ContextUtil.toast("再按一次退出！");
            lastTime = curTime;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogCat.i("--->", "onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LogCat.i("--->", "onRestart");
    }

    @Override
    public void moreHotMarket() {
        findrb.setChecked(true);
    }
}
