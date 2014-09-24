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
import com.jch.lib.util.ImageManager;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.promote.ebingo.BaseListActivity;
import com.promote.ebingo.InformationActivity.ProductInfoActivity;
import com.promote.ebingo.R;
import com.promote.ebingo.application.HttpConstant;
import com.promote.ebingo.bean.CollectBean;
import com.promote.ebingo.bean.CollectBeanTools;
import com.promote.ebingo.bean.Company;
import com.promote.ebingo.impl.EbingoHandler;
import com.promote.ebingo.impl.EbingoRequestParmater;
import com.promote.ebingo.util.ContextUtil;
import com.promote.ebingo.util.LogCat;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyCollectionActivity extends BaseListActivity implements View.OnClickListener {

    private DisplayImageOptions mOptions;

    private MyAdapter myAdapter = null;

    private ArrayList<CollectBean> mCollections = new ArrayList<CollectBean>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
    }

    private void initialize() {

        mOptions = new DisplayImageOptions.Builder()
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .showImageForEmptyUri(R.drawable.loading)
                .showImageOnLoading(R.drawable.loading)
                .showImageOnFail(R.drawable.loading)
                .cacheInMemory(true).cacheOnDisc(true).build();


        myAdapter = new MyAdapter();
        setListAdapter(myAdapter);
        getWishlist(0);
        enableDelete(true);
    }


    /**
     * 获取收藏列表.
     *
     * @param lastId 刷新下表.
     */
    public void getWishlist(int lastId) {

        String urlStr = HttpConstant.getWishlist;
        EbingoRequestParmater parama = new EbingoRequestParmater(getApplicationContext());
        parama.put("lastid", lastId);
        parama.put("pagesize ", 50);
        parama.put("company_id", Company.getInstance().getCompanyId());
        final ProgressDialog dialog = DialogUtil.waitingDialog(MyCollectionActivity.this);
        LogCat.i("--->", parama + "");
        HttpUtil.post(urlStr, parama, new JsonHttpResponseHandler("utf-8") {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                LogCat.i("--->", response.toString());
                ArrayList<CollectBean> collectBeans = CollectBeanTools.getCollections(response.toString());

                if (collectBeans != null && collectBeans.size() > 0) {
                    mCollections.clear();
                    mCollections.addAll(collectBeans);
                    myAdapter.notifyDataSetChanged();
                }
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

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = new Intent(MyCollectionActivity.this, ProductInfoActivity.class);
        intent.putExtra(ProductInfoActivity.ARG_ID, mCollections.get(position).getInfo_id());
        intent.putExtra("collectId", mCollections.get(position).getId());
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
            viewHolder.priceTv.setText(collectBean.getPrice());
            viewHolder.timesTv.setText(collectBean.getCollect_num() + "");
            viewHolder.timeTv.setText(collectBean.getCollectTimes());

            return convertView;
        }

    }

    private static class ViewHolder {

        ImageView imgIv;
        TextView priceTv;
        TextView nameTv;
        TextView timesTv;
        TextView timeTv;

    }

}
