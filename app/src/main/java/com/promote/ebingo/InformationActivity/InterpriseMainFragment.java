package com.promote.ebingo.InformationActivity;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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
import com.promote.ebingo.R;
import com.promote.ebingo.application.HttpConstant;
import com.promote.ebingo.bean.CurrentSupplyBean;
import com.promote.ebingo.bean.InterpriseInfoBean;
import com.promote.ebingo.bean.InterpriseInfoBeanTools;
import com.promote.ebingo.impl.EbingoRequestParmater;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Use the {@link com.promote.ebingo.InformationActivity.InterpriseMainFragment#newInstance} factory method to
 * create an instance of this fragment.
 * 公司首页。
 */
public class InterpriseMainFragment extends InterpriseBaseFragment implements AdapterView.OnItemClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private ImageView interprismainimg;
    private TextView fraginterprisemainnametv;
    private ImageView fraginterprisemainvipimg;
    private TextView fraginterprisemainaddrtv;
    private TextView fraginterprisemainhttpaddrtv;
    private TextView fraginterprisemaintelltv;
    private TextView fraginterprisemainrangetv;
    private TextView fraginterprisemainabstracttv;
    private ScrollListView interpisemainsupdemlv;
    private ArrayList<CurrentSupplyBean> currentSupplyBeans = new ArrayList<CurrentSupplyBean>();
    private MyAdapter myAdapter = null;

    private OnFragmentInteractionListener mListener;

    private DisplayImageOptions mOptions;
    private PagerScrollView enterpriseinfopsv;

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

    public InterpriseMainFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 使用DisplayImageOptions.Builder()创建DisplayImageOptions
        mOptions = new DisplayImageOptions.Builder()
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .showImageForEmptyUri(R.drawable.loading)
                .showImageOnLoading(R.drawable.loading)
                .showImageOnFail(R.drawable.loading)
                .cacheInMemory(true).cacheOnDisc(true).build();
        getDataInfo();

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
        fraginterprisemainnametv = (TextView) containerView.findViewById(R.id.frag_interprise_main_name_tv);
        fraginterprisemainvipimg = (ImageView) containerView.findViewById(R.id.frag_interprise_main_vip_img);
        fraginterprisemainaddrtv = (TextView) containerView.findViewById(R.id.frag_interprise_main_addr_tv);
        fraginterprisemainhttpaddrtv = (TextView) containerView.findViewById(R.id.frag_interprise_main_httpaddr_tv);
        fraginterprisemaintelltv = (TextView) containerView.findViewById(R.id.frag_interprise_main_tell_tv);
        fraginterprisemainrangetv = (TextView) containerView.findViewById(R.id.frag_interprise_main_range_tv);
        fraginterprisemainabstracttv = (TextView) containerView.findViewById(R.id.frag_interprise_main_abstract_tv);
        interpisemainsupdemlv = (ScrollListView) containerView.findViewById(R.id.interpise_main_sup_dem_lv);
        enterpriseinfopsv = (PagerScrollView) containerView.findViewById(R.id.enterprise_info_psv);
        myAdapter = new MyAdapter();
        interpisemainsupdemlv.setAdapter(myAdapter);
        interpisemainsupdemlv.setOnItemClickListener(this);
    }

    /**
     * 获取企业首页信息
     */
    private void getDataInfo() {

        String urlStr = HttpConstant.getCompanyDetail;

        EbingoRequestParmater parmater = new EbingoRequestParmater(getActivity().getApplicationContext());
        parmater.put("company_id", getInterprsetId());
        final ProgressDialog dialog = DialogUtil.waitingDialog(getActivity());

        HttpUtil.post(urlStr, parmater, new JsonHttpResponseHandler("utf-8") {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                InterpriseInfoBean interpriseInfoBean = InterpriseInfoBeanTools.getInterpriseInfo(response.toString());
                if (interpriseInfoBean != null) {
                    initData(interpriseInfoBean);
                    mListener.onFragmentInteraction(interpriseInfoBean.getName());
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


    private void initData(InterpriseInfoBean infoBean) {


        ImageManager.load(infoBean.getImage(), interprismainimg, mOptions);
        fraginterprisemainnametv.setText(infoBean.getName());
        fraginterprisemainaddrtv.setText(infoBean.getAddr());
        fraginterprisemainhttpaddrtv.setText(infoBean.getWebsite());
        fraginterprisemaintelltv.setText(infoBean.getTel());
        fraginterprisemainabstracttv.setText(infoBean.getIntroduction());
        fraginterprisemainrangetv.setText(infoBean.getMainRun());
        if (infoBean.getViptype() == 1) {
            fraginterprisemainvipimg.setVisibility(View.VISIBLE);
        } else {
            fraginterprisemainvipimg.setVisibility(View.GONE);
        }
        if (infoBean.getInfoarray() != null) {
            currentSupplyBeans.addAll(infoBean.getInfoarray());
        }
        myAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        CurrentSupplyBean currentSupply = currentSupplyBeans.get(position);
        Intent intent = null;
        if (currentSupply.getType() == 1) {
            intent = new Intent(getActivity(), ProductInfoActivity.class);
            intent.putExtra(ProductInfoActivity.ARG_ID, currentSupply.getId());
        } else {
            intent = new Intent(getActivity(), BuyInfoActivity.class);
            intent.putExtra(BuyInfoActivity.DEMAND_ID, currentSupply.getId());
        }

        startActivity(intent);
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
                viewHolder.img.setBackgroundResource(R.drawable.cur_demaind);
            } else if (currentSupplyBean1.getType() == 1) {     //求购.
                viewHolder.img.setBackgroundResource(R.drawable.cur_supply);
            }

            viewHolder.describe.setText(currentSupplyBean1.getName());
            viewHolder.time.setText(currentSupplyBean1.getTime());

            return convertView;
        }

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

    private static class ViewHolder {
        ImageView img;
        TextView describe;
        TextView time;
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


}
