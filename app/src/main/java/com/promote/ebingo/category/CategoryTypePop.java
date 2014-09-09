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
 * @author jch.
 *         <p/>
 *         行情分類window。
 */
public class CategoryTypePop extends PopupWindow {

    private TextView categoryitembuy;
    private TextView categoryitemsupply;
    private ViewGroup contentView = null;
    private LinearLayout contentLl = null;

    public CategoryTypePop(Context context, View.OnClickListener listener) {
        super(context);

        contentView = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.category_pop, null);

        categoryitembuy = (TextView) contentView.findViewById(R.id.category_item_buy);
        categoryitemsupply = (TextView) contentView.findViewById(R.id.category_item_supply);
        contentLl = (LinearLayout) contentView.findViewById(R.id.cate_type_pop_content_ll);

        categoryitembuy.setOnClickListener(listener);
        categoryitemsupply.setOnClickListener(listener);
        this.setContentView(contentView);

        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setBackgroundDrawable(new BitmapDrawable());
    }

    public int getContentWidth() {

        return contentLl.getWidth();
    }

}
