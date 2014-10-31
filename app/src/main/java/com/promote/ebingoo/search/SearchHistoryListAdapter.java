package com.promote.ebingoo.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.promote.ebingoo.R;
import com.promote.ebingoo.bean.SearchHistoryBean;
import com.promote.ebingoo.util.LogCat;

import java.util.ArrayList;

/**
 * Created by ACER on 2014/9/2.
 */
public class SearchHistoryListAdapter extends BaseAdapter {

    private Context mContext;

    private ArrayList<SearchHistoryBean> mSearchTypeBeans = new ArrayList<SearchHistoryBean>();

    /**
     * @param context
     * @param searchTypeBeans
     */
    public SearchHistoryListAdapter(Context context, ArrayList<SearchHistoryBean> searchTypeBeans) {

        this.mContext = context;
        if (searchTypeBeans != null && searchTypeBeans.size() != 0) {
            this.mSearchTypeBeans.addAll(searchTypeBeans);
        }

    }

    public void notifyDataSetChanged(ArrayList<SearchHistoryBean> searchTypeBeans) {
        this.mSearchTypeBeans.clear();
        this.mSearchTypeBeans.addAll(searchTypeBeans);
        super.notifyDataSetChanged();

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
