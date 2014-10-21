package com.promote.ebingo.publish;import android.content.Context;import android.content.DialogInterface;import android.content.Intent;import android.os.Bundle;import android.support.v4.app.Fragment;import android.support.v4.app.FragmentManager;import android.support.v4.app.FragmentPagerAdapter;import android.support.v4.view.ViewPager;import android.text.TextUtils;import android.util.SparseArray;import android.util.SparseIntArray;import android.view.LayoutInflater;import android.view.View;import android.view.ViewGroup;import android.widget.RadioButton;import android.widget.RadioGroup;import com.jch.lib.util.HttpUtil;import com.jch.lib.view.PagerSlidingTabStrip;import com.promote.ebingo.R;import com.promote.ebingo.application.HttpConstant;import com.promote.ebingo.bean.Company;import com.promote.ebingo.center.MyPrivilegeActivity;import com.promote.ebingo.impl.EbingoHandler;import com.promote.ebingo.impl.EbingoRequestParmater;import com.promote.ebingo.publish.login.LoginDialog;import com.promote.ebingo.publish.login.LoginManager;import com.promote.ebingo.util.ContextUtil;import com.promote.ebingo.util.LogCat;import org.json.JSONException;import org.json.JSONObject;import java.util.ArrayList;import java.util.HashMap;import java.util.List;import java.util.Map;import static android.support.v4.view.ViewPager.SimpleOnPageChangeListener;/** * A simple {@link Fragment} subclass. * Activities that contain this fragment must implement the * to handle interaction events. * Use the {@link PublishFragment#newInstance} factory method to * create an instance of this fragment. */public class PublishFragment extends Fragment {    // TODO: Rename parameter arguments, choose names that match    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER    /**     * 求购     */    public static final String TYPE_DEMAND = "1";    /**     * 供应     */    public static final String TYPE_SUPPLY = "2";    /**     * 选择分类     */    public static final int PICK_CATEGORY = 1 << 0;    /**     * 选择区域     */    public static final int PICK_REGION = 1 << 1;    /**     * 选择图片     */    public static final int PICK_IMAGE = 1 << 2;    /**     * 拍照     */    public static final int PICK_CAMERA = 1 << 3;    /**     * 选择描述     */    public static final int PICK_DESCRIPTION = 1 << 4;    /**     * 选择标签     */    public static final int PICK_TAGS = 1 << 5;    /**     * 预览     */    public static final int PREVIEW = 1 << 6;    /**     * 选择3d     */    public static final int APPLY_3D = 1 << 7;    /**     * 剪裁     */    public static final int CROP = 1 << 8;    /**     * 标记由发布求购页面发出的选择     */    public static final int PICK_FOR_DEMAND = 1 << 13;    /**     * 标记选由发布供应页面发出的选择     */    public static final int PICK_FOR_SUPPLY = 1 << 14;    public static final int REQUEST_MASK = 0xfff;    private static final String ARG_PARAM1 = "param1";    private static final String ARG_PARAM2 = "param2";    private LoginDialog loginDialog;    // TODO: Rename and change types of parameters    private String mParam1;    private String mParam2;    private ViewPager viewPager;    PublishDemand publishDemand = new PublishDemand();    PublishSupply publishSupply = new PublishSupply();    /**     * Use this factory method to create a new instance of     * this fragment using the provided parameters.     *     * @param param1 Parameter 1.     * @param param2 Parameter 2.     * @return A new instance of fragment PublishFragment.     */    // TODO: Rename and change types and number of parameters    public static PublishFragment newInstance(String param1, String param2) {        PublishFragment fragment = new PublishFragment();        Bundle args = new Bundle();        args.putString(ARG_PARAM1, param1);        args.putString(ARG_PARAM2, param2);        fragment.setArguments(args);        return fragment;    }    public PublishFragment() {        // Required empty public constructor    }    @Override    public void onCreate(Bundle savedInstanceState) {        super.onCreate(savedInstanceState);        if (getArguments() != null) {            mParam1 = getArguments().getString(ARG_PARAM1);            mParam2 = getArguments().getString(ARG_PARAM2);        }    }    @Override    public View onCreateView(LayoutInflater inflater, ViewGroup container,                             Bundle savedInstanceState) {        // Inflate the layout for this fragment        View view = inflater.inflate(R.layout.fragment_publish, container, false);        init(view);        return view;    }    private void init(View view) {        PagerSlidingTabStrip strip = (PagerSlidingTabStrip) view.findViewById(R.id.publish_tab);        viewPager = (ViewPager) view.findViewById(R.id.publish_content);        PublishContentAdapter adapter = new PublishContentAdapter(getChildFragmentManager());        adapter.add(publishDemand).add(publishSupply);        viewPager.setAdapter(adapter);        strip.setViewPager(viewPager);        strip.setOnPageChangeListener(new SimpleOnPageChangeListener() {            @Override            public void onPageSelected(int i) {                if (i == 1) {                    getSupplyRemainNum();                }            }        });    }    @Override    public void onResume() {        super.onResume();        showLoginDialog();    }    @Override    public void onHiddenChanged(boolean hidden) {        super.onHiddenChanged(hidden);        if (!hidden) {            showLoginDialog();        }    }    private void showLoginDialog() {        if (loginDialog == null) {            loginDialog = new LoginDialog(getActivity());            loginDialog.setCancelable(false);            loginDialog.setOwnerActivity(getActivity());            loginDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {                @Override                public void onDismiss(DialogInterface dialog) {                    if (callback != null && Company.getInstance().getCompanyId() == null)                        callback.onLoginCancel();                }            });        }        if (Company.getInstance().getCompanyId() == null && !loginDialog.isShowing() && !isHidden()) {            loginDialog.show();        }    }    public PublishCallback getCallback() {        return callback;    }    public void setCallback(PublishCallback callback) {        this.callback = callback;    }    private PublishCallback callback;    public interface PublishCallback {        public void onLoginCancel();    }    @Override    public void onDetach() {        super.onDetach();    }    private void getSupplyRemainNum() {        EbingoRequestParmater parmater = new EbingoRequestParmater(getActivity());        parmater.put("company_id", Company.getInstance().getCompanyId());        HttpUtil.post(HttpConstant.canPublishSupplyInfo, parmater, new EbingoHandler() {            @Override            public void onSuccess(int statusCode, JSONObject response) {                try {                    String data = response.getString("data");                    if ("0".equals(data)) {                        EbingoDialog dialog = EbingoDialog.newInstance(getActivity(), EbingoDialog.DialogStyle.STYLE_TO_PRIVILEGE);                        dialog.setMessage(response.getString("msg"));                        dialog.show();                    }                } catch (JSONException e) {                    e.printStackTrace();                }            }            @Override            public void onFail(int statusCode, String msg) {            }            @Override            public void onFinish() {            }        });    }    class PublishContentAdapter extends FragmentPagerAdapter {        private List<Fragment> fragments;        public PublishContentAdapter(FragmentManager fm) {            super(fm);            fragments = new ArrayList<Fragment>();        }        public PublishContentAdapter add(Fragment f) {            fragments.add(f);            return this;        }        @Override        public CharSequence getPageTitle(int position) {            return position == 0 ? getString(R.string.publish_demand) : getString(R.string.publish_supply);        }        @Override        public int getCount() {            return fragments.size();        }        @Override        public Fragment getItem(int i) {            return fragments.get(i);        }    }    @Override    public void onActivityResult(int requestCode, int resultCode, Intent data) {        LogCat.i("--->", Integer.toBinaryString(requestCode));        if (isPickFor(PICK_FOR_DEMAND, requestCode))            publishDemand.onActivityResult(requestCode, resultCode, data);        //publishSupply能自动接收到onActivityResult回调，可能与add的顺序有关。        if (isPickFor(PICK_FOR_SUPPLY, requestCode))            publishSupply.onActivityResult(requestCode, resultCode, data);    }    /**     * 判断是否为Publish页面发出的请求     */    public boolean isMyRequest(final int requestCode) {        return isPickFor(PICK_FOR_DEMAND, requestCode) || isPickFor(PICK_FOR_SUPPLY, requestCode);    }    public boolean isPickFor(int code, final int requestCode) {        return (requestCode & code) != 0;    }    public static class PublishController {        public final int CATEGORY_EMPTY = 1;        public final int REGION_EMPTY = 2;        public final int IMAGE_EMPTY = 3;        public final int DESCRIPTION_EMPTY = 4;        public final int TAGS_EMPTY = 5;        public final int TITLE_EMPTY = 6;        public final int BUY_NUM_EMPTY = 7;        public final int CONTACT_EMPTY = 8;        public final int PHONE_EMPTY = 9;        public final int PRICE_EMPTY = 10;        public final int MIN_SELL_NUM_EMPTY = 11;        public final int TITLE_LENGTH_ERROR = 12;        public final int DESCRIPTION_LENGTH_ERROR = 13;        public final int CONTACT_LENGTH_ERROR = 14;        public final int PHONE_FORMAT_ERROR = 15;        public final int NULL_UNIT = 16;        public final int TITLE_LENGTH = 17;        public final int NOT_LOGIN = 100;        /**         * 公共的属性         */        public String type;        public Integer company_id;        public Integer category_id;        public String description;        public String title;        public String contacts;        public String contacts_phone;        public String unit;        /**         * 求购才有的属性         */        public String tags;        public String buy_num;        /**         * 供应才有的属性         */        public String region_name;        public String price;        public String image_url;        public String min_sell_num;        public boolean apply_3d;        private Error error = new Error();        private EbingoRequestParmater params;        /**         * 检查基本信息         *         * @return         */        private int checkBase() {            if (company_id == null) {                ContextUtil.toast("请重新登录！");                return NOT_LOGIN;            }            if (category_id == null) {                return CATEGORY_EMPTY;            }            if (isEmpty(title)) {                return TITLE_EMPTY;            }            if (title.length() > 15) {                return TITLE_LENGTH;            }            if (isEmpty(description)) {                return DESCRIPTION_EMPTY;            }            if (description.length() < 10) {                return DESCRIPTION_LENGTH_ERROR;            }            if (isEmpty(unit)) {                return NULL_UNIT;            }            if (isEmpty(contacts)) {                return CONTACT_EMPTY;            }            if (isEmpty(contacts_phone)) {                return PHONE_EMPTY;            }            if (!LoginManager.isMobile(contacts_phone) && !LoginManager.isPhone(contacts_phone)) {                return PHONE_FORMAT_ERROR;            }            return 0;        }        /**         * 检查求购信息         *         * @return         */        public int checkDemand() {            int base = checkBase();            if (base != 0) return base;            if (isEmpty(tags)) {                return TAGS_EMPTY;            }            if (isEmpty(buy_num)) {                return BUY_NUM_EMPTY;            }            return 0;        }        /**         * 检查供应信息         *         * @return         */        public int checkSupply() {            int base = checkBase();            if (base != 0) return base;            if (isEmpty(region_name)) {                return REGION_EMPTY;            }            if (isEmpty(price)) {                return PRICE_EMPTY;            }            if (isEmpty(image_url)) {                return IMAGE_EMPTY;            }            if (isEmpty(min_sell_num)) {                return MIN_SELL_NUM_EMPTY;            }            return 0;        }        private boolean isEmpty(CharSequence str) {            return str == null || "".equals(str);        }        public EbingoRequestParmater getDemandParams(Context context) {            EbingoRequestParmater parmater = new EbingoRequestParmater(context);            parmater.put("type", TYPE_SUPPLY);            parmater.put("company_id", company_id);            parmater.put("category_id", category_id);            parmater.put("region_name", region_name);            parmater.put("price", price);            parmater.put("image_url", image_url);            parmater.put("description", description);            parmater.put("title", title);            parmater.put("min_sell_num", min_sell_num);            parmater.put("contacts", contacts);            parmater.put("contacts_phone", contacts_phone);            parmater.put("unit", unit);            return parmater;        }        public EbingoRequestParmater getSupplyParams(Context context) {            EbingoRequestParmater parmater = new EbingoRequestParmater(context);            parmater.put("type", TYPE_SUPPLY);            parmater.put("company_id", company_id);            parmater.put("category_id", category_id);            parmater.put("region_name", region_name);            parmater.put("price", price);            parmater.put("image_url", image_url);            parmater.put("description", description);            parmater.put("title", title);            parmater.put("min_sell_num", min_sell_num);            parmater.put("contacts", contacts);            parmater.put("contacts_phone", contacts_phone);            parmater.put("unit", unit);            if (apply_3d) parmater.put("apply_3d", 1);            else parmater.put("apply_3d", 0);            return parmater;        }        public void showError(int code) {            ContextUtil.toast(error.get(code));        }        public class Error {            private SparseArray<String> errorMap = new SparseArray<String>();            public Error() {                errorMap.put(CATEGORY_EMPTY, "请输入类别");                errorMap.put(REGION_EMPTY, "请输入区域");                errorMap.put(IMAGE_EMPTY, "请选择图片");                errorMap.put(DESCRIPTION_EMPTY, "请输入描述");                errorMap.put(DESCRIPTION_LENGTH_ERROR, "描述需要10字以上");                errorMap.put(TAGS_EMPTY, "请添加标签");                errorMap.put(TITLE_EMPTY, "请输入标题");                errorMap.put(TITLE_LENGTH_ERROR, "标题长度30字以下");                errorMap.put(BUY_NUM_EMPTY, "请输入求购数量");                errorMap.put(CONTACT_EMPTY, "请填写联系人姓名");                errorMap.put(CONTACT_LENGTH_ERROR, "联系人姓名为2-4字");                errorMap.put(PHONE_EMPTY, "请填写联系电话");                errorMap.put(PHONE_FORMAT_ERROR, "联系电话格式不正确");                errorMap.put(PRICE_EMPTY, "请输入价格");                errorMap.put(MIN_SELL_NUM_EMPTY, "请输入起订标准");                errorMap.put(NULL_UNIT, "单位不能为空");                errorMap.put(TITLE_LENGTH, "标题长度不能超过15字");            }            public String get(int code) {                return errorMap.get(code);            }        }    }}