package com.promote.ebingo.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.promote.ebingo.R;
import com.promote.ebingo.bean.SearchDemandBean;
import com.promote.ebingo.bean.SearchHistoryBean;
import com.promote.ebingo.bean.SearchInterpriseBean;
import com.promote.ebingo.bean.SearchSupplyBean;
import com.promote.ebingo.bean.SearchTypeBean;

import java.util.ArrayList;

/**
 * Created by ACER on 2014/9/2.
 */
public class SearchListAdapter extends BaseAdapter {

    private  Context mContext;
    private SearchType mType;

    private ArrayList<SearchTypeBean> mSearchTypeBeans = new ArrayList<SearchTypeBean>();
    private TextView searchhistoryitemtv;

    /**
     *
     * @param context
     * @param type
     * @param searchTypeBeans
     */
    public SearchListAdapter(Context context, SearchType type, ArrayList<SearchTypeBean> searchTypeBeans){

        this.mContext = context;
        this.mType = type;
        if (searchTypeBeans != null){
            this.mSearchTypeBeans = searchTypeBeans;
        }

    }

    @Override
    public int getCount() {
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

        mType = getSearchType();
        if (mType == SearchType.INTERPRISE){

        }else if (mType == SearchType.SUPPLY){

        }else if(mType == SearchType.DEMAND){

        }else {    //搜索记录.

            ViewHolder viewHolder = null;
            if (viewHolder == null){
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.search_history_item, null);
                viewHolder.name = (TextView)convertView.findViewById(R.id.search_history_item_tv);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder)convertView.getTag();
            }

            SearchHistoryBean searchHistoryBean = (SearchHistoryBean)mSearchTypeBeans.get(position);
            viewHolder.name.setText(searchHistoryBean.getHistory());
        }


        return convertView;
    }

    /**
     * 獲得list 要顯示的類型。默認是history。
     * @return
     */
    public SearchType getSearchType(){
        if (mSearchTypeBeans.size() > 0){
            SearchTypeBean searchTypeBean = mSearchTypeBeans.get(0);
            if (searchTypeBean instanceof SearchInterpriseBean){
                mType = SearchType.INTERPRISE;
            }else if (searchTypeBean instanceof SearchDemandBean){
                mType = SearchType.DEMAND;
            }else if (searchTypeBean instanceof SearchSupplyBean){
                mType = SearchType.SUPPLY;
            }else {
                mType = SearchType.HISTORY;
            }
        }

        return mType;

    }

    public void notifyDataSetChanged(ArrayList<SearchTypeBean> searchTypeBeans) {

        this.mSearchTypeBeans = searchTypeBeans;
        getSearchType();
        super.notifyDataSetChanged();
    }


    class ViewHolder{
        TextView name;
    }

    class SupplyViewHolder extends ViewHolder{
        ImageView img;
        TextView price;
        TextView min_num;
        TextView look_num;
        TextView company_name;
    }

    class DemandViewHolder extends ViewHolder{

        TextView date;
        TextView look_num;
        TextView introduction;
    }

    class Interprise extends ViewHolder{
        ImageView img;
        TextView region;//地區。
        TextView Business; //主营业务。
    }


}
