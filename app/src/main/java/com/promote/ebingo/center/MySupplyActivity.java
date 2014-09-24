package com.promote.ebingo.center;

import android.app.ProgressDialog;
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

import com.jch.lib.util.DialogUtil;
import com.jch.lib.util.HttpUtil;
import com.jch.lib.util.ImageManager;
import com.jch.lib.view.PullToRefreshView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.promote.ebingo.BaseListActivity;
import com.promote.ebingo.InformationActivity.ProductInfoActivity;
import com.promote.ebingo.R;
import com.promote.ebingo.application.HttpConstant;
import com.promote.ebingo.bean.Company;
import com.promote.ebingo.bean.SearchSupplyBean;
import com.promote.ebingo.bean.SearchSupplyBeanTools;
import com.promote.ebingo.impl.EbingoHandler;
import com.promote.ebingo.impl.EbingoRequestParmater;
import com.promote.ebingo.util.ContextUtil;
import com.promote.ebingo.util.LogCat;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MySupplyActivity extends BaseListActivity {

    private ArrayList<SearchSupplyBean> mSupplyBeans = new ArrayList<SearchSupplyBean>();
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
        setUpRefreshable(true);
        mOptions = new DisplayImageOptions.Builder()
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .showImageForEmptyUri(R.drawable.loading)
                .showImageOnLoading(R.drawable.loading)
                .showImageOnFail(R.drawable.loading)
                .cacheInMemory(true).cacheOnDisc(true).build();


        // 使用DisplayImageOptions.Builder()创建DisplayImageOptions

        adapter = new MyAdapter();
        setListAdapter(adapter);
        getMySupply(0);
        enableDelete(true);
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

    private void getMySupply(int lastId) {
        String urlStr = HttpConstant.getSupplyInfoList;
        EbingoRequestParmater param = new EbingoRequestParmater(getApplicationContext());
        final ProgressDialog dialog = DialogUtil.waitingDialog(MySupplyActivity.this);
        param.put("lastid", lastId);
        param.put("pagesize", pageSize);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("{")
                    .append(makeCondition("company_id", Company.getInstance().getCompanyId()))
                    .append(",")
                    .append(makeCondition("sort", "time"))
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
                    mSupplyBeans.addAll(searchSupplyBeans);
                    adapter.notifyDataSetChanged();
                    onLoadMoreFinish(true);
                }else{
                    onLoadMoreFinish(false);
                }
                dialog.dismiss();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                dialog.dismiss();
                LogCat.i("--->", errorResponse.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                dialog.dismiss();
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

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            SearchSupplyBean supplyBean = mSupplyBeans.get(position);
            ImageManager.load(supplyBean.getImage(), viewHolder.img, mOptions);
            viewHolder.nameTv.setText(supplyBean.getName());
            viewHolder.priceTv.setText(supplyBean.getPrice());
            if (!TextUtils.isEmpty(supplyBean.getUnit()))
                viewHolder.priceTv.append("/" + supplyBean.getUnit());
            viewHolder.timeTv.setText(supplyBean.getDate());
            viewHolder.startTv.setText(supplyBean.getMin_supply_num());

            return convertView;
        }

    }

    private static class ViewHolder {

        ImageView img;
        TextView nameTv;
        TextView priceTv;
        TextView startTv;
        TextView timeTv;
    }

    @Override
    protected void onLoadMore(int lastId) {
        getMySupply(lastId);
    }
}
