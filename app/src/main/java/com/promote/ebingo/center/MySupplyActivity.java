package com.promote.ebingo.center;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jch.lib.util.DialogUtil;
import com.jch.lib.util.HttpUtil;
import com.jch.lib.util.ImageManager;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.promote.ebingo.BaseActivity;
import com.promote.ebingo.BaseListActivity;
import com.promote.ebingo.InformationActivity.BuyInfoActivity;
import com.promote.ebingo.InformationActivity.ProductInfoActivity;
import com.promote.ebingo.R;
import com.promote.ebingo.application.HttpConstant;
import com.promote.ebingo.bean.Company;
import com.promote.ebingo.bean.SearchSupplyBean;
import com.promote.ebingo.bean.SearchSupplyBeanTools;
import com.promote.ebingo.impl.EbingoHandler;
import com.promote.ebingo.impl.EbingoRequestParmater;
import com.promote.ebingo.util.LogCat;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MySupplyActivity extends BaseListActivity implements AdapterView.OnItemLongClickListener {

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

        mOptions = new DisplayImageOptions.Builder()
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .showImageForEmptyUri(R.drawable.loading)
                .showImageOnLoading(R.drawable.loading)
                .showImageOnFail(R.drawable.loading)
                .cacheInMemory(true).cacheOnDisc(true).build();


        // 使用DisplayImageOptions.Builder()创建DisplayImageOptions

        adapter = new MyAdapter();
        setListAdapter(adapter);
        getListView().setOnItemLongClickListener(this);
        getMySupply(0);
    }

    private void delete(final int id) {
        EbingoRequestParmater param = new EbingoRequestParmater(this);
        param.put("company_id", Company.getInstance().getCompanyId());
        param.put("infoid", id);

        HttpUtil.post(HttpConstant.deleteInfo, param, new EbingoHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                mSupplyBeans.remove(id);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFail(int statusCode, String msg) {

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
        param.put("pagesize", 20);
        try {
            param.put("condition", URLEncoder.encode(appendKeyworld(Company.getInstance().getCompanyId()), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpUtil.post(urlStr, param, new JsonHttpResponseHandler("utf-8") {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                ArrayList<SearchSupplyBean> searchSupplyBeans = SearchSupplyBeanTools.getSearchSupplyBeans(response.toString());
                mSupplyBeans.clear();
                mSupplyBeans.addAll(searchSupplyBeans);
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
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        DialogUtil.showDeleteDialog(this, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        delete(position);
                        break;
                    case 1:
                        break;
                }
            }
        });
        return false;
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


}
