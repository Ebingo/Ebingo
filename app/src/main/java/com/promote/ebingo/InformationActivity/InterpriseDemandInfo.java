package com.promote.ebingo.InformationActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.promote.ebingo.R;
import com.promote.ebingo.bean.SearchDemandBean;
import com.promote.ebingo.impl.EbingoRequest;
import com.promote.ebingo.util.LogCat;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * 企业详情的求购列表.
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * to handle interaction events.
 * Use the {@link com.promote.ebingo.InformationActivity.InterpriseDemandInfo#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InterpriseDemandInfo extends Fragment implements AdapterView.OnItemClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private int enterprise_id = -1;
    private String mParam2;
    private ListView entprdemandlv;
    private ArrayList<SearchDemandBean> mSearchDemands = new ArrayList<SearchDemandBean>();
    private MyAdapter adapter = null;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InterpriseDemandInfo.
     */
    // TODO: Rename and change types and number of parameters
    public static InterpriseDemandInfo newInstance(int param1, String param2) {
        InterpriseDemandInfo fragment = new InterpriseDemandInfo();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LogCat.d("demand info");
        if (getArguments() != null) {
            enterprise_id = getArguments().getInt(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        adapter = new MyAdapter();

        EbingoRequest.getDemandInfoList(getActivity(), 0, enterprise_id, 20, new EbingoRequest.RequestCallBack<ArrayList<SearchDemandBean>>() {
            @Override
            public void onFaild(int resultCode, String msg) {

            }

            @Override
            public void onSuccess(ArrayList<SearchDemandBean> resultObj) {

                if (resultObj != null) {
                    mSearchDemands.addAll(resultObj);
                }

                if (adapter != null)
                    adapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_interprise_demand_info, container, false);

        initialize(view);

        return view;
    }


    /**
     * 初始化view。
     *
     * @param view
     */
    private void initialize(View view) {

        entprdemandlv = (ListView) view.findViewById(R.id.entpr_demand_lv);
        entprdemandlv.setAdapter(adapter);

        entprdemandlv.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent intent = new Intent(getActivity(), BuyInfoActivity.class);
        intent.putExtra(BuyInfoActivity.DEMAND_ID, mSearchDemands.get(position).getId());
        startActivity(intent);
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mSearchDemands.size();
        }

        @Override
        public Object getItem(int position) {
            return mSearchDemands.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder = null;
            if (convertView == null) {

                convertView = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.iprise_info_dynamic_item, null);
                viewHolder = new ViewHolder();

                viewHolder.timeTv = (TextView) convertView.findViewById(R.id.iprise_info_dynamic_date_tv);
                viewHolder.nameTv = (TextView) convertView.findViewById(R.id.iprise_info_dyanmic_name_tv);
                viewHolder.describTv = (TextView) convertView.findViewById(R.id.iprise_info_dynamic_content_tv);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            SearchDemandBean searchDemandBean = mSearchDemands.get(position);
            viewHolder.nameTv.setText(searchDemandBean.getName());
            viewHolder.timeTv.setText(searchDemandBean.getDate());
            viewHolder.describTv.setText(searchDemandBean.getIntroduction());

            return convertView;
        }

    }


    private static class ViewHolder {
        TextView nameTv;
        TextView timeTv;
        TextView describTv;

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
