package com.promote.ebingoo.center;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jch.lib.util.HttpUtil;
import com.promote.ebingoo.BaseListActivity;
import com.promote.ebingoo.InformationActivity.BuyInfoActivity;
import com.promote.ebingoo.R;
import com.promote.ebingoo.application.HttpConstant;
import com.promote.ebingoo.bean.Company;
import com.promote.ebingoo.bean.SearchTagBean;
import com.promote.ebingoo.impl.EbingoHandler;
import com.promote.ebingoo.impl.EbingoRequestParmater;
import com.promote.ebingoo.util.JsonUtil;
import com.promote.ebingoo.util.LogCat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TagInfoListActivity extends BaseListActivity implements View.OnClickListener {
    public static final String ID = "tag_id";
    public static final String NAME = "name";
    private ArrayList<SearchTagBean> data = new ArrayList<SearchTagBean>();
    private TagInfoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new TagInfoAdapter(data, this);
        initView();
    }

    /**
     * 根据id加载相关信息
     *
     * @param id
     */
    private void loadTagList(int id) {
        EbingoRequestParmater parmater = new EbingoRequestParmater(this);
        parmater.put("company_id", Company.getInstance().getCompanyId());
        parmater.put("tag_id", id);
        HttpUtil.post(HttpConstant.getTagInfoList, parmater, new EbingoHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                try {
                    data.clear();
                    JsonUtil.getArray(response.getJSONArray("data"), SearchTagBean.class, data);
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(int statusCode, String msg) {

            }

            @Override
            public void onFinish() {

            }
        });
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = new Intent(this, BuyInfoActivity.class);
        SearchTagBean bean = data.get(position);
        intent.putExtra(BuyInfoActivity.DEMAND_ID, bean.getId());
        startActivity(intent);
    }

    private void initView() {
        String title = getIntent().getStringExtra(NAME);
        if (!TextUtils.isEmpty(title)) setTitle(title);
        else setTitle(R.string.no_data);
        setListAdapter(adapter);
        int tagId = getIntent().getIntExtra(ID, -1);
        if (tagId != -1) loadTagList(tagId);
    }

    class TagInfoAdapter extends BaseAdapter {
        private ArrayList<SearchTagBean> data = new ArrayList<SearchTagBean>();
        private Context mContext;

        TagInfoAdapter(ArrayList<SearchTagBean> data, Context Context) {
            this.data = data;
            this.mContext = Context;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(mContext, R.layout.item_tag_info_list, null);
                holder.tv_create_time = (TextView) convertView.findViewById(R.id.tv_create_time);
                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            SearchTagBean tagBean = data.get(position);
            LogCat.i("--->", "tagBean=" + tagBean);
            holder.tv_name.setText(tagBean.getTitle());
            holder.tv_create_time.setText(tagBean.getCreate_time());
            convertView.setTag(holder);
            return convertView;
        }

        class ViewHolder {
            TextView tv_name;
            TextView tv_create_time;
        }
    }
}
