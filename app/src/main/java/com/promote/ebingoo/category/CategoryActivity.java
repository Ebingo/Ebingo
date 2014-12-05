package com.promote.ebingoo.category;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jch.lib.util.DialogUtil;
import com.jch.lib.util.DisplayUtil;
import com.jch.lib.util.HttpUtil;
import com.jch.lib.view.RefreshMoreListView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.promote.ebingoo.InformationActivity.BuyInfoActivity;
import com.promote.ebingoo.InformationActivity.ProductInfoActivity;
import com.promote.ebingoo.R;
import com.promote.ebingoo.application.HttpConstant;
import com.promote.ebingoo.bean.SearchDemandBean;
import com.promote.ebingoo.bean.SearchDemandBeanTools;
import com.promote.ebingoo.bean.SearchSupplyBean;
import com.promote.ebingoo.bean.SearchSupplyBeanTools;
import com.promote.ebingoo.bean.SearchTypeBean;
import com.promote.ebingoo.impl.EbingoRequestParmater;
import com.promote.ebingoo.util.LogCat;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;


enum CategoryType {

    DEMAND, SUPPLY;
}

enum CategoryRankType {

    TIME, PRICE, LOOKNUM;
}

/**
 * 行业分类列表。
 */
public class CategoryActivity extends Activity implements View.OnClickListener, RefreshMoreListView.XOnItemClickListener, RefreshMoreListView.LoadMoreListener {
    public static final String PARENT_ID = "parent_id";
    public static final String ARG_ID = "category_id";
    public static final String ARG_NAME = "name";
    /**
     * 分页最大加载数。 *
     */
    private static final int PAGESIZE = 10;
    private CheckBox categoryleftcb;
    private CheckBox categoryrightcb;
    private ImageView commonbackbtn;
    private TextView commontitletv;
    private int parent_id = -1;
    private int category_id = -1;
    private CategoryListAdapter mListAdapter = null;
    /**
     * 类别选择弹出window. *
     */
    private CategoryTypePop mTypePop;
    /**
     * 排序選擇 pop window.*
     */
    private CategoryRankPopWin mRankPop;
    /**
     * 當前行業分類類型。默認為供應 *
     */
    private CategoryType mCurType = CategoryType.SUPPLY;
    /**
     * 当前排序类型. 默認為瀏覽量
     */
    private CategoryRankType mCurRankType = CategoryRankType.LOOKNUM;
    /**
     * 供應。 *
     */
    private ArrayList<SearchTypeBean> mCategoryBean = new ArrayList<SearchTypeBean>();
    private RefreshMoreListView refreshMoreListView;
    private TextView nodatatv;
    private Handler handler = new Handler();
    /**
     * 初始化供应列表
     */
    private Runnable initSupplyInfoList = new Runnable() {
        @Override
        public void run() {
            getSupplyInfoList(0);
        }
    };
    /**
     * 初始化求购列表
     */
    private Runnable initDemandInfoList = new Runnable() {
        @Override
        public void run() {
            getDemandInfoList(0);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        initialize();
    }

    private void initialize() {

        categoryleftcb = (CheckBox) findViewById(R.id.category_left_cb);
        categoryrightcb = (CheckBox) findViewById(R.id.category_right_cb);
        refreshMoreListView = (RefreshMoreListView) findViewById(R.id.category_lv);
        refreshMoreListView.setLoadMoreListener(this);
        commonbackbtn = (ImageView) findViewById(R.id.common_back_btn);
        commontitletv = (TextView) findViewById(R.id.common_title_tv);
        nodatatv = (TextView) findViewById(R.id.nodata_tv);

        mTypePop = new CategoryTypePop(getApplicationContext(), this);
        mTypePop.setOnDismissListener(new TypePopDismissLSNER());
        mRankPop = new CategoryRankPopWin(getApplicationContext(), this);
        mRankPop.setOnDismissListener(new RankPopDismissLSNER());

        Intent intent = getIntent();
        String category_name = intent.getStringExtra(ARG_NAME);
        category_id = intent.getIntExtra(ARG_ID, -1);
        parent_id = intent.getIntExtra(PARENT_ID, -1);
        commonbackbtn.setOnClickListener(this);
        commontitletv.setText(category_name + "分类");
        categoryleftcb.setOnCheckedChangeListener(new CategoryTypeCheckedCL());
        categoryrightcb.setOnCheckedChangeListener(new CategoryRankCheckedCL());

        mListAdapter = new CategoryListAdapter(getApplicationContext(), mCategoryBean);
        refreshMoreListView.setAdapter(mListAdapter);
        refreshMoreListView.setXOnItemClickListener(this);
        refreshMoreListView.setmPageSize(PAGESIZE);
        //TODO 访问默认数据
        if (!HttpUtil.isNetworkConnected(getApplicationContext())) {
            netNotAvalible();
            return;
        }
        if (mCurType == CategoryType.SUPPLY) {//延迟加载数据，让UI界面先出来
            handler.postDelayed(initSupplyInfoList, 100);
        } else {
            handler.postDelayed(initDemandInfoList, 100);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(initSupplyInfoList);
        handler.removeCallbacks(initDemandInfoList);
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id) {
            case R.id.common_back_btn: {
//                onBackPressed();
                this.finish();
                break;

            }

            case R.id.category_item_buy: {       //求购
                categoryleftcb.setText(((TextView) v).getText());
                mCurType = CategoryType.DEMAND;
                if (mCurRankType == CategoryRankType.PRICE) {       //将按价格排序改成按浏览量排序.
                    categoryrightcb.setText(getResources().getString(R.string.look_num));
                    mCurRankType = CategoryRankType.LOOKNUM;
                }

                mTypePop.dismiss();
                refreshMoreListView.setmCanLoadMoreAble(true);
                if (!HttpUtil.isNetworkConnected(getApplicationContext())) {
                    netNotAvalible();
                    return;
                }
                getDemandInfoList(0);

                break;
            }
            case R.id.category_item_supply: {    //供應
                categoryleftcb.setText(((TextView) v).getText());
                mCurType = CategoryType.SUPPLY;
                if (mCurRankType == CategoryRankType.TIME) {
                    categoryrightcb.setText(getResources().getString(R.string.look_num));      //将按时间排序改成按价格排序
                    mCurRankType = CategoryRankType.LOOKNUM;
                }

                mTypePop.dismiss();
                refreshMoreListView.setmCanLoadMoreAble(true);
                if (!HttpUtil.isNetworkConnected(getApplicationContext())) {
                    netNotAvalible();
                    return;
                }
                getSupplyInfoList(0);
                break;
            }

            case R.id.category_right_item_look: {        //浏览量
                categoryrightcb.setText(((TextView) v).getText());
                mCurRankType = CategoryRankType.LOOKNUM;
                mRankPop.dismiss();
                refreshMoreListView.setmCanLoadMoreAble(true);
                if (!HttpUtil.isNetworkConnected(getApplicationContext())) {
                    netNotAvalible();
                    return;
                }
                if (mCurType == CategoryType.DEMAND) {
                    getDemandInfoList(0);
                } else {
                    getSupplyInfoList(0);
                }


                break;
            }
            case R.id.category_right_item_price: {      //价格, 时间

                String sortStr = ((TextView) v).getText().toString();
                categoryrightcb.setText(sortStr);
                refreshMoreListView.setmCanLoadMoreAble(true);
                if (getResources().getString(R.string.time).equals(sortStr)) {     //按时间排序。
                    mCurRankType = CategoryRankType.TIME;
                } else {
                    mCurRankType = CategoryRankType.PRICE;
                }
                mRankPop.dismiss();
                if (!HttpUtil.isNetworkConnected(getApplicationContext())) {
                    netNotAvalible();
                    return;
                }
                if (mCurType == CategoryType.DEMAND) {
                    getDemandInfoList(0);
                } else {
                    getSupplyInfoList(0);
                }

                break;
            }

            default: {

            }
        }
    }

    /**
     *
     */
    private void hasData() {

        refreshMoreListView.setVisibility(View.VISIBLE);
        nodatatv.setVisibility(View.GONE);
    }

    /**
     * 网络没有数据。
     */
    private void noData() {
        refreshMoreListView.setVisibility(View.GONE);
        nodatatv.setText(getString(R.string.no_data));
        nodatatv.setVisibility(View.VISIBLE);
    }

    /**
     * 网络连接失败。
     */
    private void netNotAvalible() {
        refreshMoreListView.setVisibility(View.VISIBLE);
        nodatatv.setVisibility(View.VISIBLE);
        nodatatv.setText(getString(R.string.no_network));
    }

    /**
     * 显示数据.
     *
     * @param searchTypeBeans
     * @param lastId
     */
    private void displayData(ArrayList<? extends SearchTypeBean> searchTypeBeans, int lastId) {

        if (lastId == 0) {   //第一次加载清空数据
            mCategoryBean.clear();
        }
        if (searchTypeBeans == null || searchTypeBeans.size() == 0) {       //加载数据失败或没有加载没有数据
            if (lastId == 0) {       //首次没有加载数据.
                noData();
            } else {

            }
            refreshMoreListView.loadMoreOver(0);
        } else {     //加载数据，显示.
            hasData();
            int loadCount = searchTypeBeans.size();

            refreshMoreListView.loadMoreOver(PAGESIZE);
            mCategoryBean.addAll(searchTypeBeans);
        }
        mListAdapter.notifyDataSetChanged();

    }

    @Override
    public void onLoadmore() {
        int lastId = mCategoryBean.size();
        if (mCurType == CategoryType.DEMAND) {
            getDemandInfoList(lastId);
        } else {
            getSupplyInfoList(lastId);
        }
    }

    @Override
    public void xonItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position >= mCategoryBean.size())
            return;
        SearchTypeBean searchTypeBean = mCategoryBean.get(position);
        if (searchTypeBean instanceof SearchDemandBean) {

            Intent intent = new Intent(CategoryActivity.this, BuyInfoActivity.class);
            intent.putExtra(BuyInfoActivity.DEMAND_ID, (searchTypeBean).getId());
            startActivity(intent);

        } else if (searchTypeBean instanceof SearchSupplyBean) {

            Intent intent = new Intent(CategoryActivity.this, ProductInfoActivity.class);
            intent.putExtra(ProductInfoActivity.ARG_ID, searchTypeBean.getId());
            startActivity(intent);

        }
    }

    /**
     * 獲取popwin的偏移量。
     *
     * @param percentOffset 屏幕宽度的百分比。
     * @return
     */
    private int getPopOffsetX(float percentOffset) {

        Point point = new Point();

        DisplayUtil.getSize(getWindowManager().getDefaultDisplay(), point);

        int offset = (int) (point.x * percentOffset);
        LogCat.d("screenSize:" + offset);
        return offset;

    }

    private String getRank() {

        String rankType = null;
        if (mCurRankType == CategoryRankType.LOOKNUM) {
            rankType = "hot";
        } else if (mCurRankType == CategoryRankType.TIME) {
            rankType = "time";
        } else {
            rankType = "price";
        }
        return rankType;
    }

    /**
     * 從網絡獲取求购信息列表.
     *
     * @param lastId
     */
    public void getDemandInfoList(final int lastId) {

        String url = HttpConstant.getDemandInfoList;
        EbingoRequestParmater parmater = new EbingoRequestParmater(getApplicationContext());
        parmater.put("lastid", lastId);
        parmater.put("pagesize", PAGESIZE);       //每页显示20条。
        parmater.put("father_category_id", parent_id);

        try {
            parmater.put("condition", URLEncoder.encode(appendKeyworld(category_id, getRank()), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        final ProgressDialog dialog = DialogUtil.waitingDialog(CategoryActivity.this, false);
        if (lastId == 0)
            dialog.show();

        HttpUtil.post(url, parmater, new JsonHttpResponseHandler("UTF-8") {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                ArrayList<SearchDemandBean> searchDemandBeans = SearchDemandBeanTools.getSearchDemands(response.toString());
                displayData(searchDemandBeans, lastId);
                if (lastId == 0)
                    dialog.dismiss();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                noData();
                if (lastId == 0)
                    dialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                noData();
                if (lastId == 0)
                    dialog.dismiss();
            }
        });

    }

    /**
     * 從網絡获取供应信息列表。
     *
     * @param lastId
     */
    public void getSupplyInfoList(final int lastId) {


        String url = HttpConstant.getSupplyInfoList;
        EbingoRequestParmater parmater = new EbingoRequestParmater(getApplicationContext());
        parmater.put("lastid", lastId);
        parmater.put("pagesize", PAGESIZE);       //每页显示10条。
        parmater.put("father_category_id", parent_id);
        try {
            parmater.put("condition", URLEncoder.encode(appendKeyworld(category_id, getRank()), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        final ProgressDialog dialog = DialogUtil.waitingDialog(CategoryActivity.this, false);

        if (lastId == 0)
            dialog.show();

        HttpUtil.post(url, parmater, new JsonHttpResponseHandler("UTF-8") {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                ArrayList<SearchSupplyBean> searchSupplyBeans = SearchSupplyBeanTools.getSearchSupplyBeans(response.toString());

                displayData(searchSupplyBeans, lastId);
                if (lastId == 0)
                    dialog.dismiss();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
//                getDataFailed();
                noData();
                if (lastId == 0)
                    dialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
//                getDataFailed();
                noData();
                if (lastId == 0)
                    dialog.dismiss();
            }

        });

    }

    /**
     * @param category_id
     * @param rankType    time, hote.
     * @return
     */
    private String appendKeyworld(int category_id, String rankType) {
        StringBuffer sb = new StringBuffer("{\"sort\":\"");
        sb.append(rankType);
        sb.append("\",\"category_id\":");
        sb.append(category_id);
        sb.append("}");
        return sb.toString();
    }

    /**
     * 行業種類選擇監聽.
     */
    private class CategoryTypeCheckedCL implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {

                mTypePop.showAsDropDown(categoryleftcb, DisplayUtil.getCentWidthByView(buttonView) - getResources().getDimensionPixelSize(R.dimen.cate_pop_widht) / 2, DisplayUtil.dip2px(getApplicationContext(), -5));
            }

        }
    }

    //price 价格筛选

    /**
     * 行业排列顺序选择监听。
     */
    private class CategoryRankCheckedCL implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {

                if (mCurType == CategoryType.DEMAND) {
                    mRankPop.setPriceTimeSortType(getResources().getString(R.string.time));
                } else {
                    mRankPop.setPriceTimeSortType(getResources().getString(R.string.price));
                }
                mRankPop.showAsDropDown(categoryrightcb, DisplayUtil.getCentWidthByView(buttonView) - getResources().getDimensionPixelSize(R.dimen.cate_pop_widht) / 2, DisplayUtil.dip2px(getApplicationContext(), -5));
            }
        }
    }

    /**
     * 当类别选择框消失时，类别箭头状态改变。
     */
    private class TypePopDismissLSNER implements PopupWindow.OnDismissListener {

        @Override
        public void onDismiss() {

            categoryleftcb.setChecked(false);
        }
    }

    /**
     * 当排序选择框消失时，类别箭头状态改变。
     */
    private class RankPopDismissLSNER implements PopupWindow.OnDismissListener {

        @Override
        public void onDismiss() {
            categoryrightcb.setChecked(false);
        }
    }


}