package com.promote.ebingo.publish;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jch.lib.util.DialogUtil;
import com.jch.lib.util.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.promote.ebingo.R;
import com.promote.ebingo.application.HttpConstant;
import com.promote.ebingo.bean.CategoryBeen;
import com.promote.ebingo.impl.EbingoRequestParmater;
import com.promote.ebingo.util.ContextUtil;
import com.promote.ebingo.util.Dimension;
import com.promote.ebingo.util.LogCat;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.*;

/**
 * Created by acer on 2014/9/2.
 */
public class PickCategoryActivity extends Activity implements AdapterView.OnItemClickListener, View.OnClickListener {
    ListView categoryList;
    List<CategoryBeen> categories = new ArrayList<CategoryBeen>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_trade);
        categoryList = (ListView) findViewById(R.id.category_list);
        categoryList.setOnItemClickListener(this);
        initData();
    }

    private void initData() {
        ((TextView) findViewById(R.id.common_title_tv)).setText(getString(R.string.choose_trade));
        findViewById(R.id.common_back_btn).setOnClickListener(this);
        final ProgressDialog dialog = DialogUtil.waitingDialog(this);
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
                    categoryList.setAdapter(new CategoryAdapter(PickCategoryActivity.this));
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
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent data = new Intent();
        CategoryBeen selectCategory = categories.get(position);
        data.putExtra("categoryId", selectCategory.getId());
        data.putExtra("result", selectCategory.getName());
        LogCat.i("");
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.common_back_btn) {
            setResult(RESULT_CANCELED);
            finish();
        }
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
