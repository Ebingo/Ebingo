package com.promote.ebingo.search;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.promote.ebingo.R;

/**
 * Created by ACER on 2014/8/26.
 */
public class SearchCategoryPop extends PopupWindow {


    private ViewGroup mContentView;
    private TextView categoryleftitembuy;
    private TextView categoryleftiteminterpises;
    private TextView categoryleftitemsupply;

    public SearchCategoryPop(Activity context, View.OnClickListener onClickListener) {

        super(context);

        mContentView = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.category_left_pop, null);

        categoryleftitembuy = (TextView) mContentView.findViewById(R.id.category_left_item_buy);
        categoryleftiteminterpises = (TextView) mContentView.findViewById(R.id.category_left_item_interpises);
        categoryleftitemsupply = (TextView) mContentView.findViewById(R.id.category_left_item_supply);

        categoryleftitembuy.setOnClickListener(onClickListener);
        categoryleftiteminterpises.setOnClickListener(onClickListener);
        categoryleftitemsupply.setOnClickListener(onClickListener);
        // 设置SelectPicPopupWindow的View
        this.setContentView(mContentView);
        // 设置SelectPicPopupWindow的View
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);

        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        this.setFocusable(true);

        // 设置SelectPicPopupWindow弹出窗体动画效果
//        this.setAnimationStyle(R.style.AnimBottom);
        // 设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(new BitmapDrawable());


    }

}
