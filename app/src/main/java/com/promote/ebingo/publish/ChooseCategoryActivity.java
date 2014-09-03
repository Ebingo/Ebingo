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
import com.promote.ebingo.util.Dimension;
import com.promote.ebingo.util.LogCat;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import  static android.view.ViewGroup.LayoutParams.*;
/**
 * Created by acer on 2014/9/2.
 */
public class ChooseCategoryActivity extends Activity implements AdapterView.OnItemClickListener,View.OnClickListener{
    ListView categoryList;
    List<CategoryBeen> categories=new ArrayList<CategoryBeen>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_trade);
        categoryList=(ListView)findViewById(R.id.category_list);
        initData();
        categoryList.setAdapter(new CategoryAdapter(this));
    }

    private void initData(){
        ((TextView)findViewById(R.id.common_title_tv)).setText(getString(R.string.choose_trade));
        findViewById(R.id.common_back_btn).setOnClickListener(this);
        final ProgressDialog dialog= DialogUtil.waitingDialog(this);
        EbingoRequestParmater params=new EbingoRequestParmater(this);
        HttpUtil.post(HttpConstant.getCategories,new JsonHttpResponseHandler("utf-8"){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
               LogCat.i("----->onSuccess JSONObject="+response);
                dialog.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                LogCat.i("----->onSuccess JSONArray="+response);
                 dialog.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
                LogCat.i("----->onSuccess String="+responseString);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                LogCat.i("----->onFailure JSONObject="+errorResponse);
                dialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                LogCat.i("----->onFailure String="+responseString);
                dialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                LogCat.i("----->onFailure JSONArray="+errorResponse);
            }
        });

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent data=new Intent();
        data.putExtra("result",categories.get(position).getName());
        setResult(RESULT_OK,data);
        finish();
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.common_back_btn){
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    class CategoryAdapter extends BaseAdapter{
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
            LogCat.i(position+"");
            if(convertView==null){
                holder=new ViewHolder();
                holder.tv=new TextView(context);
                holder.tv.setLayoutParams(new AbsListView.LayoutParams(MATCH_PARENT,WRAP_CONTENT));
                holder.tv.setTextColor(context.getResources().getColor(R.color.black));
                holder.tv.setTextSize(context.getResources().getDimension(R.dimen.font_18));
                holder.tv.setBackgroundColor(Color.WHITE);
                holder.tv.setGravity(Gravity.CENTER_VERTICAL|Gravity.LEFT);
                holder.tv.setPadding(dp(12),dp(8),dp(6),dp(8));
                convertView=holder.tv;
            }else{
                holder=(ViewHolder)convertView.getTag();
            }
            holder.tv.setText(categories.get(position).getName());
            convertView.setTag(holder);
            return convertView;
        }
        private int dp(float value){
            return (int)Dimension.dp(value);
        }
    }

    static class ViewHolder{
        TextView tv;
    }
}
