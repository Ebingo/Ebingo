package com.promote.ebingo.InformationActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jch.lib.util.ImageManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.promote.ebingo.R;
import com.promote.ebingo.bean.SearchDemandBean;
import com.promote.ebingo.bean.SearchSupplyBean;
import com.promote.ebingo.bean.SearchTypeBean;

import java.util.ArrayList;

/**
 * Created by ACER on 2014/9/9.
 *
 *
 *
 */
public class CategoryListAdapter extends BaseAdapter {

    /** 上下文。 **/
    private Context mContenxt;

    private ArrayList<SearchTypeBean> mCategoryBeans = null;
    /**
     * 是否应该执行getview（）。 *
     */
    private DisplayImageOptions mOptions;


    public CategoryListAdapter(Context context, ArrayList<SearchTypeBean> categoryBeans){

        this.mContenxt = context;
        if (categoryBeans != null){
            mCategoryBeans = categoryBeans;
        }else {
            try {
                throw new Exception("adapter data should not be null.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 使用DisplayImageOptions.Builder()创建DisplayImageOptions
        mOptions = new DisplayImageOptions.Builder()
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .showImageForEmptyUri(R.drawable.loading)
                .showImageOnLoading(R.drawable.loading)
                .showImageOnFail(R.drawable.loading)
                .cacheInMemory(true).cacheOnDisc(true).build();

    }

    @Override
    public int getCount() {
        return mCategoryBeans.size();
}

    @Override
    public Object getItem(int position) {
        return mCategoryBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        SearchTypeBean categroyItemBean = mCategoryBeans.get(position);
        if (categroyItemBean != null && categroyItemBean instanceof SearchSupplyBean){

            SupplyViewHolder supplyViewHolder = null;
            if (convertView == null || !(convertView.getTag() instanceof SupplyViewHolder)){

                convertView = LayoutInflater.from(mContenxt).inflate(R.layout.category_item_layout, null);
                supplyViewHolder = new SupplyViewHolder();

                supplyViewHolder.img = (ImageView) convertView.findViewById(R.id.category_lv_item_img);
                supplyViewHolder.nameTv = (TextView) convertView.findViewById(R.id.category_lv_item_name_tv);
                supplyViewHolder.priceTv = (TextView) convertView.findViewById(R.id.category_lv_item_price_tv);
                supplyViewHolder.startTv = (TextView) convertView.findViewById(R.id.category_lv_item_min_num_tv);
                supplyViewHolder.lookNumTv = (TextView) convertView.findViewById(R.id.catgory_lv_look_num_tv);
                supplyViewHolder.providerTv = (TextView) convertView.findViewById(R.id.category_lv_item_privater_tv);

                convertView.setTag(supplyViewHolder);

            }else {
                supplyViewHolder = (SupplyViewHolder)convertView.getTag();
            }

            SearchSupplyBean supplyBean = (SearchSupplyBean)categroyItemBean;
            supplyViewHolder.lookNumTv.setText(Integer.toString(supplyBean.getRead_num()));
            supplyViewHolder.startTv.setText(supplyBean.getMin_supply_num());
            supplyViewHolder.priceTv.setText(supplyBean.getPrice());
            supplyViewHolder.nameTv.setText(supplyBean.getName());
            supplyViewHolder.providerTv.setText(supplyBean.getCompany());
            //TODO img
            ImageManager.load(supplyBean.getImage(), supplyViewHolder.img, mOptions);

        }else if (categroyItemBean != null && categroyItemBean instanceof SearchDemandBean){

            DemandViewHolder demandViewHolder = null;
            if (convertView == null || !(convertView.getTag() instanceof DemandViewHolder)){

                convertView = LayoutInflater.from(mContenxt).inflate(R.layout.category_demand_item_layout, null);
                demandViewHolder = new DemandViewHolder();

                demandViewHolder.timeTv = (TextView) convertView.findViewById(R.id.cate_demand_item_time_tv);
                demandViewHolder.nameTv = (TextView) convertView.findViewById(R.id.cate_demand_item_name_tv);
                demandViewHolder.numTv = (TextView) convertView.findViewById(R.id.cate_demand_item_start_num_tv);
                demandViewHolder.lookTv = (TextView) convertView.findViewById(R.id.cate_demand_item_look_num_tv);
                demandViewHolder.describeTv = (TextView) convertView.findViewById(R.id.cate_demand_ite_deiscribe_tv);

                convertView.setTag(demandViewHolder);
            }else {
                demandViewHolder = (DemandViewHolder) convertView.getTag();
            }

            SearchDemandBean demandBean = (SearchDemandBean)categroyItemBean;
            demandViewHolder.timeTv.setText(demandBean.getDate());
            demandViewHolder.nameTv.setText(demandBean.getName());
            demandViewHolder.numTv.setText(demandBean.getBuy_num());
            demandViewHolder.lookTv.setText(demandBean.getRead_num());
            demandViewHolder.describeTv.setText(demandBean.getIntroduction());
        }

        return convertView;
    }


    static class SupplyViewHolder{

        ImageView img;
        TextView nameTv;
        TextView priceTv;
        TextView startTv;
        TextView providerTv;
        TextView lookNumTv;
    }

    static class DemandViewHolder{

        TextView nameTv;
        TextView timeTv;
        TextView numTv;
        TextView describeTv;
        TextView lookTv;
    }

}
