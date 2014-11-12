package com.promote.ebingoo.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.promote.ebingoo.R;

import java.util.ArrayList;

/**
 * Created by ACER on 2014/11/12.
 */
public class SearchKeyAdapter extends BaseAdapter {

    private ArrayList<String> mKeys = new ArrayList<String>();
    private Context context;

    public SearchKeyAdapter(Context context) {
        this.context = context;
    }

    /**
     * 更新数据.
     *
     * @param keys
     */
    public void nodifyOnDataChanged(ArrayList<String> keys) {
        if (keys != null && keys.size() != 0) {
            mKeys.clear();
            mKeys.addAll(keys);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mKeys.size();
    }

    @Override
    public Object getItem(int position) {
        return mKeys.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.search_key_item, null);
            viewHolder = new ViewHolder();
            viewHolder.tv = (TextView) convertView.findViewById(R.id.search_key_tv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tv.setText(mKeys.get(position));

        return convertView;
    }


    static class ViewHolder {
        TextView tv;
    }
}
