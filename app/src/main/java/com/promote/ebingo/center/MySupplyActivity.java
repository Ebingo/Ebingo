package com.promote.ebingo.center;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jch.lib.util.HttpUtil;
import com.jch.lib.util.ImageManager;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.promote.ebingo.BaseListActivity;
import com.promote.ebingo.InformationActivity.ProductInfoActivity;
import com.promote.ebingo.R;
import com.promote.ebingo.application.Constant;
import com.promote.ebingo.application.HttpConstant;
import com.promote.ebingo.bean.Company;
import com.promote.ebingo.bean.SearchSupplyBean;
import com.promote.ebingo.bean.SearchSupplyBeanTools;
import com.promote.ebingo.impl.EbingoHandler;
import com.promote.ebingo.impl.EbingoRequestParmater;
import com.promote.ebingo.publish.PublishEditActivity;
import com.promote.ebingo.util.ContextUtil;
import com.promote.ebingo.util.FileUtil;
import com.promote.ebingo.util.FormatUtil;
import com.promote.ebingo.util.LogCat;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MySupplyActivity extends BaseListActivity {

    private final ArrayList<SearchSupplyBean> mSupplyBeans = new ArrayList<SearchSupplyBean>();
    private DisplayImageOptions mOptions;
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = new Intent(MySupplyActivity.this, ProductInfoActivity.class);
        intent.putExtra(ProductInfoActivity.ARG_ID, mSupplyBeans.get(position).getId());
        startActivity(intent);
    }

    private void initialize() {
        addEmptyView();
        setUpRefreshable(true);
        mOptions = ContextUtil.getSquareImgOptions();
        adapter = new MyAdapter();
        setListAdapter(adapter);

        enableDelete(true);
        enableCache(FileUtil.FILE_SUPPLY_LIST, mSupplyBeans);

        setDownRefreshable(true);
        setUpRefreshable(true);
        if (mSupplyBeans.size() == 0 || getIntent().getBooleanExtra(ARG_REFRESH, false))
            onRefresh();

    }

    /**
     * 添加一个emptyView,如果不添加则使用默认的
     */
    private void addEmptyView() {
        View empty_view = View.inflate(this, R.layout.empty_layout, null);
        TextView notice= (TextView) empty_view.findViewById(R.id.tv_empty_notice);
        notice.setText(getString(R.string.empty_notice,"供应"));
        empty_view.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MySupplyActivity.this, PublishEditActivity.class);
                intent.putExtra(PublishEditActivity.TYPE, Constant.PUBLISH_SUPPLY);
                startActivity(intent);
                finish();
            }
        });

        setEmptyView(empty_view);
    }

    private void delete(final int posotion) {
        EbingoRequestParmater param = new EbingoRequestParmater(this);
        param.put("company_id", Company.getInstance().getCompanyId());
        param.put("infoid", mSupplyBeans.get(posotion).getId());

        HttpUtil.post(HttpConstant.deleteInfo, param, new EbingoHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                mSupplyBeans.remove(posotion);
                adapter.notifyDataSetChanged();
                ContextUtil.toast("删除成功！");
                refreshFooterView(true);
            }

            @Override
            public void onFail(int statusCode, String msg) {
                ContextUtil.toast("删除失败！");
            }

            @Override
            public void onFinish() {

            }
        });
    }

    private void getMySupply(final int lastId) {
        String urlStr = HttpConstant.getSupplyInfoList;
        EbingoRequestParmater param = new EbingoRequestParmater(getApplicationContext());
        param.put("lastid", lastId);
        param.put("pagesize", pageSize);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("{")
                    .append(makeCondition("company_id", Company.getInstance().getCompanyId()))
                    .append(",")
                    .append(makeCondition("sort", "time"))
                    .append(",")
                    .append(makeCondition("verify", 3))
                    .append("}");
            param.put("condition", URLEncoder.encode(sb.toString(), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpUtil.post(urlStr, param, new JsonHttpResponseHandler("utf-8") {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                LogCat.i("--->", response.toString());
                ArrayList<SearchSupplyBean> searchSupplyBeans = SearchSupplyBeanTools.getSearchSupplyBeans(response.toString());
                if (searchSupplyBeans != null && searchSupplyBeans.size() > 0) {
                    if (lastId==0)mSupplyBeans.clear();
                    mSupplyBeans.addAll(searchSupplyBeans);
                    adapter.notifyDataSetChanged();
                }
                onLoadFinish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                LogCat.i("--->", errorResponse + "");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                LogCat.i("--->", responseString);
            }
        });

    }

    @Override
    protected CharSequence onPrepareDelete(int position) {
        return mSupplyBeans.get(position).getName();
    }

    @Override
    protected void onDelete(int position) {
        delete(position);
    }

    /**
     * listview adapter。
     */
    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mSupplyBeans.size();
        }

        @Override
        public Object getItem(int position) {
            return mSupplyBeans.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.mysupply_item, null);
                viewHolder = new ViewHolder();
                viewHolder.img = (ImageView) convertView.findViewById(R.id.mysupply_item_img);
                viewHolder.nameTv = (TextView) convertView.findViewById(R.id.mysupply_item_name);
                viewHolder.priceTv = (TextView) convertView.findViewById(R.id.mysupply_price_tv);
                viewHolder.startTv = (TextView) convertView.findViewById(R.id.mysupply_supply_num_tv);
                viewHolder.timeTv = (TextView) convertView.findViewById(R.id.mysupply_time);
                viewHolder.verifyTv = (TextView) convertView.findViewById(R.id.tv_verify_result);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            SearchSupplyBean supplyBean = mSupplyBeans.get(position);
            ImageManager.load(supplyBean.getImage(), viewHolder.img, mOptions);

            viewHolder.nameTv.setText(supplyBean.getName());
            viewHolder.priceTv.setText(FormatUtil.formatPrice(supplyBean.getPrice(),supplyBean.getUnit()));
            viewHolder.timeTv.setText(supplyBean.getDate());

            String minNum=FormatUtil.formatSellNum(supplyBean.getMin_supply_num(),supplyBean.getUnit());
            viewHolder.startTv.setText(getString(R.string.supply_start_num,minNum));

            String verify_result=supplyBean.getVerify_result();
            if (Constant.VERIFY_WAITING.equals(verify_result)){
                viewHolder.verifyTv.setVisibility(View.VISIBLE);
                viewHolder.verifyTv.setText("待审核");
                viewHolder.verifyTv.setTextColor(getResources().getColor(R.color.orange_my));
            }else if(Constant.VERIFY_NOT_PASS.equals(verify_result)){
                viewHolder.verifyTv.setVisibility(View.VISIBLE);
                viewHolder.verifyTv.setText("未通过");
                viewHolder.verifyTv.setTextColor(getResources().getColor(R.color.gray));
            }else{
                viewHolder.verifyTv.setVisibility(View.INVISIBLE);
            }
            return convertView;
        }

    }

    private static class ViewHolder {

        ImageView img;
        TextView nameTv;
        TextView priceTv;
        TextView startTv;
        TextView timeTv;
        TextView verifyTv;
    }

    @Override
    protected void onLoadMore(int lastId) {
        getMySupply(lastId);
    }

    @Override
    protected void onRefresh() {
        getMySupply(0);
    }
}
