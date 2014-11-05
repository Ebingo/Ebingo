package com.promote.ebingoo.publish;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jch.lib.util.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.promote.ebingoo.BaseActivity;
import com.promote.ebingoo.R;
import com.promote.ebingoo.application.HttpConstant;
import com.promote.ebingoo.bean.CategoryBeen;
import com.promote.ebingoo.impl.EbingoRequestParmater;
import com.promote.ebingoo.util.ContextUtil;
import com.promote.ebingoo.util.Dimension;
import com.promote.ebingoo.util.FileUtil;
import com.promote.ebingoo.util.LogCat;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by acer on 2014/9/2.
 */
public class PickCategoryActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    List<CategoryBeen> categories_1 = new ArrayList<CategoryBeen>();
    List<CategoryBeen> categories_2 = new ArrayList<CategoryBeen>();
    CategoryAdapter1 adapter1;
    CategoryAdapter2 adapter2;
    ListView lv_category_1, lv_category_2;
    private int selected = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pick_category);
        lv_category_1 = (ListView) findViewById(R.id.lv_category_1);
        lv_category_2 = (ListView) findViewById(R.id.lv_category_2);
        initData();
        adapter1 = new CategoryAdapter1(this);
        adapter2 = new CategoryAdapter2(this);
        lv_category_1.setAdapter(adapter1);
        lv_category_1.setOnItemClickListener(this);
        lv_category_2.setAdapter(adapter2);
    }

    private void initData() {
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
                        categories_1.add(categoryBeen);

                    }
                    adapter1.notifyDataSetChanged();
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

    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent data = new Intent();
        CategoryBeen selectCategory = categories_1.get(position);
        data.putExtra("categoryId", selectCategory.getId());
        data.putExtra("result", selectCategory.getName());
        LogCat.i("");
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selected = position;
        ContextUtil.toast("select:" + selected);
        adapter1.notifyDataSetChanged();
        getSubCategory(position);
    }

    private void getSubCategory(int id) {
        categories_2.clear();
        String name = categories_1.get(id).getName();
        categories_2= (List<CategoryBeen>) FileUtil.readCache(this,"subCategory"+id);
        if (categories_2==null){
            for (int i = 0; i < 20; i++) {
                CategoryBeen been = new CategoryBeen();
                been.setName(name + i);
                categories_2.add(been);
            }
            FileUtil.saveCache(this,"subCategory"+id,categories_2);
        }
        adapter2.notifyDataSetChanged();
    }


    class CategoryAdapter1 extends BaseAdapter {
        private Activity context;
        private final int checkedColor = 0xff3344bb;
        private final int unCheckedColor = 0xff666666;

        CategoryAdapter1(Activity context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return categories_1.size();
        }

        @Override
        public Object getItem(int position) {
            return categories_1.get(position);
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
                holder.tv = (TextView) LayoutInflater.from(context).inflate(R.layout.activate_textview, null);
                convertView = holder.tv;
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tv.setText(categories_1.get(position).getName());
            if (selected == position) holder.tv.setBackgroundColor(checkedColor);
            else holder.tv.setBackgroundColor(unCheckedColor);
            convertView.setTag(holder);
            return convertView;
        }

        private int dp(float value) {
            return (int) Dimension.dp(value);
        }
    }

    class CategoryAdapter2 extends BaseAdapter {
        private Activity context;
        private final int checkedColor = 0xff3344ee;
        private final int unCheckedColor = 0xff999999;

        CategoryAdapter2(Activity context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return categories_2.size();
        }

        @Override
        public Object getItem(int position) {
            return categories_2.get(position);
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
                holder.tv = (TextView) LayoutInflater.from(context).inflate(R.layout.activate_textview, null);
                convertView = holder.tv;
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tv.setText(categories_2.get(position).getName());
            if (selected == position) holder.tv.setBackgroundColor(checkedColor);
            else holder.tv.setBackgroundColor(unCheckedColor);
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
