package com.promote.ebingo.publish;

import android.app.Activity;
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

import com.promote.ebingo.R;
import com.promote.ebingo.util.Dimension;
import com.promote.ebingo.util.LogCat;

import java.util.Arrays;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by acer on 2014/9/9.
 */
public class PickRegionActivity extends Activity implements AdapterView.OnItemClickListener,View.OnClickListener {

    ListView listView;

    private final String[] regions=new String[]{"江苏","浙江","北京","上海","天津","重庆","四川","山西","安徽"};
    private List<String> regionList=  Arrays.asList(regions);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_trade);
        listView = (ListView) findViewById(R.id.category_list);
        listView.setOnItemClickListener(this);
        listView.setAdapter(new RegionAdapter(this));
        ((TextView) findViewById(R.id.common_title_tv)).setText("选择区域");
        findViewById(R.id.common_back_btn).setOnClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent data = new Intent();
        data.putExtra("result",regionList.get(position));
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

    class RegionAdapter extends BaseAdapter {
        private Activity context;


        RegionAdapter(Activity context) {
            this.context = context;

        }

        @Override
        public int getCount() {
            return regionList.size();
        }

        @Override
        public Object getItem(int position) {
            return regionList.get(position);
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
            holder.tv.setText(regionList.get(position));
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
