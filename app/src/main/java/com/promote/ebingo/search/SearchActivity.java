package com.promote.ebingo.search;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
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

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.DataInput;
import java.util.ArrayList;

public class SearchActivity extends Activity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, PullToRefreshView.OnFooterRefreshListener, View.OnFocusChangeListener{
    /** 當前搜索類型，默認為顯示歷史记录。 **/
    private SearchType mCurSearchType = SearchType.HISTORY;
    private SearchCategoryPop mCategoryPop = null;
    /**搜索list的显示内容类型，默认是历史记录。**/
    private SearchType mSearchType = SearchType.HISTORY;
    private ImageView searchbackbtn;
    private TextView searchcancelbtn;
    private LinearLayout searchbtn;
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

    private SearchListAdapter mAdapter = null;
    private ArrayList<SearchTypeBean> mSearchTypeBeans =new ArrayList<SearchTypeBean>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initialize();

    }


    private void initialize() {

        searchcancelbtn = (TextView) findViewById(R.id.search_cancel_btn);
        searchbtn = (LinearLayout) findViewById(R.id.search_btn);
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

        mCategoryPop = new SearchCategoryPop(this, this);
        mCategoryPop.setOnDismissListener(new PopDismissLSNER());

        mAdapter = new SearchListAdapter(getApplicationContext(), mCurSearchType, mSearchTypeBeans);
        searchlv.setAdapter(mAdapter);
        mRefreshView.setDownRefreshable(false);
        mRefreshView.setOnFooterRefreshListener(this);

        searchbackbtn.setOnClickListener(this);
        searchcategrycb.setOnCheckedChangeListener(this);
        searchbaret.setOnClickListener(this);
        searchbaret.setOnFocusChangeListener(this);
        searchbtn.setOnClickListener(this);
        searchclearbtn.setOnClickListener(this);

        displayHistory();

    }

    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == SEARCh_HISTORY){
                if (mSearchTypeBeans.size() == 0){
                    hidKey();
                    noData(getString(R.string.no_history));
                }else {
                    hasData(true);
                    hidKey();
                }

                mAdapter.notifyDataSetChanged(mSearchTypeBeans);

            }

        }
    };

    /**
     * 没有数据。
     */
    private void noData(String msg){
        searchcontentll.setVisibility(View.GONE);
        searchnohistorytv.setVisibility(View.VISIBLE);
        searchnohistorytv.setText(msg);

    }

    /**
     *
     * @param btnVisible 清空按钮是否显示。
     */
    private void hasData(boolean btnVisible){
        searchcontentll.setVisibility(View.VISIBLE);
        searchnohistorytv.setVisibility(View.GONE);
        if (btnVisible){
            searchclearbtn.setVisibility(View.VISIBLE);
        }else {
            searchclearbtn.setVisibility(View.GONE);
        }
    }

    /**
     * 顯示關鍵字項。
     */
    private void showkey(String key){
        searchresultkeyll.setVisibility(View.VISIBLE);

    }

    /**
     * 隱藏關鍵字項。
     */
    private void hidKey(){
        searchresultkeyll.setVisibility(View.GONE);
    }


    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id){

            case  R.id.search_back_btn:{

                onBackPressed();
                this.finish();

                break;
            }

            case R.id.search_clear_btn:{        //取消

                SearchDao dao = new SearchDao(getApplicationContext());
                dao.clearHistory();
                displayHistory();
                break;
            }

            case R.id.category_left_item_buy:{
                searchcategrycb.setText(((TextView)v).getText());
                mCategoryPop.dismiss();
                break;
            }

            case R.id.search_bar_et:{       //搜索框被点击，显示搜索记录。

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

            case R.id.search_btn:{      //搜索按鈕。

                String key = searchbaret.getText().toString();
                searchbaret.clearFocus();
                mRefreshView.setFootViewVisibility(View.VISIBLE);
                mRefreshView.setUpRefreshable(true);

                if (key != null && !key.equals("")){
                    saveHistory(key);
                }

                mCurSearchType = getSearchType(searchcategrycb.getText().toString());
                if (mCurSearchType == SearchType.SUPPLY){
                    getSupplyInfoList(0, key);
                }else if(mCurSearchType == SearchType.DEMAND){
                    getDemandInfoList(0, key);
                }else{
                    getCompanyList(0, key);
                }
                
                showkey(key);       //显示关键字项。

                break;
            }

            case R.id.category_left_item_supply: {
                searchcategrycb.setText(((TextView) v).getText());
                mCategoryPop.dismiss();

                break;
            }default:{

                break;
            }

        }

    }

    /**
     * 獲得當前搜索類型。
     *
     * @param type
     * @return
     */
    private SearchType getSearchType(String type){

        if (getString(R.string.interprise).equals(type)){
            mCurSearchType = SearchType.INTERPRISE;
        }else if (getString(R.string.buy).equals(type)){
            mCurSearchType = SearchType.DEMAND;
        }else if (getString(R.string.supply).equals(type)){
            mCurSearchType = SearchType.SUPPLY;
        }

        return mCurSearchType;
    }

    /**
     * 保存歷史記錄.
     *
     * @param history
     */
    private void saveHistory(final String history){

        new Thread(new Runnable() {
            @Override
            public void run() {
                SearchDao searchDao = new SearchDao(getApplicationContext());
                searchDao.addHistory(history);
            }
        }).start();

    }

    private static final int SEARCh_HISTORY = 1;

    /**
     * 显示搜索记录.
     */
    private void displayHistory(){
        mCurSearchType = SearchType.HISTORY;
        mRefreshView.setUpRefreshable(false);
        mRefreshView.setFootViewVisibility(View.GONE);
        new Thread(new Runnable() {
            @Override
            public void run() {

                SearchDao searchDao = new SearchDao(getApplicationContext());
                mSearchTypeBeans.clear();
                mSearchTypeBeans.addAll(searchDao.getHistorys());
                mHandler.sendEmptyMessage(SEARCh_HISTORY);
            }
        }).start();
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        int id = buttonView.getId();
        if (id == R.id.search_categry_cb){
            if (isChecked){
                mCategoryPop.showAsDropDown(buttonView, 0, DisplayUtil.px2dip(getApplicationContext(), 3));
            }
        }

    }

    @Override
    public void onFooterRefresh(PullToRefreshView view) {

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

            if (hasFocus){
                displayHistory();
            }

    }


    /**
     * 当类别选择框消失时，类别箭头状态改变。
     */
    private class PopDismissLSNER implements PopupWindow.OnDismissListener{

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
    public void getDemandInfoList(final int lastId, String keyword){

        String url = HttpConstant.getDemandInfoList;
        EbingoRequestParmater parmater = new EbingoRequestParmater(getApplicationContext());
        parmater.put("lastId", lastId);
        parmater.put("pagesize", 20);       //每页显示20条。
        parmater.put("condition", appendKeyworld(keyword));
        final ProgressDialog dialog = DialogUtil.waitingDialog(SearchActivity.this);

        HttpUtil.post(url, parmater, new JsonHttpResponseHandler("UTF-8"){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                ArrayList<SearchDemandBean> searchDemandBeans = SearchDemandBeanTools.getSearchDemands(response.toString());
                if (lastId == 0) {           //如果第一次请求，即不是加载更多时。
                    mSearchTypeBeans.clear();
                }
                mCurSearchType = SearchType.DEMAND;
                if (searchDemandBeans != null && searchDemandBeans.size() != 0){
                    mSearchTypeBeans.addAll(searchDemandBeans);
                    noData(getString(R.string.no_search_data));
                }else {
                    hasData(false);
                }

                mAdapter.notifyDataSetChanged(mSearchTypeBeans);
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
        });

    }


    /**
     *
     * 從網絡获取供应信息列表。
     *
     * @param lastId
     * @param keyword
     */
    public void getSupplyInfoList(final int lastId, String keyword){


        String url = HttpConstant.getSupplyInfoList;
        EbingoRequestParmater parmater = new EbingoRequestParmater(getApplicationContext());
        parmater.put("lastId", lastId);
        parmater.put("pagesize", 20);       //每页显示20条。
        parmater.put("condition", appendKeyworld(keyword));
        final ProgressDialog dialog = DialogUtil.waitingDialog(SearchActivity.this);

        HttpUtil.post(url, parmater, new JsonHttpResponseHandler("UTF-8") {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                ArrayList<SearchSupplyBean> searchSupplyBeans = SearchSupplyBeanTools.getSearchSupplyBeans(response.toString());
                if (lastId == 0){           //如果第一次请求，即不是加载更多时。
                    mSearchTypeBeans.clear();
                }
                mCurSearchType = SearchType.SUPPLY;
                if (searchSupplyBeans != null && searchSupplyBeans.size() != 0) {
                    mSearchTypeBeans.addAll(searchSupplyBeans);
                    noData(getString(R.string.no_search_data));
                } else {
                    hasData(false);
                }
                mAdapter.notifyDataSetChanged(mSearchTypeBeans);
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
        });

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
        parmater.put("lastId", lastId);
        parmater.put("pagesize", 20);       //每页显示20条。
        parmater.put("condition", appendKeyworld(keyword));
        final ProgressDialog dialog = DialogUtil.waitingDialog(SearchActivity.this);

        HttpUtil.post(url, parmater, new JsonHttpResponseHandler("UTF-8") {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                ArrayList<SearchInterpriseBean> searchInterpriseBeans = SearchInterpriseBeanTools.getSearchTypeBeans(response.toString());
                if (lastId == 0) {           //如果第一次请求，即不是加载更多时。
                    mSearchTypeBeans.clear();
                }
                mCurSearchType = SearchType.INTERPRISE;
                if (searchInterpriseBeans != null && searchInterpriseBeans.size() != 0) {
                    mSearchTypeBeans.addAll(searchInterpriseBeans);
                    noData(getString(R.string.no_search_data));
                } else {
                    hasData(false);
                }

                mAdapter.notifyDataSetChanged(mSearchTypeBeans);
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
        });

    }

    /**
     * 獲取數據失敗，顯示無數據。
     */
    private void getDataFailed(){
        mSearchTypeBeans.clear();
        mAdapter.notifyDataSetChanged(mSearchTypeBeans);
        noData(getString(R.string.no_search_data));
    }


    /**
     * 拼接赛选条件参数。
     * @param keyword
     * @return
     */
    private String appendKeyworld(String keyword){
        StringBuffer sb = new StringBuffer("{\"keywords\":\"");
        sb.append(keyword);
        sb.append("\"}");
        return sb.toString();
    }

}
