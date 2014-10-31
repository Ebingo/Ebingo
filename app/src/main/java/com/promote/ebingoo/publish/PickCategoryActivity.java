package com.promote.ebingoo.publish;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jch.lib.util.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.promote.ebingoo.BaseListActivity;
import com.promote.ebingoo.R;
import com.promote.ebingoo.application.HttpConstant;
import com.promote.ebingoo.bean.CategoryBeen;
import com.promote.ebingoo.impl.EbingoRequestParmater;
import com.promote.ebingoo.util.ContextUtil;
import com.promote.ebingoo.util.Dimension;
import com.promote.ebingoo.util.LogCat;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by acer on 2014/9/2.
 */
public class PickCategoryActivity extends BaseListActivity {
    List<CategoryBeen> categories = new ArrayList<CategoryBeen>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    private void initData() {
        ((TextView) findViewById(R.id.common_title_tv)).setText(getString(R.string.choose_trade));
        findViewById(R.id.common_back_btn).setOnClickListener(this);
        EbingoRequestParmater params = new EbingoRequestParmater(this);
        HttpUtil.post(HttpConstant.getCategories, params, new JsonHttpResponseHandler("utf-8") {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                LogCat.i("--->", response + "");
                try {
                    JSONArray array = response.getJSONArray("response");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        CategoryBeen categoryBeen = new CategoryBeen();
                        categoryBeen.setName(object.getString("name"));
                        categoryBeen.setImage(object.getString("image"));
                        categoryBeen.setId(object.getInt("id"));
                        categories.add(categoryBeen);
                    }
                    setListAdapter(new CategoryAdapter(PickCategoryActivity.this));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                ContextUtil.toast(responseString);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent data = new Intent();
        CategoryBeen selectCategory = categories.get(position);
        data.putExtra("categoryId", selectCategory.getId());
        data.putExtra("result", selectCategory.getName());
        LogCat.i("");
        setResult(RESULT_OK, data);
        finish();
    }


    class CategoryAdapter extends BaseAdapter {
        private Activity context;


        CategoryAdapter(Activity context) {
            this.context = context;

        }

        @Override
        public int getCount() {
            return categories.size();
        }

        @Override
        public Object getItem(int position) {
            return categories.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            LogCat.i(position + "");
            if (convertView == null) {
                holder = new ViewHolder();
                holder.tv = new TextView(context);
                holder.tv.setLayoutParams(new AbsListView.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
                holder.tv.setTextColor(context.getResources().getColor(R.color.black));
                holder.tv.setTextSize(18);
                holder.tv.setClickable(false);
                holder.tv.setBackgroundColor(Color.WHITE);
                holder.tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
                holder.tv.setPadding(dp(12), dp(8), dp(6), dp(8));
                convertView = holder.tv;
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tv.setText(categories.get(position).getName());
            convertView.setTag(holder);
            return convertView;
        }

        private int dp(float value) {
            return (int) Dimension.dp(value);
        }
    }

    static class ViewHolder {
        TextView tv;
    }
}
