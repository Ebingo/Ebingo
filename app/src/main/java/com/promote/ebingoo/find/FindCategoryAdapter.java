package com.promote.ebingoo.find;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jch.lib.util.ImageManager;
import com.jch.lib.view.SlideGridViewBaseAdapter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.promote.ebingoo.R;
import com.promote.ebingoo.bean.CategoryBeen;
import com.promote.ebingoo.util.ContextUtil;

import java.util.ArrayList;

/**
 * Created by ACER on 2014/11/10.
 */
public class FindCategoryAdapter extends SlideGridViewBaseAdapter {

    private ArrayList<CategoryBeen> datas = new ArrayList<CategoryBeen>();

    private Context context;

    private DisplayImageOptions mImgOptions;

    public FindCategoryAdapter(Context context) {
        this.context = context;
        mImgOptions = ContextUtil.getCircleImgOptions();
    }

    public void notifyDataSetChanged(ArrayList<CategoryBeen> categoryBeens) {
        datas.clear();
        datas.addAll(categoryBeens);
        notifyDataSetChanged();
    }

    @Override
    public int getSubItemCountByPosition(int itemPosition) {
        return datas.get(itemPosition).getSubCategorys().size();
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.find_cate_item, null);
        ImageView iv = (ImageView) view.findViewById(R.id.find_grid_item_img);
        TextView tv = (TextView) view.findViewById(R.id.find_grid_item_tv);
        CategoryBeen categoryBeen = datas.get(position);
        ImageManager.load(categoryBeen.getImage(), iv, mImgOptions);
        tv.setText(categoryBeen.getName());

        return view;
    }

    @Override
    public View getSubItemView(int itemPosition, int subItemPosition) {

        View view = LayoutInflater.from(context).inflate(R.layout.find_subcate_item, null);
        TextView tv = (TextView) view.findViewById(R.id.find_subcate_item_tv);
        tv.setText(datas.get(itemPosition).getSubCategorys().get(subItemPosition).getName());

        return view;
    }
}
