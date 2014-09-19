package com.promote.ebingo.InformationActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jch.lib.util.ImageManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.promote.ebingo.R;
import com.promote.ebingo.bean.SearchSupplyBean;
import com.promote.ebingo.impl.EbingoRequest;
import com.promote.ebingo.util.LogCat;

import java.util.ArrayList;

/**
 * 企业详情的供应列表.
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link com.promote.ebingo.InformationActivity.InterpriseSupplyInfo#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InterpriseSupplyInfo extends InterpriseBaseFragment implements AdapterView.OnItemClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private int enterprise_id;
    private String mParam2;

    private ArrayList<SearchSupplyBean> mSearchSupplys = new ArrayList<SearchSupplyBean>();
    private ListView enterprisesupplyitemlv;
    private MyAdapter myAdapter = null;
    private DisplayImageOptions mOptions;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InterpriseSupplyInfo.
     */
    // TODO: Rename and change types and number of parameters
    public static InterpriseSupplyInfo newInstance(int param1, String param2) {
        InterpriseSupplyInfo fragment = new InterpriseSupplyInfo();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogCat.d("supply info");
        if (getArguments() != null) {
            enterprise_id = getArguments().getInt(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mOptions = new DisplayImageOptions.Builder()
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .showImageForEmptyUri(R.drawable.loading)
                .showImageOnLoading(R.drawable.loading)
                .showImageOnFail(R.drawable.loading)
                .cacheInMemory(true).cacheOnDisc(true).build();
        EbingoRequest.getSupplyInfoList(getActivity(), 0, getInterprsetId(), 20, new MyRequest());  //网络请求.
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_interprise_supply_info, container, false);

        initialize(view);

        return view;
    }

    private void initialize(View view) {

        enterprisesupplyitemlv = (ListView) view.findViewById(R.id.enterprise_supply_item_lv);
        myAdapter = new MyAdapter();
        enterprisesupplyitemlv.setAdapter(myAdapter);
        enterprisesupplyitemlv.setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), ProductInfoActivity.class);
        intent.putExtra(ProductInfoActivity.ARG_ID, mSearchSupplys.get(position).getId());
        startActivity(intent);
    }

    /**
     * 请求网络.
     */
    private class MyRequest implements EbingoRequest.RequestCallBack<ArrayList<SearchSupplyBean>> {

        @Override
        public void onFaild(int resultCode, String msg) {

        }

        @Override
        public void onSuccess(ArrayList<SearchSupplyBean> resultObj) {
            if (resultObj != null) {
                mSearchSupplys.addAll(resultObj);
                myAdapter.notifyDataSetChanged();
            }
        }
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mSearchSupplys.size();
        }

        @Override
        public Object getItem(int position) {
            return mSearchSupplys.get(position);
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
                convertView = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.iprise_info_s_item_layout, null);

                viewHolder.img = (ImageView) convertView.findViewById(R.id.iprise_info_s_item_img);
                viewHolder.timeTv = (TextView) convertView.findViewById(R.id.iprise_info_s_item_time);
                viewHolder.nameTv = (TextView) convertView.findViewById(R.id.iprise_info_s_item_black_name_tv);
                viewHolder.priceTv = (TextView) convertView.findViewById(R.id.iprise_info_s_item_price_tv);
                viewHolder.lookTimeTv = (TextView) convertView.findViewById(R.id.iprise_info_s_item_item_look_num_tvs);
                viewHolder.startTv = (TextView) convertView.findViewById(R.id.iprise_info_s_item_num);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            SearchSupplyBean supplyBean = mSearchSupplys.get(position);

            ImageManager.load(supplyBean.getImage(), viewHolder.img, mOptions);
            viewHolder.nameTv.setText(supplyBean.getName());
            viewHolder.timeTv.setText(supplyBean.getDate());
            viewHolder.priceTv.setText(supplyBean.getPrice());
            viewHolder.lookTimeTv.setText(String.valueOf(supplyBean.getRead_num()));
            viewHolder.startTv.setText(supplyBean.getMin_supply_num());


            return convertView;
        }

    }

    private static class ViewHolder {
        ImageView img;
        TextView nameTv;
        TextView priceTv;
        TextView lookTimeTv;
        TextView startTv;
        TextView timeTv;
    }

}
