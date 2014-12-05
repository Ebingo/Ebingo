package com.promote.ebingoo.home;

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
import com.promote.ebingoo.bean.HotCategory;

import java.util.ArrayList;

/**
 * 熱門市場adapter。
 * <p/>
 * Created by ACER on 2014/8/29.
 */
public class HotMarketAdapter extends BaseAdapter {

    DisplayImageOptions mOptions;
    ArrayList<HotCategory> hotCategories = new ArrayList<HotCategory>();
    private Context mContext;

    public HotMarketAdapter(Context context, ArrayList<HotCategory> hotCategories, DisplayImageOptions options) {

        this.mContext = context;
        this.hotCategories = hotCategories;
        this.mOptions = options;
    }

    @Override
    public int getCount() {
        return hotCategories.size() + 1;
    }

    @Override
    public Object getItem(int position) {

        if (position < hotCategories.size()) {
            return hotCategories.get(position);
        } else {
            return "last item";
        }


    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        if (convertView == null) {

            convertView = LayoutInflater.from(mContext).inflate(R.layout.home_market_item, null);
            viewHolder = new ViewHolder();
            viewHolder.img = (ImageView) convertView.findViewById(R.id.home_market_item_img);
            viewHolder.tv = (TextView) convertView.findViewById(R.id.home_market_item_tv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        if (position == hotCategories.size()) {
            viewHolder.tv.setText(mContext.getString(R.string.more));
            viewHolder.img.setImageDrawable(mContext.getResources().getDrawable(R.drawable.adv_more));
        } else {
            HotCategory hotCategory = hotCategories.get(position);
            if (hotCategory != null) {
                viewHolder.tv.setText(hotCategory.getName());
                ImageManager.load(hotCategory.getImage(), viewHolder.img, mOptions);
            }
        }

        return convertView;
    }


    static class ViewHolder {
        ImageView img;
        TextView tv;
    }
}
