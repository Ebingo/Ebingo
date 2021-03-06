package com.promote.ebingoo.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jch.lib.util.ImageManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.promote.ebingoo.R;
import com.promote.ebingoo.bean.SearchDemandBean;
import com.promote.ebingoo.bean.SearchInterpriseBean;
import com.promote.ebingoo.bean.SearchSupplyBean;
import com.promote.ebingoo.bean.SearchTypeBean;
import com.promote.ebingoo.util.ContextUtil;

import java.util.ArrayList;

/**
 * Created by jch on 2014/9/22.
 * <p/>
 * 搜索结果数据
 */
public class SearchResultAdapter extends BaseAdapter {

    private Context mContext;
    private SearchType mType;

    private ArrayList<SearchTypeBean> mSearchTypeBeans = null;

    /**
     * 是否应该执行getview（）。 *
     */
    private DisplayImageOptions mOptions;

    public SearchResultAdapter(Context context, SearchType type, ArrayList<SearchTypeBean> searchTypeBeans) {

        // 使用DisplayImageOptions.Builder()创建DisplayImageOptions
        mOptions = ContextUtil.getSquareImgOptions();
        this.mContext = context;
        mSearchTypeBeans = searchTypeBeans;
        mType = type;
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

        SearchTypeBean searchTypeBean = mSearchTypeBeans.get(position);
        if (searchTypeBean instanceof SearchInterpriseBean) {

            InterpriseViewHolder viewHolder = null;
            if (convertView == null || !(convertView.getTag() instanceof InterpriseViewHolder)) {
                viewHolder = new InterpriseViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.search_interprise_result_item_layout, null);
                viewHolder.img = (ImageView) convertView.findViewById(R.id.search_interprise_item_iv);
                viewHolder.vip_img = (ImageView) convertView.findViewById(R.id.search_list_vip_img);
                viewHolder.region = (TextView) convertView.findViewById(R.id.search_result_province_tv);
                viewHolder.name = (TextView) convertView.findViewById(R.id.search_result_black_name_tv);
                viewHolder.Business = (TextView) convertView.findViewById(R.id.search_result_describe_tv);
                convertView.setTag(viewHolder);

            } else {
                viewHolder = (InterpriseViewHolder) convertView.getTag();
            }

            SearchInterpriseBean interpriseBean = (SearchInterpriseBean) mSearchTypeBeans.get(position);
            viewHolder.name.setText(interpriseBean.getName());
            ImageManager.load(interpriseBean.getImage(), viewHolder.img, mOptions);
            viewHolder.region.setText(interpriseBean.getRegion());
            viewHolder.Business.setText(interpriseBean.getBusiness());
            viewHolder.vip_img.setImageDrawable(ContextUtil.getVipImgByType(mContext.getResources(), interpriseBean.getRank()));

        } else if (searchTypeBean instanceof SearchSupplyBean) {

            SupplyViewHolder viewHolder = null;
            if (convertView == null || !(convertView.getTag() instanceof SupplyViewHolder)) {
                viewHolder = new SupplyViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.search_supply_result_item_layout, null);

                viewHolder.img = (ImageView) convertView.findViewById(R.id.search_list_item_img);
                viewHolder.name = (TextView) convertView.findViewById(R.id.search_result_black_name_tv);
                viewHolder.price = (TextView) convertView.findViewById(R.id.search_result_price_tv);
                viewHolder.min_num = (TextView) convertView.findViewById(R.id.search_min_num);
                viewHolder.look_num = (TextView) convertView.findViewById(R.id.search_result_item_look_num_tvs);
                viewHolder.company_name = (TextView) convertView.findViewById(R.id.search_result_company_name_tv);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (SupplyViewHolder) convertView.getTag();
            }

            SearchSupplyBean supplyBean = (SearchSupplyBean) mSearchTypeBeans.get(position);
            viewHolder.name.setText(supplyBean.getName());
            ImageManager.load(supplyBean.getImage(), viewHolder.img, mOptions);
            viewHolder.price.setText(String.valueOf(supplyBean.getPrice()));  //服務器字段缺失。
            viewHolder.min_num.setText(supplyBean.getMin_supply_num());
            viewHolder.look_num.setText(String.valueOf(supplyBean.getRead_num()));
            viewHolder.company_name.setText(String.valueOf(supplyBean.getCompany()));


        } else if (searchTypeBean instanceof SearchDemandBean) {

            DemandViewHolder viewHolder = null;
            if (convertView == null || !(convertView.getTag() instanceof DemandViewHolder)) {

                viewHolder = new DemandViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.search_buy_result_item_layout, null);
                viewHolder.date = (TextView) convertView.findViewById(R.id.search_buy_result_time_tv);
                viewHolder.look_num = (TextView) convertView.findViewById(R.id.search_result_item_look_num_tvs);
                viewHolder.name = (TextView) convertView.findViewById(R.id.search_result_black_name_tv);
                viewHolder.introduction = (TextView) convertView.findViewById(R.id.search_result_describe_tv);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (DemandViewHolder) convertView.getTag();
            }

            SearchDemandBean demandBean = (SearchDemandBean) mSearchTypeBeans.get(position);
            viewHolder.name.setText(demandBean.getName());
            viewHolder.date.setText(demandBean.getDate());
            viewHolder.look_num.setText(String.valueOf(demandBean.getRead_num()));
            viewHolder.introduction.setText(demandBean.getIntroduction());


        }
        return convertView;
    }

    /**
     * 獲得list 要顯示的類型。默認是history。
     *
     * @return
     */
    public void getSearchType() {
        if (mSearchTypeBeans.size() > 0) {
            SearchTypeBean searchTypeBean = mSearchTypeBeans.get(0);
            if (searchTypeBean instanceof SearchInterpriseBean) {
                mType = SearchType.INTERPRISE;
            } else if (searchTypeBean instanceof SearchDemandBean) {
                mType = SearchType.DEMAND;
            } else if (searchTypeBean instanceof SearchSupplyBean) {
                mType = SearchType.SUPPLY;
            }
        }

    }

    public void notifyDataSetChanged(ArrayList<SearchTypeBean> searchTypeBeans) {

        this.mSearchTypeBeans = null;
        this.mSearchTypeBeans = searchTypeBeans;
        getSearchType();
        super.notifyDataSetChanged();
    }

    class SupplyViewHolder {
        ImageView img;
        TextView price;
        TextView min_num;
        TextView look_num;
        TextView name;
        TextView company_name;
    }

    class DemandViewHolder {
        TextView name;
        TextView date;
        TextView look_num;
        TextView introduction;
    }

    class InterpriseViewHolder {
        TextView name;
        ImageView img;
        ImageView vip_img;
        TextView region;//地區。
        TextView Business; //主营业务。
    }
}
