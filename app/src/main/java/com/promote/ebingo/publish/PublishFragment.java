package com.promote.ebingo.publish;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.jch.lib.util.DialogUtil;
import com.jch.lib.util.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.promote.ebingo.R;
import com.promote.ebingo.application.HttpConstant;
import com.promote.ebingo.bean.Company;
import com.promote.ebingo.center.MyPrivilegeActivity;
import com.promote.ebingo.center.MySupplyActivity;
import com.promote.ebingo.impl.EbingoRequestParmater;
import com.promote.ebingo.publish.login.LoginDialog;
import com.promote.ebingo.util.ContextUtil;
import com.promote.ebingo.util.LogCat;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link PublishFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PublishFragment extends Fragment implements RadioGroup.OnCheckedChangeListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    /**
     * 求购
     */
    public static final String TYPE_DEMAND = "1";
    /**
     * 供应
     */
    public static final String TYPE_SUPPLY = "2";
    /**
     * 选择分类
     */
    public static final int PICK_CATEGORY = 1 << 0;
    /**
     * 选择区域
     */
    public static final int PICK_REGION = 1 << 1;
    /**
     * 选择图片
     */
    public static final int PICK_IMAGE = 1 << 2;
    /**
     * 拍照
     */
    public static final int PICK_CAMERA = 1 << 3;
    /**
     * 选择描述
     */
    public static final int PICK_DESCRIPTION = 1 << 4;
    /**
     * 选择标签
     */
    public static final int PICK_TAGS = 1 << 5;
    /**
     * 预览
     */
    public static final int PREVIEW = 1 << 6;

    /**
     * 标记由发布求购页面发出的选择
     */
    public static final int PICK_FOR_DEMAND = 1 << 13;
    /**
     * 标记选由发布供应页面发出的选择
     */
    public static final int PICK_FOR_SUPPLY = 1 << 14;


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private LoginDialog loginDialog;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RadioGroup tabs;
    private ViewPager content;
    PublishDemand publishDemand = new PublishDemand();
    PublishSupply publishSupply = new PublishSupply();

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PublishFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PublishFragment newInstance(String param1, String param2) {
        PublishFragment fragment = new PublishFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public PublishFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_publish, container, false);
        tabs = (RadioGroup) view.findViewById(R.id.publish_type);
        content = (ViewPager) view.findViewById(R.id.publish_content);
        tabs.setOnCheckedChangeListener(this);
        PublishContentAdapter adapter = new PublishContentAdapter(getChildFragmentManager(), content, tabs);
        adapter.add(publishDemand).add(publishSupply);
        content.setAdapter(adapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        showLoginDialog();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            showLoginDialog();
        }
    }

    private void showLoginDialog() {
        if (loginDialog == null) {
            loginDialog = new LoginDialog(getActivity());
            loginDialog.setCancelable(false);
            loginDialog.setOwnerActivity(getActivity());
            loginDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (callback != null && Company.getInstance().getCompanyId() == null)
                        callback.onLoginCancel();
                }
            });
        }
        if (Company.getInstance().getCompanyId() == null && !loginDialog.isShowing() && !isHidden()) {
            loginDialog.show();
        }
    }

    private void showVipDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setMessage("您好！您目前是普通会员，没有权限发布自己的产品供应信息，您可以点击下方升级按钮进行升级。")
        .setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                content.setCurrentItem(0);

            }
        })
        .setPositiveButton("升级",new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent=new Intent(getActivity(), MyPrivilegeActivity.class);
                startActivity(intent);
            }
        });
    }

    public PublishCallback getCallback() {
        return callback;
    }

    public void setCallback(PublishCallback callback) {
        this.callback = callback;
    }

    private PublishCallback callback;

    public interface PublishCallback {
        public void onLoginCancel();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        LogCat.i("--->","onCheckedChanged");
        switch (checkedId) {
            case R.id.rb_publish_demand:
                content.setCurrentItem(1);
                break;
            case R.id.rb_publish_supply:
                content.setCurrentItem(0);
                break;
        }
    }

    class PublishContentAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {

        private RadioGroup tabs;
        private ViewPager content;
        private List<Fragment> fragments;

        public PublishContentAdapter(FragmentManager fm, ViewPager content, RadioGroup tabs) {
            super(fm);
            fragments = new ArrayList<Fragment>();
            this.tabs = tabs;
            this.content = content;
            this.content.setOnPageChangeListener(this);
        }

        public PublishContentAdapter add(Fragment f) {
            fragments.add(f);
            return this;
        }

        @Override
        public void onPageScrolled(int i, float v, int i2) {

        }

        @Override
        public void onPageSelected(int i) {
            ((RadioButton) tabs.getChildAt(i)).setChecked(true);
            final String vipType=Company.getInstance().getVipType();
            if(!VipType.canPublishSupply(vipType)){
                showVipDialog();
            }
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public Fragment getItem(int i) {
            return fragments.get(i);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogCat.i("--->", Integer.toBinaryString(requestCode));
        if (isPickFor(PICK_FOR_DEMAND, requestCode))
            publishDemand.onActivityResult(requestCode, resultCode, data);
        //publishSupply能自动接收到onActivityResult回调，可能与add的顺序有关。
        if (isPickFor(PICK_FOR_SUPPLY, requestCode))
            publishSupply.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 判断是否为Publish页面发出的请求
     *
     * @param requestCode
     * @return
     */
    public boolean isMyRequest(final int requestCode) {
        return isPickFor(PICK_FOR_DEMAND, requestCode) || isPickFor(PICK_FOR_SUPPLY, requestCode);
    }

    public boolean isPickFor(int code, final int requestCode) {
        int myCode = requestCode;
        return (myCode & code) != 0;
    }

    public void startPublish(EbingoRequestParmater parmater) {

        final ProgressDialog dialog = DialogUtil.waitingDialog(getActivity());
        HttpUtil.post(HttpConstant.saveInfo, parmater, new JsonHttpResponseHandler("utf-8") {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                ContextUtil.toast(response);
                try {
                    JSONObject result = response.getJSONObject("response");
                    if (HttpConstant.CODE_OK.equals(result.getString("code"))) {
                        Intent intent = new Intent(getActivity(), MySupplyActivity.class);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                ContextUtil.toast(responseString);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dialog.dismiss();
            }
        });
    }

    public static class Error {

        public static final int CATEGORY_EMPTY = 1;
        public static final int REGION_EMPTY = 2;
        public static final int IMAGE_EMPTY = 3;
        public static final int DESCRIPTION_EMPTY = 4;
        public static final int TAGS_EMPTY = 5;
        public static final int TITLE_EMPTY = 6;
        public static final int BUY_NUM_EMPTY = 7;
        public static final int CONTACT_EMPTY = 8;
        public static final int PHONE_EMPTY = 9;
        public static final int PRICE_EMPTY = 10;
        public static final int MIN_SELL_NUM_EMPTY = 11;
        public static final int TITLE_LENGTH_ERROR = 12;
        public static final int DESCRIPTION_LENGTH_ERROR = 13;
        public static final int CONTACT_LENGTH_ERROR = 14;
        public static final int PHONE_FORMAT_ERROR = 15;
        private static Map<Integer, String> errorMap = new HashMap<Integer, String>();

        static {
            errorMap.put(CATEGORY_EMPTY, "请输入类别");
            errorMap.put(REGION_EMPTY, "请输入区域");
            errorMap.put(IMAGE_EMPTY, "请选择图片");
            errorMap.put(DESCRIPTION_EMPTY, "请输入产品描述");
            errorMap.put(DESCRIPTION_LENGTH_ERROR, "描述需要10字以上");
            errorMap.put(TAGS_EMPTY, "请添加标签");
            errorMap.put(TITLE_EMPTY, "请编辑标题");
            errorMap.put(TITLE_LENGTH_ERROR, "标题长度30字以下");
            errorMap.put(BUY_NUM_EMPTY, "请输入求购数量");
            errorMap.put(CONTACT_EMPTY, "请填写联系人姓名");
            errorMap.put(CONTACT_LENGTH_ERROR, "联系人姓名为2-4字");
            errorMap.put(PHONE_EMPTY, "请填写联系人手机号");
            errorMap.put(PHONE_FORMAT_ERROR, "手机格式不正确");
            errorMap.put(PRICE_EMPTY, "请输入价格");
            errorMap.put(MIN_SELL_NUM_EMPTY, "请输入起订标准");
        }

        public static String get(int code) {
            return errorMap.get(code);
        }

        public static void showError(View v, int code) {
            ContextUtil.toast(get(code));
            v.requestFocus();
        }
    }

}
