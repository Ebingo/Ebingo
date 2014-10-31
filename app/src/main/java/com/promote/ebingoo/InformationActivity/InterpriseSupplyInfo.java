package com.promote.ebingoo.InformationActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jch.lib.util.ImageManager;
import com.jch.lib.view.PullToRefreshView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.promote.ebingoo.R;
import com.promote.ebingoo.bean.SearchSupplyBean;
import com.promote.ebingoo.impl.EbingoRequest;
import com.promote.ebingoo.util.ContextUtil;
import com.promote.ebingoo.util.LogCat;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * 企业详情的供应列表.
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link InterpriseSupplyInfo#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InterpriseSupplyInfo extends CommonListFragment implements AdapterView.OnItemClickListener, PullToRefreshView.OnFooterRefreshListener {
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
    private PullToRefreshView itprsupplypulltorefresh;

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
        mOptions = ContextUtil.getSquareImgOptions();
        myAdapter = new MyAdapter();
        getData(0);
    }


    private void getData(int lastId) {
        LogCat.d("--->","enterprise_id="+enterprise_id);
        EbingoRequest.getSupplyInfoList(getActivity(), lastId, enterprise_id, 20, new MyRequest(lastId));  //网络请求.
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        setAddMaxNum(20);
        setOnItemClickListener(this);
        setDownRefreshAble(false);
        setUpRefreshAble(true);
        setAdapter(myAdapter);

        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), ProductInfoActivity.class);
        intent.putExtra(ProductInfoActivity.ARG_ID, mSearchSupplys.get(position).getId());
        startActivity(intent);
    }

    @Override
    public void onFooterRefresh(PullToRefreshView view) {
        getData(mSearchSupplys.size());
    }

    /**
     * 请求网络.
     */
    private class MyRequest implements EbingoRequest.RequestCallBack<ArrayList<SearchSupplyBean>> {

        int lastId = 0;

        public MyRequest(int lastId) {
            this.lastId = lastId;
        }

        @Override
        public void onFaild(int resultCode, String msg) {
            if (itprsupplypulltorefresh != null)
                itprsupplypulltorefresh.onFooterRefreshComplete();
        }

        @Override
        public void onSuccess(ArrayList<SearchSupplyBean> resultObj) {


            if (resultObj != null) {
                int loadNum = resultObj.size();
                if (lastId != 0) {      //刷新家在更多.
                    if (loadNum < getAddMaxeNum()) {        //加载到最后一页
                        loadMore(false, true);
                        haseData();
                    } else {
                        loadMore(true, true);
                        haseData();
                    }
                } else {     //首次加载
                    if (loadNum == 0) {
                        noData();
                        loadMore(false, false);
                    } else if (loadNum > 0 && loadNum < getAddMaxeNum()) {      //首次只加载不到一页的内容。
                        loadMore(false, false);
                        haseData();
                    } else {
                        loadMore(true, false);
                    }

                }
                mSearchSupplys.addAll(resultObj);
                myAdapter.notifyDataSetChanged();
            } else {
                if (lastId == 0) {
                    noData();
                }
                loadMore(false, false);

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

                convertView.setTag(viewHolder);
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


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }


}
