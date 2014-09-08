package com.promote.ebingo.category;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jch.lib.util.DialogUtil;
import com.jch.lib.util.DisplayUtil;
import com.jch.lib.util.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.promote.ebingo.R;
import com.promote.ebingo.application.HttpConstant;
import com.promote.ebingo.bean.SearchDemandBean;
import com.promote.ebingo.bean.SearchDemandBeanTools;
import com.promote.ebingo.bean.SearchSupplyBean;
import com.promote.ebingo.bean.SearchSupplyBeanTools;
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
public class CategoryActivity extends Activity implements View.OnClickListener {

    public static final String ARG_ID = "category_id";
    public static final String ARG_NAME = "name";
    private CheckBox categoryleftcb;
    private CheckBox categoryrightcb;
    private ListView categorylv;
    private ImageView commonbackbtn;
    private TextView commontitletv;
    private int category_id = -1;
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
    private ArrayList<SearchSupplyBean> mSupplyBeans = new ArrayList<SearchSupplyBean>();
    /**
     * 求購。 *
     */
    private ArrayList<SearchDemandBean> mDemandBeans = new ArrayList<SearchDemandBean>();


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

    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id) {
            case R.id.common_back_btn: {

                onBackPressed();
                this.finish();
                break;

            }

            case R.id.category_item_buy: {       //求购
                categoryleftcb.setText(((TextView)v).getText());
                mCurType = CategoryType.DEMAND;
                getDemandInfoList(0);

                break;
            }
            case R.id.category_item_supply: {    //供應
                categoryleftcb.setText(((TextView)v).getText());
                mCurType = CategoryType.SUPPLY;
                getSupplyInfoList(0);
                break;
            }

            case R.id.category_right_item_look: {        //浏览量
                categoryrightcb.setText(((TextView)v).getText());
                mCurRankType = CategoryRankType.LOOKNUM;
                if (mCurType == CategoryType.DEMAND){
                    getDemandInfoList(0);
                }else {
                    getSupplyInfoList(0);
                }


                break;
            }
            case R.id.category_right_item_price: {      //价格
                categoryrightcb.setText(((TextView)v).getText());
                mCurRankType = CategoryRankType.PRICE;
                if (mCurType == CategoryType.DEMAND){
                    getDemandInfoList(0);
                }else {
                    getSupplyInfoList(0);
                }

                break;
            }

            default: {

            }
        }

    }

    /**
     * 行業種類選擇監聽.
     */
    private class CategoryTypeCheckedCL implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {

                mTypePop.showAsDropDown(categoryleftcb, getPopOffsetX(0.25f) - getResources().getDimensionPixelSize(R.dimen.cate_pop_widht) /2, DisplayUtil.dip2px(getApplicationContext(), -5));
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

                mRankPop.showAsDropDown(categoryrightcb, getPopOffsetX(0.25f) - getResources().getDimensionPixelSize(R.dimen.cate_pop_widht) /2 , DisplayUtil.dip2px(getApplicationContext(), -5));
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
        } else {
            rankType = "time";
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
        parmater.put("pagesize", 20);       //每页显示20条。
        parmater.put("content", appendKeyworld(category_id, getRank()));

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
                if (lastId == 0) {           //如果第一次请求，即不是加载更多时。
                    mDemandBeans.clear();
                }
                if (searchDemandBeans == null || searchDemandBeans.size() == 0) {


//                    noData(getString(R.string.no_search_data));
                } else {
//                    mSearchTypeBeans.addAll(searchDemandBeans);
//                    hasData(false);
                }

//                mAdapter.notifyDataSetChanged(mSearchTypeBeans);
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
        });

    }


    /**
     * 從網絡获取供应信息列表。
     *
     * @param lastId
     * @param keyword
     */
    public void getSupplyInfoList(final int lastId) {


        String url = HttpConstant.getSupplyInfoList;
        EbingoRequestParmater parmater = new EbingoRequestParmater(getApplicationContext());
        parmater.put("lastid", lastId);
        parmater.put("pagesize", 20);       //每页显示20条。
        parmater.put("content", appendKeyworld(category_id, getRank()));
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
                if (lastId == 0) {           //如果第一次请求，即不是加载更多时。
//                    mSearchTypeBeans.clear();
                }
                if (searchSupplyBeans == null || searchSupplyBeans.size() == 0) {

//                    noData(getString(R.string.no_search_data));
                } else {
                    mSupplyBeans.addAll(searchSupplyBeans);
//                    hasData(false);
                }
//                mAdapter.notifyDataSetChanged(mSearchTypeBeans);
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
        sb.append("\",\"company_id\":");
        sb.append(category_id);
        sb.append("}");
        return sb.toString();
    }


}

enum CategoryType {

    DEMAND, SUPPLY;
}

enum CategoryRankType {

    PRICE, LOOKNUM;
}