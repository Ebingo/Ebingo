package com.promote.ebingoo.center;

import android.content.Intent;
import android.os.Bundle;
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
import com.promote.ebingoo.BaseListActivity;
import com.promote.ebingoo.InformationActivity.ProductInfoActivity;
import com.promote.ebingoo.R;
import com.promote.ebingoo.application.HttpConstant;
import com.promote.ebingoo.bean.CollectBean;
import com.promote.ebingoo.bean.CollectBeanTools;
import com.promote.ebingoo.bean.Company;
import com.promote.ebingoo.impl.EbingoHandler;
import com.promote.ebingoo.impl.EbingoRequestParmater;
import com.promote.ebingoo.util.ContextUtil;
import com.promote.ebingoo.util.FileUtil;
import com.promote.ebingoo.util.LogCat;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyCollectionActivity extends BaseListActivity implements View.OnClickListener {

    private static boolean refresh;

    private DisplayImageOptions mOptions;

    private MyAdapter myAdapter = null;

    private ArrayList<CollectBean> mCollections = new ArrayList<CollectBean>();

    /**
     * 刷新收藏列表
     */
    public static void setRefresh() {
        MyCollectionActivity.refresh = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
    }

    private void initialize() {
        setUpRefreshable(true);
        mOptions = ContextUtil.getSquareImgOptions();
        myAdapter = new MyAdapter();
        setListAdapter(myAdapter);
        enableCache(FileUtil.FILE_WISH_LIST, mCollections);
        if (mCollections.size() == 0 || getIntent().getBooleanExtra(ARG_REFRESH, false) || refresh) {
            onRefresh();
        }
        enableDelete(true);
        setDownRefreshable(true);
    }

    /**
     * 获取收藏列表.
     *
     * @param lastId 刷新下表.
     */
    public void getWishlist(final int lastId) {

        String urlStr = HttpConstant.getWishlist;
        EbingoRequestParmater parama = new EbingoRequestParmater(getApplicationContext());
        parama.put("lastid", lastId);
        parama.put("pagesize", pageSize);
        parama.put("company_id", Company.getInstance().getCompanyId());
        LogCat.i("--->", parama + "");
        HttpUtil.post(urlStr, parama, new JsonHttpResponseHandler("utf-8") {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                LogCat.i("--->", response.toString());
                ArrayList<CollectBean> collectBeans = CollectBeanTools.getCollections(response.toString());

                if (collectBeans != null && collectBeans.size() > 0) {
                    if (lastId == 0) mCollections.clear();
                    mCollections.addAll(collectBeans);
                    myAdapter.notifyDataSetChanged();
                    refresh = false;
                }
                onLoadFinish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        CollectBean collect = mCollections.get(position);
        Integer info_id = collect.getInfo_id();
        if (info_id == null || info_id <= 0) {
            ContextUtil.toast(R.string.data_error);
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(ProductInfoActivity.ARG_ID, info_id);
        intent.setClass(MyCollectionActivity.this, ProductInfoActivity.class);
        startActivity(intent);


    }

    @Override
    protected CharSequence onPrepareDelete(int position) {
        return mCollections.get(position).getTitle();
    }

    @Override
    protected void onDelete(final int position) {
        EbingoRequestParmater parmater = new EbingoRequestParmater(getApplicationContext());
        parmater.put("company_id", Company.getInstance().getCompanyId());
        parmater.put("wishlistid", mCollections.get(position).getId());
        HttpUtil.post(HttpConstant.cancleWishlist, parmater, new EbingoHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                ContextUtil.toast("删除收藏成功！");
                mCollections.remove(position);
                myAdapter.notifyDataSetChanged();
                refreshFooterView(true);
            }

            @Override
            public void onFail(int statusCode, String msg) {
                ContextUtil.toast("删除收藏失败！" + msg);
            }

            @Override
            public void onFinish() {

            }
        });
    }

    @Override
    protected void onRefresh() {
        getWishlist(0);
    }

    @Override
    protected void onLoadMore(int lastId) {
        getWishlist(lastId);
    }

    private static class ViewHolder {
        ImageView imgIv;
        TextView priceTv;
        TextView nameTv;
        TextView timesTv;
        TextView timeTv;
    }

    /**
     * listView adapter.
     */
    class MyAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return mCollections.size();
        }

        @Override
        public Object getItem(int position) {
            return mCollections.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.mycollection_item, null);
                viewHolder = new ViewHolder();
                viewHolder.imgIv = (ImageView) convertView.findViewById(R.id.mycol_item_img);
                viewHolder.nameTv = (TextView) convertView.findViewById(R.id.mycol_item_name);
                viewHolder.priceTv = (TextView) convertView.findViewById(R.id.mycol_item_price_tv);
                viewHolder.timesTv = (TextView) convertView.findViewById(R.id.mycol_item_times_tv);
                viewHolder.timeTv = (TextView) convertView.findViewById(R.id.mycol_item_time_tv);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            CollectBean collectBean = mCollections.get(position);
            ImageManager.load(collectBean.getImg(), viewHolder.imgIv, mOptions);
            viewHolder.nameTv.setText(collectBean.getTitle());
            viewHolder.priceTv.setText(Double.parseDouble(collectBean.getPrice()) == 0 ? getApplicationContext().getString(R.string.price_zero) : collectBean.getPrice());
            viewHolder.timesTv.setText(collectBean.getCollect_num() + "");
            viewHolder.timeTv.setText(collectBean.getCollectTimes());

            return convertView;
        }

    }
}
