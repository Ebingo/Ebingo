package com.promote.ebingo.search;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class SearchActivity extends Activity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, PullToRefreshView.OnFooterRefreshListener, View.OnFocusChangeListener, AdapterView.OnItemClickListener{
    /** 當前搜索類型，默認為顯示歷史记录。 **/
    private SearchType mCurSearchType = SearchType.HISTORY;
    private SearchCategoryPop mCategoryPop = null;
    /**搜索list的显示内容类型，默认是历史记录。**/
    private SearchType mSearchType = SearchType.HISTORY;
    private ImageView searchbackbtn;
    private ImageButton searchBtnIB;
    private LinearLayout searchClearLl;
    private CheckBox searchcategrycb;
    private EditText searchbaret;
    private RelativeLayout searchheadcenterll;
    private TextView searchkeytv;
    private LinearLayout searchresultkeyll;
//    private PullToRefreshView mRefreshView;
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
//        mRefreshView = (PullToRefreshView) findViewById(R.id.search_freshview);
        searchlv = (ListView) findViewById(R.id.search_lv);
        searchclearbtn = (Button) findViewById(R.id.search_clear_btn);

        mCategoryPop = new SearchCategoryPop(this, this);
        mCategoryPop.setOnDismissListener(new PopDismissLSNER());

        mAdapter = new SearchListAdapter(getApplicationContext(), mCurSearchType, mSearchTypeBeans);
        searchlv.setAdapter(mAdapter);
        searchlv.setOnItemClickListener(this);
//        mRefreshView.setDownRefreshable(false);
//        mRefreshView.setOnFooterRefreshListener(this);

        searchbackbtn.setOnClickListener(this);
        searchcategrycb.setOnCheckedChangeListener(this);
        searchbaret.setOnClickListener(this);
        searchbaret.setOnFocusChangeListener(this);
        searchClearLl.setOnClickListener(this);
        searchclearbtn.setOnClickListener(this);
        searchBtnIB.setOnClickListener(this);

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
        searchkeytv.setText(key);

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

            case R.id.search_btn_ib:{      //搜索按鈕。

                String key = searchbaret.getText().toString();
                searchbaret.clearFocus();
//                mRefreshView.setVisibility(View.VISIBLE);
                mAdapter.setViewFreshable(false);  //不讓 lisvView 重複執行getView（）。
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
//                mRefreshView.setUpRefreshable(true);
//                mRefreshView.setFootViewVisibility(View.VISIBLE);
                showkey(key);       //显示关键字项。

                break;
            }

            case R.id.category_left_item_supply: {
                searchcategrycb.setText(((TextView) v).getText());
                mCategoryPop.dismiss();

                break;
            }
            case R.id.search_clear_ll: {

                searchbaret.setText("");
                break;
            }
            default:{

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
//        mRefreshView.setUpRefreshable(false);
//        mRefreshView.setFootViewVisibility(View.GONE);
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
                mAdapter.setViewFreshable(false);           //不讓 lisvView 重複執行getView（）。
                displayHistory();
//                mRefreshView.setVisibility(View.GONE);
            }

    }

    //當前不同的搜索類型類型，做不同的操作。
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        switch (mCurSearchType){
            case HISTORY:{          //當前顯示搜索記錄。

                SearchHistoryBean historyBean = (SearchHistoryBean) mSearchTypeBeans.get(position);
                searchbaret.setText(historyBean.getHistory());
                break;
            }

            case DEMAND:{       //当前显示求购信息.

                SearchDemandBean demandBean = (SearchDemandBean) mSearchTypeBeans.get(position);
                Intent intent = new Intent(SearchActivity.this, BuyInfoActivity.class);
                intent.putExtra("id", demandBean.getId());
                startActivity(intent);

                break;
            }

            case SUPPLY:{

                SearchSupplyBean supplyBean = (SearchSupplyBean) mSearchTypeBeans.get(position);
                Intent intent = new Intent(SearchActivity.this, ProductInfoActivity.class);
                intent.putExtra(ProductInfoActivity.ARG_ID, supplyBean.getId());
                startActivity(intent);

                break;
            }

            case INTERPRISE:{

                SearchInterpriseBean interpriseBean = (SearchInterpriseBean) mSearchTypeBeans.get(position);
                Intent intent = new Intent(SearchActivity.this, InterpriseInfoActivity.class);
                intent.putExtra("id", interpriseBean.getId());
                startActivity(intent);

                break;
            }

            default:{

            }
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
     *  http://218.244.149.129/eb/index.php?s=/Home/Api/getDemandInfoList
     *
     * condition={"keywords":""}&lastId=0&pagesize=20&os=android&secret=deaf3dad91c211dd71775c47e588e7e6&uuid=319875235004276&time=1409792711637
     *
     * @param lastId
     */
    public void getDemandInfoList(final int lastId, String keyword){

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

        HttpUtil.post(url, parmater, new JsonHttpResponseHandler("UTF-8"){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                ArrayList<SearchDemandBean> searchDemandBeans = SearchDemandBeanTools.getSearchDemands(response.toString());
                if (lastId == 0) {           //如果第一次请求，即不是加载更多时。
                    mSearchTypeBeans.clear();
                }
                mCurSearchType = SearchType.DEMAND;
                if (searchDemandBeans == null || searchDemandBeans.size() == 0){


                    noData(getString(R.string.no_search_data));
                }else {
                    mSearchTypeBeans.addAll(searchDemandBeans);
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
                if (lastId == 0){           //如果第一次请求，即不是加载更多时。
                    mSearchTypeBeans.clear();
                }
                mCurSearchType = SearchType.SUPPLY;
                if (searchSupplyBeans == null || searchSupplyBeans.size()== 0) {

                    noData(getString(R.string.no_search_data));
                } else {
                    mSearchTypeBeans.addAll(searchSupplyBeans);
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
                if (lastId == 0) {           //如果第一次请求，即不是加载更多时。
                    mSearchTypeBeans.clear();
                }
                mCurSearchType = SearchType.INTERPRISE;
                if (searchInterpriseBeans == null || searchInterpriseBeans.size() == 0) {
                    noData(getString(R.string.no_search_data));
                } else {
                    mSearchTypeBeans.addAll(searchInterpriseBeans);
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
