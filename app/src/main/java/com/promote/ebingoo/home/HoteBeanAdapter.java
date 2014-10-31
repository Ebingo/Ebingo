package com.promote.ebingoo.home;

import android.app.Activity;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jch.lib.util.DisplayUtil;
import com.jch.lib.util.ImageManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.promote.ebingoo.R;
import com.promote.ebingoo.bean.HotBean;

import java.util.ArrayList;

/**
 * Created by ACER on 2014/8/29.
 */
public class HoteBeanAdapter extends BaseAdapter {

    private Activity mContext;
    private Point mPoint = null;
    /**
     * 廣告大圖的緩存機制. *
     */
    private DisplayImageOptions mOptions;
    /**
     * 圓形小圖片的緩存機制。 *
     */
    private DisplayImageOptions mCircleImageOptions;
    /**
     * 數據。
     */
    private ArrayList<HotBean> mHotBeans;
    private TextView mainhote1gettiteltv;
    private TextView mainhoteget1subtitletv;
    private ImageView mainhot1getimg;
    /**
     * listview的margin *
     */
    private int itemMargin = 0;

    /**
     * @param context
     * @param options
     * @param circlOptions
     */
    public HoteBeanAdapter(Activity context, DisplayImageOptions options, DisplayImageOptions circlOptions, ArrayList<HotBean> buyBeans, Point point) {
        this.mContext = context;
        this.mOptions = options;
        this.mCircleImageOptions = circlOptions;
        this.mHotBeans = buyBeans;
        this.mPoint = point;
        itemMargin = (int) context.getResources().getDimension(R.dimen.main_subtitle_marg_left) * 2;
    }

    @Override
    public int getCount() {
        return mHotBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return mHotBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (position == 0) {

            ViewHolder1 viewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.hote_buy_supply_top_item, null);
                convertView.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
                viewHolder = new ViewHolder1();
                viewHolder.img = (ImageView) convertView.findViewById(R.id.hote_buy_supplly_top_img);
                convertView.setTag(viewHolder);
            } else {
                Object OViewHolder = convertView.getTag();
                if (OViewHolder instanceof ViewHolder1) {
                    viewHolder = (ViewHolder1) convertView.getTag();
                } else {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.hote_buy_supply_top_item, null);
                    viewHolder = new ViewHolder1();
                    viewHolder.img = (ImageView) convertView.findViewById(R.id.hote_buy_supplly_top_img);
                    convertView.setTag(viewHolder);
                }

            }
            DisplayUtil.resizeViewByScreenWidth(viewHolder.img, mPoint.x, mPoint.y, itemMargin, mContext);//设置img的大小
            ImageManager.load(mHotBeans.get(position).getImage(), viewHolder.img, mOptions);

        } else {
            ViewHolder2 viewHolder2 = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.hote_buy_supply_item, null);
                convertView.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
                viewHolder2 = new ViewHolder2();
                viewHolder2.img = (ImageView) convertView.findViewById(R.id.main_hot_1get_img);
                viewHolder2.nameTv = (TextView) convertView.findViewById(R.id.main_hote_1get_titel_tv);
                viewHolder2.subNameTv = (TextView) convertView.findViewById(R.id.main_hote_get_1subtitle_tv);
                convertView.setTag(viewHolder2);
            } else {
                Object OViewHolder = convertView.getTag();
                if (OViewHolder instanceof ViewHolder2) {
                    viewHolder2 = (ViewHolder2) convertView.getTag();
                } else {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.hote_buy_supply_item, null);
                    viewHolder2 = new ViewHolder2();
                    viewHolder2.img = (ImageView) convertView.findViewById(R.id.main_hot_1get_img);
                    viewHolder2.nameTv = (TextView) convertView.findViewById(R.id.main_hote_1get_titel_tv);
                    viewHolder2.subNameTv = (TextView) convertView.findViewById(R.id.main_hote_get_1subtitle_tv);
                    convertView.setTag(viewHolder2);
                }


            }

            HotBean buyBean = mHotBeans.get(position);
            if (buyBean != null) {
                ImageManager.load(buyBean.getImage(), viewHolder2.img, mCircleImageOptions);
                viewHolder2.nameTv.setText(buyBean.getTitle());
                viewHolder2.subNameTv.setText(buyBean.getSub_title());
            }


        }

        return convertView;
    }

    private class ViewHolder1 {

        ImageView img;
    }

    private class ViewHolder2 {
        TextView nameTv;
        TextView subNameTv;
        ImageView img;

    }

}
