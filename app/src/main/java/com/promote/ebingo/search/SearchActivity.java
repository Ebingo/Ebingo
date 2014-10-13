package com.promote.ebingo.search;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jch.lib.util.DialogUtil;
import com.jch.lib.util.DisplayUtil;
import com.jch.lib.util.HttpUtil;
import com.jch.lib.view.PullToRefreshView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.promote.ebingo.InformationActivity.BuyInfoActivity;
import com.promote.ebingo.InformationActivity.InterpriseInfoActivity;
import com.promote.ebingo.InformationActivity.ProductInfoActivity;
import com.promote.ebingo.R;
import com.promote.ebingo.application.HttpConstant;
import com.promote.ebingo.bean.SearchDemandBean;
import com.promote.ebingo.bean.SearchDemandBeanTools;
import com.promote.ebingo.bean.SearchHistoryBean;
import com.promote.ebingo.bean.SearchInterpriseBean;
import com.promote.ebingo.bean.SearchInterpriseBeanTools;
import com.promote.ebingo.bean.SearchSupplyBean;
import com.promote.ebingo.bean.SearchSupplyBeanTools;
import com.promote.ebingo.bean.SearchTypeBean;
import com.promote.ebingo.impl.EbingoRequestParmater;
import com.promote.ebingo.impl.SearchDao;
import com.promote.ebingo.util.ContextUtil;
import com.promote.ebingo.util.LogCat;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class SearchActivity extends Activity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, PullToRefreshView.OnFooterRefreshListener, View.OnFocusChangeListener, AdapterView.OnItemClickListener, TextWatcher {
    /**
     * 當前搜索類型，默認為顯示歷史记录。 *
     */
    private SearchType mCurSearchType = SearchType.HISTORY;
    private SearchCategoryPop mCategoryPop = null;
    /**
     * 搜索list的显示内容类型，默认是历史记录。*
     */
    private SearchType mSearchType = SearchType.HISTORY;
    private ImageView searchbackbtn;
    private ImageButton searchBtnIB;
    private LinearLayout searchClearLl;
    private CheckBox searchcategrycb;
    private EditText searchbaret;
    private RelativeLayout searchheadcenterll;
    private TextView searchkeytv;
    private LinearLayout searchresultkeyll;
    private PullToRefreshView mRefreshView;
    private ListView searchlv;
    private Button searchclearbtn;
    private LinearLayout searchcontentll;
    private TextView searchnohistorytv;
    /**
     * 搜索内容清空按钮。 *
     */
    private ImageButton mSearchClearIb;

    private SearchHistoryListAdapter mHistoryAdapter = null;
    private SearchResultAdapter mResultAdatper = null;
    private ArrayList<SearchTypeBean> mSearchTypeBeans = new ArrayList<SearchTypeBean>();
    private ArrayList<SearchHistoryBean> mHistoryBeans = new ArrayList<SearchHistoryBean>();
    private LinearLayout searchcontentresultll;
    private ListView searchresultlv;
    private LinearLayout searchcontenthistoryll;
    /**
     * 线程锁.*
     */
    private Object objLock = new Object();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initialize();
    }


    private void initialize() {

        searchBtnIB = (ImageButton) findViewById(R.id.search_btn_ib);
        searchClearLl = (LinearLayout) findViewById(R.id.search_clear_ll);
        searchheadcenterll = (RelativeLayout) findViewById(R.id.search_head_center_ll);
        searchcontentll = (LinearLayout) findViewById(R.id.search_content_ll);
        searchnohistorytv = (TextView) findViewById(R.id.search_no_history_tv);
        searchbackbtn = (ImageView) findViewById(R.id.search_back_btn);
        searchcategrycb = (CheckBox) findViewById(R.id.search_categry_cb);
        searchbaret = (EditText) findViewById(R.id.search_bar_et);
        searchkeytv = (TextView) findViewById(R.id.search_key_tv);
        searchresultkeyll = (LinearLayout) findViewById(R.id.search_result_key_ll);
        mRefreshView = (PullToRefreshView) findViewById(R.id.search_freshview);
        searchlv = (ListView) findViewById(R.id.search_lv);
        searchclearbtn = (Button) findViewById(R.id.search_clear_btn);

        searchcontentresultll = (LinearLayout) findViewById(R.id.search_content_result_ll);
        searchresultlv = (ListView) findViewById(R.id.search_result_lv);
        searchcontenthistoryll = (LinearLayout) findViewById(R.id.search_content_history_ll);
        mSearchClearIb = (ImageButton) findViewById(R.id.search_clear_ib);


        mCategoryPop = new SearchCategoryPop(this, this);
        mCategoryPop.setOnDismissListener(new PopDismissLSNER());
        //历史数据.
        mHistoryBeans = new ArrayList<SearchHistoryBean>();
        mHistoryAdapter = new SearchHistoryListAdapter(getApplicationContext(), mHistoryBeans);
        searchlv.setAdapter(mHistoryAdapter);
        searchlv.setOnItemClickListener(this);
        //结果数据.
        mSearchTypeBeans = new ArrayList<SearchTypeBean>();
        mResultAdatper = new SearchResultAdapter(getApplicationContext(), getCurSearchType(searchcategrycb.getText().toString()), mSearchTypeBeans);
        searchresultlv.setAdapter(mResultAdatper);
        searchresultlv.setOnItemClickListener(this);

        mRefreshView.setDownRefreshable(false);
        mRefreshView.setOnFooterRefreshListener(this);

        searchbackbtn.setOnClickListener(this);
        searchcategrycb.setOnCheckedChangeListener(this);
        searchbaret.setOnClickListener(this);
        searchbaret.setOnFocusChangeListener(this);
        searchbaret.addTextChangedListener(this);
//        searchClearLl.setOnClickListener(this);
        searchclearbtn.setOnClickListener(this);
        searchBtnIB.setOnClickListener(this);
        mSearchClearIb.setOnClickListener(this);

        LogCat.i("init display history.");
        displayHistory();

    }

    /**
     * 显示历史记录数据,隐藏搜索数据.
     */
    public void showHistoryData() {

        searchcontenthistoryll.setVisibility(View.VISIBLE);
        searchcontentresultll.setVisibility(View.GONE);
    }

    /**
     * 显示搜索数据,隐藏历史记录数据.
     */

    public void showSearchData() {
        searchcontenthistoryll.setVisibility(View.GONE);
        searchcontentresultll.setVisibility(View.VISIBLE);
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == SEARCh_HISTORY) {
                if (mHistoryBeans.size() == 0) {
                    hidKey();
                    noData(getString(R.string.no_history));
                } else {
                    hasData(true);
                    hidKey();
                }
                showHistoryData();
                LogCat.i("searchActivity handler history beansize：" + mHistoryBeans.size());
                mHistoryAdapter.notifyDataSetChanged(mHistoryBeans);

            }

        }
    };

    /**
     * 没有数据。
     */
    private void noData(String msg) {
        searchcontentll.setVisibility(View.GONE);
        searchnohistorytv.setVisibility(View.VISIBLE);
        searchnohistorytv.setText(msg);

    }

    /**
     * @param btnVisible 清空按钮是否显示。
     */
    private void hasData(boolean btnVisible) {
        searchcontentll.setVisibility(View.VISIBLE);
        searchnohistorytv.setVisibility(View.GONE);
        if (btnVisible) {
            searchclearbtn.setVisibility(View.VISIBLE);
        } else {
            searchclearbtn.setVisibility(View.GONE);
        }
    }

    /**
     * 顯示關鍵字項。
     */
    private void showkey(String key) {
        searchresultkeyll.setVisibility(View.VISIBLE);
        searchkeytv.setText(key);
    }


    /**
     * 隱藏關鍵字項。
     */
    private void hidKey() {
        searchresultkeyll.setVisibility(View.GONE);
    }


    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id) {

            case R.id.search_back_btn: {

                onBackPressed();
                this.finish();

                break;
            }

            case R.id.search_clear_btn: {        //取消

                SearchDao dao = new SearchDao(getApplicationContext());
                dao.clearHistory();
                LogCat.i("clear search history.");
                displayHistory();
                break;
            }

            case R.id.category_left_item_buy: {
                searchcategrycb.setText(((TextView) v).getText());
                mCategoryPop.dismiss();
                break;
            }

            case R.id.search_bar_et: {       //搜索框被点击，显示搜索记录。

//                if (mCurSearchType != SearchType.HISTORY){      //如果當前沒有顯示歷史記錄，顯示歷史記錄。
//                    displayHistory();
//                }

                break;

            }

            case R.id.category_left_item_interpises: {
                searchcategrycb.setText(((TextView) v).getText());
                mCategoryPop.dismiss();
                break;
            }

            case R.id.search_btn_ib: {      //搜索按鈕。
                onSearch();
                break;
            }

            case R.id.category_left_item_supply: {
                searchcategrycb.setText(((TextView) v).getText());
                mCategoryPop.dismiss();

                break;
            }
            case R.id.search_clear_ib: {
                searchbaret.setText("");
                break;
            }
            default: {

                break;
            }


        }

    }

    /**
     * 搜索。
     */
    public void onSearch() {
        mHistoryBeans.clear();  //清空历史数据.
        showSearchData();
        mRefreshView.setUpRefreshable(true);

        String key = searchbaret.getText().toString();
        getCurSearchType(searchcategrycb.getText().toString());      //设置当前搜索类型.
        searchbaret.clearFocus();
//                mRefreshView.setVisibility(View.VISIBLE);
        if (key != null && !key.equals("")) {
            saveHistory(key);
        }

        if (mCurSearchType == SearchType.SUPPLY) {
            getSupplyInfoList(0, key);
        } else if (mCurSearchType == SearchType.DEMAND) {
            getDemandInfoList(0, key);
        } else {
            getCompanyList(0, key);
        }
//                mRefreshView.setUpRefreshable(true);
//                mRefreshView.setFootViewVisibility(View.VISIBLE);
        showkey(key);       //显示关键字项。
    }

    /**
     * 獲得當前搜索類型。
     *
     * @param type
     * @return
     */
    private SearchType getCurSearchType(String type) {

        if (getString(R.string.interprise).equals(type)) {
            mCurSearchType = SearchType.INTERPRISE;
        } else if (getString(R.string.buy).equals(type)) {
            mCurSearchType = SearchType.DEMAND;
        } else if (getString(R.string.supply).equals(type)) {
            mCurSearchType = SearchType.SUPPLY;
        }

        return mCurSearchType;
    }

    /**
     * 根据checkbox中的内容判断当前搜索类型.尽在存储和显示历史记录时使用.
     *
     * @return
     */
    private int getmCurSearchType() {

        String searchType = searchcategrycb.getText().toString();

        if (getString(R.string.interprise).equals(searchType)) {

            return SearchType.INTERPRISE.getValue();
        } else if (getString(R.string.buy).equals(searchType)) {

            return SearchType.DEMAND.getValue();
        } else if (getString(R.string.supply).equals(searchType)) {

            return SearchType.SUPPLY.getValue();
        }

        return 0;
    }

    /**
     * 保存歷史記錄.
     *
     * @param history
     */
    private void saveHistory(final String history) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                SearchDao searchDao = new SearchDao(getApplicationContext());
                searchDao.addHistory(history, getmCurSearchType());
            }
        }).start();

    }

    private static final int SEARCh_HISTORY = 1;

    /**
     * 显示搜索记录.
     */
    private void displayHistory() {

        mCurSearchType = SearchType.HISTORY;
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (objLock) {
                    SearchDao searchDao = new SearchDao(getApplicationContext());
                    mHistoryBeans.clear();
                    mHistoryBeans.addAll(searchDao.getHistorys(getmCurSearchType(), searchbaret.getText().toString()));
                    mHandler.sendEmptyMessage(SEARCh_HISTORY);
                    LogCat.d("searchActivity thread history beansize：" + mHistoryBeans.size());
                }
            }
        }).start();
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        int id = buttonView.getId();
        if (id == R.id.search_categry_cb) {
            if (isChecked) {
                buttonView.getWidth();
                DisplayUtil.getCentWidthByView(buttonView);
                mCategoryPop.showAsDropDown(buttonView, DisplayUtil.getCentWidthByView(buttonView) - DisplayUtil.dip2px(getApplicationContext(), 50), DisplayUtil.px2dip(getApplicationContext(), 3));
            } else {
                LogCat.i("check changed.");
                displayHistory();
            }
        }

    }

    @Override
    public void onFooterRefresh(PullToRefreshView view) {
        String key = searchbaret.getText().toString();

        int search_id = mSearchTypeBeans.size() - 1;

        if (mCurSearchType == SearchType.SUPPLY) {
            getSupplyInfoList(search_id, key);
        } else if (mCurSearchType == SearchType.DEMAND) {
            getDemandInfoList(search_id, key);
        } else {
            getCompanyList(search_id, key);
        }

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        if (hasFocus) {
            mSearchTypeBeans.clear();       //清空搜索结果.
            LogCat.i("focus changed.");
            displayHistory();
//                mRefreshView.setVisibility(View.GONE);
        } else {     //隐藏键盘.
            DisplayUtil.hideSystemKeyBoard(SearchActivity.this, v);

        }

    }

    //當前不同的搜索類型類型，做不同的操作。
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        switch (mCurSearchType) {
            case HISTORY: {          //當前顯示搜索記錄。

                SearchHistoryBean historyBean = (SearchHistoryBean) mHistoryBeans.get(position);
                searchbaret.setText(historyBean.getHistory());
                onSearch();
                break;
            }

            case DEMAND: {       //当前显示求购信息.

                SearchDemandBean demandBean = (SearchDemandBean) mSearchTypeBeans.get(position);
                Intent intent = new Intent(SearchActivity.this, BuyInfoActivity.class);
                intent.putExtra("id", demandBean.getId());
                startActivity(intent);

                break;
            }

            case SUPPLY: {

                SearchSupplyBean supplyBean = (SearchSupplyBean) mSearchTypeBeans.get(position);
                Intent intent = new Intent(SearchActivity.this, ProductInfoActivity.class);
                intent.putExtra(ProductInfoActivity.ARG_ID, supplyBean.getId());
                startActivity(intent);

                break;
            }

            case INTERPRISE: {

                SearchInterpriseBean interpriseBean = (SearchInterpriseBean) mSearchTypeBeans.get(position);
                Intent intent = new Intent(SearchActivity.this, InterpriseInfoActivity.class);
                intent.putExtra("id", interpriseBean.getId());
                startActivity(intent);

                break;
            }

            default: {

            }
        }

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {


    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {


    }

    @Override
    public void afterTextChanged(Editable s) {

        if (s.toString() == null || s.toString().equals("")) {
            mSearchClearIb.setVisibility(View.INVISIBLE);
        } else {
            mSearchClearIb.setVisibility(View.VISIBLE);
        }

        if (searchbaret.isFocused()) {
            displayHistory();
        }
    }

    /**
     * 当类别选择框消失时，类别箭头状态改变。
     */
    private class PopDismissLSNER implements PopupWindow.OnDismissListener {

        @Override
        public void onDismiss() {
            searchcategrycb.setChecked(false);
        }
    }

    /**
     * 從網絡獲取求购信息列表.
     *
     * @param lastId
     */
    public void getDemandInfoList(final int lastId, String keyword) {

        String url = HttpConstant.getDemandInfoList;
        EbingoRequestParmater parmater = new EbingoRequestParmater(getApplicationContext());
        parmater.put("lastid", lastId);
        parmater.put("pagesize", 20);       //每页显示20条。
        try {
            parmater.put("condition", URLEncoder.encode(appendKeyworld(keyword), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        final ProgressDialog dialog = DialogUtil.waitingDialog(SearchActivity.this);

        HttpUtil.post(url, parmater, new JsonHttpResponseHandler("UTF-8") {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                ArrayList<SearchDemandBean> searchDemandBeans = SearchDemandBeanTools.getSearchDemands(response.toString());

                mCurSearchType = SearchType.DEMAND;
                displayData(searchDemandBeans, lastId);

                dialog.dismiss();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                getDataFailed();
                dialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                getDataFailed();
                dialog.dismiss();
            }

            @Override
            public void onFinish() {
                mRefreshView.onFooterRefreshComplete();
                super.onFinish();
            }
        });

    }


    /**
     * 從網絡获取供应信息列表。
     *
     * @param lastId
     * @param keyword
     */
    public void getSupplyInfoList(final int lastId, String keyword) {


        String url = HttpConstant.getSupplyInfoList;
        EbingoRequestParmater parmater = new EbingoRequestParmater(getApplicationContext());
        parmater.put("lastid", lastId);
        parmater.put("pagesize", 20);       //每页显示20条。
        try {
            parmater.put("condition", URLEncoder.encode(appendKeyworld(keyword), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        final ProgressDialog dialog = DialogUtil.waitingDialog(SearchActivity.this);

        HttpUtil.post(url, parmater, new JsonHttpResponseHandler("UTF-8") {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                ArrayList<SearchSupplyBean> searchSupplyBeans = SearchSupplyBeanTools.getSearchSupplyBeans(response.toString());
                mCurSearchType = SearchType.SUPPLY;
                displayData(searchSupplyBeans, lastId);
//                mResultAdatper.notifyDataSetChanged(mSearchTypeBeans);
                dialog.dismiss();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                getDataFailed();
                dialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                getDataFailed();
                dialog.dismiss();
            }

            @Override
            public void onFinish() {
                mRefreshView.onFooterRefreshComplete();
                super.onFinish();
            }
        });

    }

    /**
     * 展示数据.
     *
     * @param searchTypeBeans
     * @param lastId
     */
    private void displayData(ArrayList<? extends SearchTypeBean> searchTypeBeans, int lastId) {
        if (lastId == 0) {   //第一次加载清空数据
            mSearchTypeBeans.clear();
        }
        if (searchTypeBeans == null || searchTypeBeans.size() == 0) {       //加载数据失败或没有加载没有数据
            if (lastId == 0) {       //首次没有加载数据.
//                        noData(getString(R.string.no_search_data));
                noData(getString(R.string.no_search_data));
            } else {
                mRefreshView.setUpRefreshable(false);
                loadDataComplete();
            }
        } else {     //加载数据，显示.
            hasData(false);
            mSearchTypeBeans.addAll(searchTypeBeans);
            mResultAdatper.notifyDataSetChanged();
        }

    }

    private void loadDataComplete() {
        mRefreshView.setUpRefreshable(false);
        ContextUtil.toast(getResources().getString(R.string.load_data_complete));
    }

    /**
     * 從網絡获取企业列表。
     *
     * @param lastId
     * @param keyword
     */
    public void getCompanyList(final int lastId, String keyword) {

        String url = HttpConstant.getCompanyList;
        EbingoRequestParmater parmater = new EbingoRequestParmater(getApplicationContext());
        parmater.put("lastid", lastId);
        parmater.put("pagesize", 20);       //每页显示20条。
        try {
            parmater.put("condition", URLEncoder.encode(appendKeyworld(keyword), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        final ProgressDialog dialog = DialogUtil.waitingDialog(SearchActivity.this);

        HttpUtil.post(url, parmater, new JsonHttpResponseHandler("UTF-8") {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                ArrayList<SearchInterpriseBean> searchInterpriseBeans = SearchInterpriseBeanTools.getSearchTypeBeans(response.toString());
                mSearchType = SearchType.INTERPRISE;
                displayData(searchInterpriseBeans, lastId);
                dialog.dismiss();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                mSearchTypeBeans.clear();
                getDataFailed();
                dialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                getDataFailed();
                dialog.dismiss();
            }

            @Override
            public void onFinish() {
                mRefreshView.onFooterRefreshComplete();
                super.onFinish();
            }
        });

    }

    /**
     * 獲取數據失敗，顯示無數據。
     */
    private void getDataFailed() {
        mSearchTypeBeans.clear();
        mResultAdatper.notifyDataSetChanged(mSearchTypeBeans);
        noData(getString(R.string.no_search_data));
    }


    /**
     * 拼接赛选条件参数。
     *
     * @param keyword
     * @return
     */
    private String appendKeyworld(String keyword) {
        StringBuffer sb = new StringBuffer("{\"keywords\":\"");
        sb.append(keyword);
        sb.append("\"}");
        return sb.toString();
    }

}
