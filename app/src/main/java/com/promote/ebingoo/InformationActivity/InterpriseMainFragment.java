package com.promote.ebingoo.InformationActivity;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jch.lib.util.DialogUtil;
import com.jch.lib.util.HttpUtil;
import com.jch.lib.util.ImageManager;
import com.jch.lib.view.PagerScrollView;
import com.jch.lib.view.ScrollListView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.promote.ebingoo.R;
import com.promote.ebingoo.application.HttpConstant;
import com.promote.ebingoo.bean.Company;
import com.promote.ebingoo.bean.CurrentSupplyBean;
import com.promote.ebingoo.bean.InterpriseInfoBean;
import com.promote.ebingoo.bean.InterpriseInfoBeanTools;
import com.promote.ebingoo.impl.EbingoRequestParmater;
import com.promote.ebingoo.publish.VipType;
import com.promote.ebingoo.util.ContextUtil;
import com.promote.ebingoo.util.Dimension;
import com.promote.ebingoo.util.LogCat;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Use the {@link InterpriseMainFragment#newInstance} factory method to
 * create an instance of this fragment.
 * 公司首页。
 */
public class InterpriseMainFragment extends Fragment implements AdapterView.OnItemClickListener, InterpriseBaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private ImageView interprismainimg;
    private ImageView e_plat;
    private TextView fraginterprisemainnametv;
    private TextView fraginterprisemainaddrtv;
    private TextView fraginterprisemainhttpaddrtv;
    private TextView fraginterprisemaintelltv;
    private TextView fraginterprisemainrangetv;
    private TextView fraginterprisemainabstracttv;
    private ScrollListView interpisemainsupdemlv;
    private View website_layout;
    private View address_layout;
    private View tel_layout;
    private ArrayList<CurrentSupplyBean> currentSupplyBeans = new ArrayList<CurrentSupplyBean>();
    private MyAdapter myAdapter = null;
    private InterpriseInfoBean mInterpriseInfoBean = null;
    private boolean mScrollAble = true;     //viewpager 防止滑动时重绘view

    private OnFragmentInteractionListener mListener;
    private int companyId = -1;

    private DisplayImageOptions mOptions;
    private PagerScrollView enterpriseinfopsv;
    private int[] e_plat_drawable = new int[]{R.drawable.e_plat_disabled, R.drawable.e_plat};

    public InterpriseMainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment InterpriseMainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InterpriseMainFragment newInstance(String param1) {
        InterpriseMainFragment fragment = new InterpriseMainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 使用DisplayImageOptions.Builder()创建DisplayImageOptions
        mOptions = ContextUtil.getSquareImgOptions();
        getDataInfo();
        mScrollAble = true;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View containerView = inflater.inflate(R.layout.fragment_interprise_main, container, false);


        initialize(containerView);

        return containerView;
    }


    private void initialize(View containerView) {

        interprismainimg = (ImageView) containerView.findViewById(R.id.interpris_main_img);
        e_plat = (ImageView) containerView.findViewById(R.id.e_plat);
        fraginterprisemainnametv = (TextView) containerView.findViewById(R.id.frag_interprise_main_name_tv);
        fraginterprisemainaddrtv = (TextView) containerView.findViewById(R.id.frag_interprise_main_addr_tv);
        fraginterprisemainhttpaddrtv = (TextView) containerView.findViewById(R.id.frag_interprise_main_httpaddr_tv);
        fraginterprisemaintelltv = (TextView) containerView.findViewById(R.id.frag_interprise_main_tell_tv);
        fraginterprisemainrangetv = (TextView) containerView.findViewById(R.id.frag_interprise_main_range_tv);
        fraginterprisemainabstracttv = (TextView) containerView.findViewById(R.id.frag_interprise_main_abstract_tv);
        interpisemainsupdemlv = (ScrollListView) containerView.findViewById(R.id.interpise_main_sup_dem_lv);
        enterpriseinfopsv = (PagerScrollView) containerView.findViewById(R.id.enterprise_info_psv);
        website_layout = containerView.findViewById(R.id.website_layout);
        tel_layout = containerView.findViewById(R.id.tel_layout);
        address_layout = containerView.findViewById(R.id.address_layout);
        enterpriseinfopsv.smoothScrollTo(0, 0);
        myAdapter = new MyAdapter();
        interpisemainsupdemlv.setAdapter(myAdapter);
        interpisemainsupdemlv.setOnItemClickListener(this);
        interpisemainsupdemlv.setEmptyView(containerView.findViewById(R.id.empty));
        if (mInterpriseInfoBean != null) {    //只有当首次加载界面时加载listView。
            initData(mInterpriseInfoBean);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        enterpriseinfopsv.smoothScrollTo(0, 0);
    }

    /**
     * 计算公司图片的size.
     */
    private void reSizeImage() {

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    /**
     * 获取企业首页信息
     */
    private void getDataInfo() {

        String urlStr = HttpConstant.getCompanyDetail;

        EbingoRequestParmater parmater = new EbingoRequestParmater(getActivity().getApplicationContext());
        parmater.put("company_id", getInterprsetId());
        Integer login_id = Company.getInstance().getCompanyId();
        parmater.put("login_id", login_id == null ? 0 : login_id.intValue());
        LogCat.d("--->MainFragment id==" + getInterprsetId());
        final ProgressDialog dialog = DialogUtil.waitingDialog(getActivity());

        HttpUtil.post(urlStr, parmater, new JsonHttpResponseHandler("utf-8") {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                mInterpriseInfoBean = InterpriseInfoBeanTools.getInterpriseInfo(response.toString());
                if (mInterpriseInfoBean != null) {
                    initData(mInterpriseInfoBean);
                    mListener.onFragmentInteraction(mInterpriseInfoBean.getName());
                }

                dialog.dismiss();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

                dialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                dialog.dismiss();
            }
        });
    }


    private void initData(final InterpriseInfoBean infoBean) {

        ImageManager.load(infoBean.getImage(), interprismainimg, mOptions);
        fraginterprisemainnametv.setText(infoBean.getName());
        fraginterprisemainnametv.setText(getTitle(infoBean.getName(), infoBean.getViptype()));

        setText(address_layout, fraginterprisemainaddrtv, infoBean.getAddr());
        setText(website_layout, fraginterprisemainhttpaddrtv, infoBean.getWebsite());
        setText(tel_layout, fraginterprisemaintelltv, infoBean.getTel());

        fraginterprisemainabstracttv.setText(infoBean.getIntroduction());
        fraginterprisemainrangetv.setText(infoBean.getMainRun());
        if (infoBean.getInfoarray() != null) {
            currentSupplyBeans.clear();
            currentSupplyBeans.addAll(infoBean.getInfoarray());
        }
        myAdapter.notifyDataSetChanged();
        enterpriseinfopsv.smoothScrollTo(0, 0);
        final String e_url = infoBean.getE_url();
        if (TextUtils.isEmpty(e_url)) {
            e_plat.setImageResource(e_plat_drawable[0]);
        } else {
            e_plat.setImageResource(e_plat_drawable[1]);
        }
        e_plat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(e_url)) {
                    ContextUtil.toast("该企业还没有开通e平台。");
                } else {
                    Intent intent = new Intent().setClass(getActivity(), CodeScanOnlineActivity.class);
                    intent.putExtra(CodeScanOnlineActivity.URLSTR, infoBean.getE_url());
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * 如果text为空就隐藏container，否则就用tv展示text
     */
    private void setText(View container, TextView tv, CharSequence text) {
        if (TextUtils.isEmpty(text)) {
            container.setVisibility(View.GONE);
        } else {
            tv.setText(text);
        }
    }

    private android.text.Spanned getTitle(String companyName, int vipType) {

        StringBuilder html = new StringBuilder();
        html.append("<span style = \"font-family:arial;color:black;font-weight:bolder;")
                .append("font-size:").append(Dimension.sp(14))
                .append("px\">")
                .append(companyName)
                .append("<img src=" + vipType + ">")
                .append("</span>");


        return Html.fromHtml(html.toString(),
                new Html.ImageGetter() {
                    @Override
                    public Drawable getDrawable(String source) {
                        VipType vipType = VipType.parse(source);
                        Drawable icon = vipType.getIcon(getActivity());
                        if (icon != null)
                            icon.setBounds(0, 0, (int) Dimension.dp(14), (int) Dimension.dp(19));
                        return icon;
                    }
                }, null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        CurrentSupplyBean currentSupply = currentSupplyBeans.get(position);
        Intent intent = null;
        if (currentSupply.getType() == 2) {
            intent = new Intent(getActivity(), ProductInfoActivity.class);
            intent.putExtra(ProductInfoActivity.ARG_ID, currentSupply.getId());
        } else {
            intent = new Intent(getActivity(), BuyInfoActivity.class);
            intent.putExtra(BuyInfoActivity.DEMAND_ID, currentSupply.getId());
        }

        startActivity(intent);
    }

    @Override
    public int getInterprsetId() {
        return companyId;
    }

    @Override
    public void setInterprsetId(int interprsetId) {
        companyId = interprsetId;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String name);
    }

    private static class ViewHolder {
        ImageView img;
        TextView describe;
        TextView time;
    }

    /**
     * BaseAdapter.
     */
    private class MyAdapter extends BaseAdapter {


        private DisplayImageOptions options = null;

        public MyAdapter() {
            options = new DisplayImageOptions.Builder()
                    .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                    .showImageForEmptyUri(R.drawable.loading)
                    .showImageOnLoading(R.drawable.loading)
                    .showImageOnFail(R.drawable.loading)
                    .cacheInMemory(true).cacheOnDisc(true).build();
        }

        @Override
        public int getCount() {
            return currentSupplyBeans.size();
        }

        @Override
        public Object getItem(int position) {
            return currentSupplyBeans.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder = null;
            if (convertView == null) {

                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.interprise_cur_item_layout, null);
                viewHolder.img = (ImageView) convertView.findViewById(R.id.interprise_cur_item_img);
                viewHolder.time = (TextView) convertView.findViewById(R.id.interprise_cur_item_time_tv);
                viewHolder.describe = (TextView) convertView.findViewById(R.id.interprise_cur_item_describe_tv);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            CurrentSupplyBean currentSupplyBean1 = currentSupplyBeans.get(position);
            if (currentSupplyBean1.getType() == 2) {        // 供应.
                viewHolder.img.setBackgroundResource(R.drawable.cur_supply);
            } else if (currentSupplyBean1.getType() == 1) {     //求购.
                viewHolder.img.setBackgroundResource(R.drawable.cur_demaind);
            }

            viewHolder.describe.setText(currentSupplyBean1.getName());
            viewHolder.time.setText(currentSupplyBean1.getTime());

            return convertView;
        }

    }


}
