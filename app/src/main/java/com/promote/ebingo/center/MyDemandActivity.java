package com.promote.ebingo.center;

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

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MyDemandActivity extends BaseListActivity implements View.OnClickListener, AdapterView.OnItemLongClickListener, ItemDelteDialog.DeleteItemListener {

    private ArrayList<SearchDemandBean> mDemandBeans = new ArrayList<SearchDemandBean>();
    private MyAdapter adapter;
    private ItemDelteDialog mItemDeleteDialog = null;
    /**
     * 要删除的求购信息.
     */
    private SearchDemandBean mDelDemandBean = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
    }

    private void initialize() {

        adapter = new MyAdapter();
        setListAdapter(adapter);
        getListView().setOnItemLongClickListener(this);
        mItemDeleteDialog = new ItemDelteDialog(this, this);
        getMyDemandList(0);
    }

    private void getMyDemandList(int lastId) {

        String urlStr = HttpConstant.getDemandInfoList;
        final EbingoRequestParmater parma = new EbingoRequestParmater(getApplicationContext());
        final ProgressDialog dialog = new ProgressDialog(MyDemandActivity.this);
        parma.put("lastid", lastId);
        parma.put("pagesize", 20);
        try {
            parma.put("condition", URLEncoder.encode(appendKeyworld(Company.getInstance().getCompanyId()), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpUtil.post(urlStr, parma, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                ArrayList<SearchDemandBean> demandBeans = SearchDemandBeanTools.getSearchDemands(response.toString());

                mDemandBeans.clear();
                mDemandBeans.addAll(demandBeans);
                adapter.notifyDataSetChanged();
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
     * 删除供求信息
     *
     * @param id 所要删除的求购信息id.
     */
    private void deleteInfo(int id) {

        String urlStr = HttpConstant.deleteInfo;
        final ProgressDialog dialog = DialogUtil.waitingDialog(MyDemandActivity.this);
        EbingoRequestParmater param = new EbingoRequestParmater(getApplicationContext());

        param.put("infoid", id);
        param.put("company_id", Company.getInstance().getCompanyId());

        HttpUtil.post(urlStr, param, new JsonHttpResponseHandler("utf-8") {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                mDemandBeans.remove(mDelDemandBean);
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

    /**
     * 拼接赛选条件参数。
     *
     * @param companyId
     * @return
     */
    private String appendKeyworld(int companyId) {
        StringBuffer sb = new StringBuffer("{\"company_id\":\"");
        sb.append(companyId);
        sb.append("\"}");
        return sb.toString();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = new Intent(MyDemandActivity.this, BuyInfoActivity.class);
        intent.putExtra(BuyInfoActivity.DEMAND_ID, mDemandBeans.get(position).getId());
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        mDelDemandBean = mDemandBeans.get(position);
        mItemDeleteDialog.show();
        mItemDeleteDialog.setItemText(mDelDemandBean.getName(), mDelDemandBean.getId());

        return true;
    }

    /**
     * 所删除条的id.
     *
     * @param view
     * @param itemId
     */
    @Override
    public void onItemDelete(View view, int itemId) {

        deleteInfo(itemId);
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


}
