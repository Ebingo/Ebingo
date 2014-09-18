package com.promote.ebingo.InformationActivity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jch.lib.util.DialogUtil;
import com.jch.lib.util.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.promote.ebingo.R;
import com.promote.ebingo.application.HttpConstant;
import com.promote.ebingo.bean.ComanyNewsListBeanTools;
import com.promote.ebingo.impl.EbingoRequestParmater;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Use the {@link InterpriseDynamicFragment#newInstance} factory method to
 * create an instance of this fragment.
 * <p/>
 * 企業動態。
 */
public class InterpriseDynamicFragment extends InterpriseBaseFragment implements AdapterView.OnItemClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ListView ipriseinfodynamiclv;
    private ArrayList<ComanyNewsListBeanTools.CompanyNewListBean> mCompanyNewListBeans = new ArrayList<ComanyNewsListBeanTools.CompanyNewListBean>();
    private MyAdapter myAdapter = null;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment InterpriseDynamic.
     */
    // TODO: Rename and change types and number of parameters
    public static InterpriseDynamicFragment newInstance(String param1) {
        InterpriseDynamicFragment fragment = new InterpriseDynamicFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public InterpriseDynamicFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
        getCompanyNewsList();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_interprise_dynamic, container, false);
        initialize(view);
        return view;
    }

    private void initialize(View view) {

        ipriseinfodynamiclv = (ListView) view.findViewById(R.id.iprise_info_dynamic_lv);
        myAdapter = new MyAdapter();
        ipriseinfodynamiclv.setAdapter(myAdapter);
        ipriseinfodynamiclv.setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        ComanyNewsListBeanTools.CompanyNewListBean companyNewListBean = mCompanyNewListBeans.get(position);
        Intent intent = new Intent(getActivity(), InterpriseNewActivity.class);
        intent.putExtra(InterpriseNewActivity.ARG_ID, companyNewListBean.getId());
        startActivity(intent);

    }

    /**
     * list adapter.
     */
    private class MyAdapter extends BaseAdapter {


        private TextView ipriseinfodynamicdatetv;
        private TextView ipriseinfodyanmicnametv;
        private TextView ipriseinfodynamiccontenttv;

        @Override
        public int getCount() {
            return mCompanyNewListBeans.size();
        }

        @Override
        public Object getItem(int position) {
            return mCompanyNewListBeans.get(position);
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

                viewHolder.nameTv = (TextView) convertView.findViewById(R.id.iprise_info_dyanmic_name_tv);
                viewHolder.discTv = (TextView) convertView.findViewById(R.id.iprise_info_dynamic_content_tv);
                viewHolder.timeTv = (TextView) convertView.findViewById(R.id.iprise_info_dynamic_date_tv);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            ComanyNewsListBeanTools.CompanyNewListBean companyNewListBean = mCompanyNewListBeans.get(position);
            viewHolder.nameTv.setText(companyNewListBean.getTitle());
            viewHolder.timeTv.setText(companyNewListBean.getCreate_time());
            viewHolder.discTv.setText(companyNewListBean.getDescription());

            return convertView;
        }

    }

    private static class ViewHolder {

        TextView nameTv;
        TextView timeTv;
        TextView discTv;

    }

    /**
     * 获取网络数据。
     */
    private void getCompanyNewsList() {

        String urlStr = HttpConstant.getCompanyNewsList;

        EbingoRequestParmater parmater = new EbingoRequestParmater(getActivity().getApplicationContext());
        final ProgressDialog dialog = DialogUtil.waitingDialog(getActivity());

        HttpUtil.post(urlStr, parmater, new JsonHttpResponseHandler("utf-8") {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                ArrayList<ComanyNewsListBeanTools.CompanyNewListBean> companyNewListBeans = ComanyNewsListBeanTools.getCompanyNewsListBeans(response.toString());
                if (companyNewListBeans != null) {
                    mCompanyNewListBeans.addAll(companyNewListBeans);
                    myAdapter.notifyDataSetChanged();
                }
                dialog.dismiss();
                super.onSuccess(statusCode, headers, response);
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

}
