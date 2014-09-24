package com.promote.ebingo.category;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jch.lib.util.DialogUtil;
import com.jch.lib.util.DisplayUtil;
import com.jch.lib.util.HttpUtil;
import com.jch.lib.view.PullToRefreshView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.promote.ebingo.InformationActivity.BuyInfoActivity;
import com.promote.ebingo.InformationActivity.ProductInfoActivity;
import com.promote.ebingo.R;
import com.promote.ebingo.application.HttpConstant;
import com.promote.ebingo.bean.SearchDemandBean;
import com.promote.ebingo.bean.SearchDemandBeanTools;
import com.promote.ebingo.bean.SearchSupplyBean;
import com.promote.ebingo.bean.SearchSupplyBeanTools;
import com.promote.ebingo.bean.SearchTypeBean;
import com.promote.ebingo.impl.EbingoRequestParmater;
import com.promote.ebingo.util.LogCat;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;


/**
 * 行业分类列表。
 */
public class CategoryActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener, PullToRefreshView.OnFooterRefreshListener {

    public static final String ARG_ID = "category_id";
    public static final String ARG_NAME = "name";
    private CheckBox categoryleftcb;
    private CheckBox categoryrightcb;
    private ListView categorylv;
    private ImageView commonbackbtn;
    private TextView commontitletv;
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
     * 当前排序类型. 默認為瀏覽量*
     */
    private CategoryRankType mCurRankType = CategoryRankType.LOOKNUM;
    /**
     * 供應。 *
     */
    private ArrayList<SearchTypeBean> mCategoryBean = new ArrayList<SearchTypeBean>();
    private PullToRefreshView categoryrefreshview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        initialize();
    }


    private void initialize() {

        categoryleftcb = (CheckBox) findViewById(R.id.category_left_cb);
        categoryrightcb = (CheckBox) findViewById(R.id.category_right_cb);
        categorylv = (ListView) findViewById(R.id.category_lv);
        commonbackbtn = (ImageView) findViewById(R.id.common_back_btn);
        commontitletv = (TextView) findViewById(R.id.common_title_tv);
        categoryrefreshview = (PullToRefreshView) findViewById(R.id.category_refresh_view);

        mTypePop = new CategoryTypePop(getApplicationContext(), this);
        mTypePop.setOnDismissListener(new TypePopDismissLSNER());
        mRankPop = new CategoryRankPopWin(getApplicationContext(), this);
        mRankPop.setOnDismissListener(new RankPopDismissLSNER());

        Intent intent = getIntent();
        String category_name = intent.getStringExtra(ARG_NAME);
        category_id = intent.getIntExtra(ARG_ID, -1);
        commonbackbtn.setOnClickListener(this);
        commontitletv.setText(category_name + "分类");
        categoryleftcb.setOnCheckedChangeListener(new CategoryTypeCheckedCL());
        categoryrightcb.setOnCheckedChangeListener(new CategoryRankCheckedCL());

        categoryrefreshview.setUpRefreshable(true);
        categoryrefreshview.setDownRefreshable(false);
        categoryrefreshview.setOnFooterRefreshListener(this);

        mListAdapter = new CategoryListAdapter(getApplicationContext(), mCategoryBean);
        categorylv.setAdapter(mListAdapter);
        categorylv.setOnItemClickListener(this);
        //TODO 访问默认数据
        if (mCurType == CategoryType.SUPPLY) {
            getSupplyInfoList(0);
        } else {
            getDemandInfoList(0);
        }

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
                getSupplyInfoList(0);
                break;
            }

            case R.id.category_right_item_look: {        //浏览量
                categoryrightcb.setText(((TextView) v).getText());
                mCurRankType = CategoryRankType.LOOKNUM;
                mRankPop.dismiss();
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

                if (getResources().getString(R.string.time).equals(sortStr)) {     //按时间排序。
                    mCurRankType = CategoryRankType.TIME;
                } else {
                    mCurRankType = CategoryRankType.PRICE;
                }
                mRankPop.dismiss();
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

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

    @Override
    public void onFooterRefresh(PullToRefreshView view) {

        int lastId = mCategoryBean.size();
        if (mCurType == CategoryType.DEMAND) {
            getDemandInfoList(lastId);
        } else {
            getSupplyInfoList(lastId);
        }

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

    //price 价格筛选

    /**
     * 從網絡獲取求购信息列表.
     *
     * @param lastId
     */
    public void getDemandInfoList(final int lastId) {

        String url = HttpConstant.getDemandInfoList;
        EbingoRequestParmater parmater = new EbingoRequestParmater(getApplicationContext());
        parmater.put("lastid", lastId);
        parmater.put("pagesize", 20);       //每页显示20条。

        try {
            parmater.put("condition", URLEncoder.encode(appendKeyworld(category_id, getRank()), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        final ProgressDialog dialog = DialogUtil.waitingDialog(CategoryActivity.this);

        HttpUtil.post(url, parmater, new JsonHttpResponseHandler("UTF-8") {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                ArrayList<SearchDemandBean> searchDemandBeans = SearchDemandBeanTools.getSearchDemands(response.toString());
                if (lastId == 0) {   //第一次加载清空数据
                    mCategoryBean.clear();
                }
                if (searchDemandBeans == null || searchDemandBeans.size() == 0) {       //加载数据失败或没有加载没有数据
                    if (lastId == 0) {       //首次没有加载数据.
//                        noData(getString(R.string.no_search_data));
                    }
                } else {     //加载数据，显示.
                    mCategoryBean.addAll(searchDemandBeans);
                    mListAdapter.notifyDataSetChanged();
                }

                dialog.dismiss();


                mListAdapter.notifyDataSetChanged();

                dialog.dismiss();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
//                getDataFailed();
                dialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
//                getDataFailed();
                dialog.dismiss();
            }

            @Override
            public void onFinish() {
                categoryrefreshview.onFooterRefreshComplete();
                super.onFinish();
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
        parmater.put("pagesize", 20);       //每页显示20条。
        try {
            parmater.put("condition", URLEncoder.encode(appendKeyworld(category_id, getRank()), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        final ProgressDialog dialog = DialogUtil.waitingDialog(CategoryActivity.this);

        HttpUtil.post(url, parmater, new JsonHttpResponseHandler("UTF-8") {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                ArrayList<SearchSupplyBean> searchSupplyBeans = SearchSupplyBeanTools.getSearchSupplyBeans(response.toString());


                if (lastId == 0) {   //第一次加载清空数据
                    mCategoryBean.clear();
                }
                if (searchSupplyBeans == null || searchSupplyBeans.size() == 0) {       //加载数据失败或没有加载没有数据
                    if (lastId == 0) {       //首次没有加载数据.
//                        noData(getString(R.string.no_search_data));
                    }
                } else {     //加载数据，显示.
                    mCategoryBean.addAll(searchSupplyBeans);
                    mListAdapter.notifyDataSetChanged();
                }

                dialog.dismiss();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
//                getDataFailed();
                dialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
//                getDataFailed();
                dialog.dismiss();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                categoryrefreshview.onFooterRefreshComplete();
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


}

enum CategoryType {

    DEMAND, SUPPLY;
}

enum CategoryRankType {

    TIME, PRICE, LOOKNUM;
}