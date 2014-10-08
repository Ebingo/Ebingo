package com.promote.ebingo.center;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jch.lib.util.DialogUtil;
import com.jch.lib.util.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.promote.ebingo.BaseListActivity;
import com.promote.ebingo.InformationActivity.BuyInfoActivity;
import com.promote.ebingo.R;
import com.promote.ebingo.application.HttpConstant;
import com.promote.ebingo.bean.Company;
import com.promote.ebingo.bean.SearchDemandBean;
import com.promote.ebingo.bean.SearchDemandBeanTools;
import com.promote.ebingo.impl.EbingoRequestParmater;
import com.promote.ebingo.util.FileUtil;
import com.promote.ebingo.util.LogCat;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MyDemandActivity extends BaseListActivity implements View.OnClickListener {

    private ArrayList<SearchDemandBean> mDemandBeans = new ArrayList<SearchDemandBean>();
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
    }

    private void initialize() {
        setUpRefreshable(true);
        adapter = new MyAdapter();
        setListAdapter(adapter);
        getListView().setOnItemLongClickListener(this);
        enableDelete(true);
        setDownRefreshable(true);
        enableCache(FileUtil.FILE_DEMAND_LIST, mDemandBeans);
        if (mDemandBeans.size() == 0||getIntent().getBooleanExtra(ARG_REFRESH,false)) onRefresh();
    }

    private void getMyDemandList(int lastId) {

        String urlStr = HttpConstant.getDemandInfoList;
        final EbingoRequestParmater parma = new EbingoRequestParmater(getApplicationContext());
        final ProgressDialog dialog = new ProgressDialog(MyDemandActivity.this);
        parma.put("lastid", lastId);
        parma.put("pagesize", pageSize);
        parma.put("condition", getCondition());
        LogCat.i("--->", parma.toString());
        HttpUtil.post(urlStr, parma, new JsonHttpResponseHandler("utf-8") {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                LogCat.i("--->", response.toString());
                ArrayList<SearchDemandBean> demandBeans = SearchDemandBeanTools.getSearchDemands(response.toString());
                if (demandBeans != null && demandBeans.size() > 0) {
                    mDemandBeans.addAll(demandBeans);
                    adapter.notifyDataSetChanged();
                }
                onLoadFinish();
                dialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                dialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                dialog.dismiss();
            }
        });
    }

    /**
     * 创建筛选条件
     * @return
     */
    private String getCondition() {
        StringBuilder sb = new StringBuilder();
        sb.append("{")
                .append(makeCondition("company_id", Company.getInstance().getCompanyId()))
                .append(",")
                .append(makeCondition("sort", "time"))
                .append(",")
                .append(makeCondition("verify",3))
                .append("}");

        try {
            return URLEncoder.encode(sb.toString(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = new Intent(MyDemandActivity.this, BuyInfoActivity.class);
        intent.putExtra(BuyInfoActivity.DEMAND_ID, mDemandBeans.get(position).getId());
        startActivity(intent);
    }


    @Override
    protected CharSequence onPrepareDelete(int position) {
        return mDemandBeans.get(position).getName();
    }

    @Override
    protected void onDelete(final int position) {
        String urlStr = HttpConstant.deleteInfo;
        final ProgressDialog dialog = DialogUtil.waitingDialog(MyDemandActivity.this);
        EbingoRequestParmater param = new EbingoRequestParmater(getApplicationContext());

        param.put("infoid", mDemandBeans.get(position).getId());
        param.put("company_id", Company.getInstance().getCompanyId());

        HttpUtil.post(urlStr, param, new JsonHttpResponseHandler("utf-8") {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                mDemandBeans.remove(position);
                adapter.notifyDataSetChanged();
                dialog.dismiss();
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

                dialog.dismiss();
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                dialog.dismiss();
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }

    private class MyAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return mDemandBeans.size();
        }

        @Override
        public Object getItem(int position) {
            return mDemandBeans.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.mydemand_item_layout, null);
                viewHolder = new ViewHolder();
                viewHolder.nameTv = (TextView) convertView.findViewById(R.id.mydemand_name_tv);
                viewHolder.lookTv = (TextView) convertView.findViewById(R.id.mydemand_look_num_tv);
                viewHolder.timeTv = (TextView) convertView.findViewById(R.id.mydemand_time_tv);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            SearchDemandBean demandBean = mDemandBeans.get(position);
            viewHolder.nameTv.setText(demandBean.getName());
            viewHolder.lookTv.setText(demandBean.getRead_num());
            viewHolder.timeTv.setText(demandBean.getDate());

            return convertView;
        }

    }

    private static class ViewHolder {

        TextView nameTv;
        TextView lookTv;
        TextView timeTv;
    }

    @Override
    protected void onLoadMore(int lastId) {
        getMyDemandList(lastId);
    }

    @Override
    protected void onRefresh() {
        mDemandBeans.clear();
        getMyDemandList(0);
    }
}
