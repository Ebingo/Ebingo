package com.promote.ebingo.category;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.promote.ebingo.R;

/**
 * @author jch.
 *
 * 行情分類window。
 *
 */
public class CategoryTypePop extends PopupWindow {

    private TextView categoryitembuy;
    private TextView categoryitemsupply;

    public CategoryTypePop(Context context, View.OnClickListener listener) {
        super(context);

        ViewGroup contentView = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.category_pop, null);

        categoryitembuy = (TextView) contentView.findViewById(R.id.category_item_buy);
        categoryitemsupply = (TextView) contentView.findViewById(R.id.category_item_supply);

        categoryitembuy.setOnClickListener(listener);
        categoryitemsupply.setOnClickListener(listener);
        this.setContentView(contentView);

        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setBackgroundDrawable(new BitmapDrawable());
    }

}
