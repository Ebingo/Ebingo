package com.promote.ebingoo.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jch.lib.view.ScrollGridView;
import com.promote.ebingoo.R;
import com.promote.ebingoo.search.SearchKeyAdapter;

import java.util.ArrayList;

/**
 * 热门关键词。
 * <p/>
 * Created by ACER on 2014/11/13.
 */
public class SearchHotKeyLayout extends LinearLayout implements AdapterView.OnItemClickListener {

    private ViewGroup contentView;
    private ScrollGridView searchhotgv;
    private TextView nohotkeytv;
    private LinearLayout loadinghotkeyll;
    private SearchKeyAdapter mAdater;

    public void setHotKeyItemClickListener(SearchHotkeyOnItemClickListener hotKeyItemClickListener) {

        this.mHotKeyItemClickListener = hotKeyItemClickListener;

    }

    private SearchHotkeyOnItemClickListener mHotKeyItemClickListener;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        String hotKey = mAdater.getItem(position);

        mHotKeyItemClickListener.onHotKeyItemClickListener(hotKey, position, id);
    }

    public interface SearchHotkeyOnItemClickListener {

        public void onHotKeyItemClickListener(String hotkey, int position, long id);
    }

    public SearchHotKeyLayout(Context context) {
        super(context);
        initView(context);
    }

    public SearchHotKeyLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }


    private void initView(Context context) {
        contentView = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.search_hotkey_layout, null);
        searchhotgv = (ScrollGridView) contentView.findViewById(R.id.search_hot_gv);
        searchhotgv.setSelector(new BitmapDrawable());
        nohotkeytv = (TextView) contentView.findViewById(R.id.no_hotkey_tv);
        loadinghotkeyll = (LinearLayout) contentView.findViewById(R.id.loading_hotkey_ll);
        mAdater = new SearchKeyAdapter(context);
        searchhotgv.setAdapter(mAdater);
        searchhotgv.setOnItemClickListener(this);
        loadingData();
        addView(contentView);
    }

    /**
     * 数据加载中。
     */
    public void loadingData() {
        searchhotgv.setVisibility(GONE);
        nohotkeytv.setVisibility(GONE);
        loadinghotkeyll.setVisibility(VISIBLE);
    }

    public void noData() {
        searchhotgv.setVisibility(GONE);
        nohotkeytv.setVisibility(VISIBLE);
        loadinghotkeyll.setVisibility(GONE);
    }

    public void hasData() {

        searchhotgv.setVisibility(VISIBLE);
        nohotkeytv.setVisibility(GONE);
        loadinghotkeyll.setVisibility(GONE);
    }

    /**
     * 向view中添加关键词。当有/无数据时分别表示。
     *
     * @param hotekeys
     */
    public void addData(ArrayList<String> hotekeys) {
        hasData();

        if (hotekeys == null || hotekeys.size() == 0) {
            noData();
        } else {
            hasData();
        }
        mAdater.nodifyOnDataChanged(hotekeys);
    }


//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        int measureWidth = measureWidth(widthMeasureSpec);
//        int measureHeight = measureHeight(heightMeasureSpec);
//        // 计算自定义的ViewGroup中所有子控件的大小
//        measureChildren(widthMeasureSpec, heightMeasureSpec);
//        // 设置自定义的控件MyViewGroup的大小
//        setMeasuredDimension(measureWidth, measureHeight);
//    }
//
//    private int measureWidth(int pWidthMeasureSpec) {
//        int result = 0;
//        int widthMode = MeasureSpec.getMode(pWidthMeasureSpec);// 得到模式
//        int widthSize = MeasureSpec.getSize(pWidthMeasureSpec);// 得到尺寸
//
//        switch (widthMode) {
//            /**
//             * mode共有三种情况，取值分别为MeasureSpec.UNSPECIFIED, MeasureSpec.EXACTLY,
//             * MeasureSpec.AT_MOST。
//             *
//             *
//             * MeasureSpec.EXACTLY是精确尺寸，
//             * 当我们将控件的layout_width或layout_height指定为具体数值时如andorid
//             * :layout_width="50dip"，或者为FILL_PARENT是，都是控件大小已经确定的情况，都是精确尺寸。
//             *
//             *
//             * MeasureSpec.AT_MOST是最大尺寸，
//             * 当控件的layout_width或layout_height指定为WRAP_CONTENT时
//             * ，控件大小一般随着控件的子空间或内容进行变化，此时控件尺寸只要不超过父控件允许的最大尺寸即可
//             * 。因此，此时的mode是AT_MOST，size给出了父控件允许的最大尺寸。
//             *
//             *
//             * MeasureSpec.UNSPECIFIED是未指定尺寸，这种情况不多，一般都是父控件是AdapterView，
//             * 通过measure方法传入的模式。
//             */
//            case MeasureSpec.AT_MOST:
//            case MeasureSpec.EXACTLY:
//                result = widthSize;
//                break;
//        }
//        return result;
//    }
//
//    private int measureHeight(int pHeightMeasureSpec) {
//        int result = 0;
//
//        int heightMode = MeasureSpec.getMode(pHeightMeasureSpec);
//        int heightSize = MeasureSpec.getSize(pHeightMeasureSpec);
//
//        switch (heightMode) {
//            case MeasureSpec.AT_MOST:
//            case MeasureSpec.EXACTLY:
//                result = heightSize;
//                break;
//        }
//        return result;
//    }
//
//    @Override
//    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        int childBtm = 0;
//        // 获取所有的子View的个数
//        final int childCount = getChildCount();
//        for (int i = 0; i < childCount; i++) {
//            final View childView = getChildAt(i);
//            final int childWidth = childView.getMeasuredWidth();
//            final int childHeight = childView.getMeasuredHeight();
//            childView.layout(l, childBtm, childWidth, childBtm + childHeight);
//            // 下一个VIew的左边左边+一个
//            childBtm += childHeight;
//        }
//    }

}
