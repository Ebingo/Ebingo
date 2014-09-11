package com.promote.ebingo.center;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.promote.ebingo.R;

public class MySupplyActivity extends ActionBarActivity implements View.OnClickListener {
import com.jch.lib.util.DialogUtil;
import com.jch.lib.util.HttpUtil;
import com.jch.lib.util.ImageManager;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.promote.ebingo.InformationActivity.ProductInfoActivity;
import com.promote.ebingo.R;
import com.promote.ebingo.application.HttpConstant;
import com.promote.ebingo.bean.Company;
import com.promote.ebingo.bean.SearchSupplyBean;
import com.promote.ebingo.bean.SearchSupplyBeanTools;
import com.promote.ebingo.impl.EbingoRequestParmater;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MySupplyActivity extends ActionBarActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private ListView mysupplylv;
    private ImageView commonbackbtn;
    private TextView commontitletv;
    private ArrayList<SearchSupplyBean> mSupplyBeans = new ArrayList<SearchSupplyBean>();
    private DisplayImageOptions mOptions;
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_supply);
        initialize();
    }


    private void initialize() {

        mOptions = new DisplayImageOptions.Builder()
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .showImageForEmptyUri(R.drawable.loading)
                .showImageOnLoading(R.drawable.loading)
                .showImageOnFail(R.drawable.loading)
                .cacheInMemory(true).cacheOnDisc(true).build();

        commonbackbtn = (ImageView) findViewById(R.id.common_back_btn);
        commontitletv = (TextView) findViewById(R.id.common_title_tv);
        mysupplylv = (ListView) findViewById(R.id.mysupply_lv);

        commontitletv.setText(getResources().getString(R.string.my_supply));
        commonbackbtn.setOnClickListener(this);
        // 使用DisplayImageOptions.Builder()创建DisplayImageOptions

        adapter = new MyAdapter();
        mysupplylv.setAdapter(adapter);
        mysupplylv.setOnItemClickListener(this);
        getMySupply(0);
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        switch (id) {

            case R.id.common_back_btn: {
                onBackPressed();
                finish();
                break;
            }

            case R.id.common_title_tv: {
                break;
            }
            default: {

            }


        }

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
                if (searchSupplyBeans != null && searchSupplyBeans.size() != 0) {
                    mysupplylv.setVisibility(View.VISIBLE);
                    mSupplyBeans.clear();
                    mSupplyBeans.addAll(searchSupplyBeans);
                    adapter.notifyDataSetChanged();
                } else {
                    mysupplylv.setVisibility(View.GONE);
                }

                dialog.dismiss();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                mysupplylv.setVisibility(View.GONE);
                dialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                mysupplylv.setVisibility(View.GONE);
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent intent = new Intent(MySupplyActivity.this, ProductInfoActivity.class);
        intent.putExtra(ProductInfoActivity.ARG_ID, mSupplyBeans.get(position).getId());
        startActivity(intent);

    }

    /**
     * listview adapter。
     */
    private class MyAdapter extends BaseAdapter {

        private ImageView mysupplyitemimg;
        private ImageView mysupplyarroimg;
        private TextView mysupplyitemname;
        private TextView mysupplypricetv;
        private TextView mysupplysupplynumtv;
        private TextView mysupplytime;

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

                viewHolder.img = (ImageView) findViewById(R.id.mysupply_item_img);
                viewHolder.nameTv = (TextView) findViewById(R.id.mysupply_item_name);
                viewHolder.priceTv = (TextView) findViewById(R.id.mysupply_price_tv);
                viewHolder.startTv = (TextView) findViewById(R.id.mysupply_supply_num_tv);
                viewHolder.timeTv = (TextView) findViewById(R.id.mysupply_time);

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
