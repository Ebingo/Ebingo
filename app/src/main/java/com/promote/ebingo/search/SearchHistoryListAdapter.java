package com.promote.ebingo.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.promote.ebingo.R;
import com.promote.ebingo.bean.SearchHistoryBean;
import com.promote.ebingo.util.LogCat;

import java.util.ArrayList;

/**
 * Created by ACER on 2014/9/2.
 */
public class SearchHistoryListAdapter extends BaseAdapter {

    private Context mContext;

    private ArrayList<SearchHistoryBean> mSearchTypeBeans = null;

    /**
     * @param context
     * @param searchTypeBeans
     */
    public SearchHistoryListAdapter(Context context, ArrayList<SearchHistoryBean> searchTypeBeans) {

        this.mContext = context;
        if (searchTypeBeans != null) {
            this.mSearchTypeBeans = searchTypeBeans;
        }

    }

    @Override
    public int getCount() {

        LogCat.d("adapter beanCount: " + mSearchTypeBeans.size());
        return mSearchTypeBeans.size();

    }

    @Override
    public Object getItem(int position) {
        return mSearchTypeBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        ViewHolder viewHolder = null;
        if (convertView == null || !(convertView.getTag() instanceof ViewHolder)) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.search_history_item, null);
            viewHolder.name = (TextView) convertView.findViewById(R.id.search_history_item_tv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        SearchHistoryBean searchHistoryBean = (SearchHistoryBean) mSearchTypeBeans.get(position);
        LogCat.d("adapter getview: " + searchHistoryBean.getHistory());
        viewHolder.name.setText(searchHistoryBean.getHistory());


        return convertView;
    }


    class ViewHolder {
        TextView name;
    }


}
