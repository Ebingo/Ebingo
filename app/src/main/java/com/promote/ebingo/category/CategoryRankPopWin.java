package com.promote.ebingo.category;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.promote.ebingo.R;

/**
 * Created by ACER on 2014/9/5.
 */
public class CategoryRankPopWin extends PopupWindow {

    private TextView categoryrightitembuy;
    private TextView categoryrightiteminterpises;

    private ViewGroup mContentView;
    private LinearLayout contentLl = null;

    public CategoryRankPopWin(Context context, View.OnClickListener listener) {

        super(context);

        mContentView = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.category_right_pop, null);

        categoryrightitembuy = (TextView) mContentView.findViewById(R.id.category_right_item_look);
        categoryrightiteminterpises = (TextView) mContentView.findViewById(R.id.category_right_item_price);
        contentLl = (LinearLayout) mContentView.findViewById(R.id.cate_right_pop_content);
        categoryrightitembuy.setOnClickListener(listener);
        categoryrightiteminterpises.setOnClickListener(listener);

        // 设置SelectPicPopupWindow的View
        this.setContentView(mContentView);
        // 设置SelectPicPopupWindow的View
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);

        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        this.setFocusable(true);

//        设置SelectPicPopupWindow弹出窗体动画效果
//        this.setAnimationStyle(R.style.AnimBottom);
//        设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(new BitmapDrawable());
        this.setAnimationStyle(R.style.popupAnimation);
        this.update();

    }

    /**
     * 获取显示内容的width.
     *
     * @return
     */
    public int getContentSizeWidth() {

        return contentLl.getWidth();
    }

    /**
     * 设置按时间或价格排序.
     *
     * @param typeStr
     */
    public void setPriceTimeSortType(String typeStr) {

        categoryrightiteminterpises.setText(typeStr);
    }


}
