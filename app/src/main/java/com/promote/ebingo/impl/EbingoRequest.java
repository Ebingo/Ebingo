package com.promote.ebingo.impl;

import android.app.Activity;
import android.app.ProgressDialog;

import com.jch.lib.util.DialogUtil;
import com.jch.lib.util.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.promote.ebingo.application.HttpConstant;
import com.promote.ebingo.bean.SearchDemandBean;
import com.promote.ebingo.bean.SearchDemandBeanTools;
import com.promote.ebingo.bean.SearchSupplyBean;
import com.promote.ebingo.bean.SearchSupplyBeanTools;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by ACER on 2014/9/19.
 */
public class EbingoRequest {

    public interface RequestCallBack<T> {
        public void onFaild(int resultCode, String msg);

        public void onSuccess(T resultObj);
    }


    /**
     * 從網絡獲取求购信息列表.
     *
     * @param activity
     * @param lastId
     * @param companyId
     * @param pageSize
     * @param callBack
     */
    public static void getDemandInfoList(Activity activity, final int lastId, int companyId, int pageSize, final RequestCallBack<ArrayList<SearchDemandBean>> callBack) {

        String url = HttpConstant.getDemandInfoList;
        EbingoRequestParmater parmater = new EbingoRequestParmater(activity.getApplicationContext());
        parmater.put("lastid", lastId);
        parmater.put("pagesize", pageSize);       //每页显示20条。
        try {
            parmater.put("condition", URLEncoder.encode(appendcompanyId(companyId), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        final ProgressDialog dialog = DialogUtil.waitingDialog(activity);

        HttpUtil.post(url, parmater, new JsonHttpResponseHandler("UTF-8") {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                ArrayList<SearchDemandBean> searchDemandBeans = SearchDemandBeanTools.getSearchDemands(response.toString());
                callBack.onSuccess(searchDemandBeans);
//                if (lastId == 0) {           //如果第一次请求，即不是加载更多时。
//                    mSearchTypeBeans.clear();
//                }
//                mCurSearchType = SearchType.DEMAND;
//                if (searchDemandBeans == null || searchDemandBeans.size() == 0) {
//
//
//                    noData(getString(R.string.no_search_data));
//                } else {
//                    mSearchTypeBeans.addAll(searchDemandBeans);
//                    hasData(false);
//                }
//
//                mAdapter.notifyDataSetChanged(mSearchTypeBeans);
                dialog.dismiss();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                callBack.onFaild(statusCode, "加载数据失败。");
                dialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                callBack.onFaild(statusCode, "加载数据失败。");
                dialog.dismiss();
            }
        });

    }

    /**
     * 拼接赛选条件参数。
     *
     * @param companyId
     * @return
     */
    public static String appendcompanyId(int companyId) {
        StringBuffer sb = new StringBuffer("{\"company_id\":\"");
        sb.append(companyId);
        sb.append("\"}");
        return sb.toString();
    }


    /**
     * 從網絡获取供应信息列表。
     *
     * @param activity
     * @param lastId
     * @param company_id
     * @param pageSize
     * @param callBack
     */
    public static void getSupplyInfoList(Activity activity, final int lastId, int company_id, int pageSize, final RequestCallBack<ArrayList<SearchSupplyBean>> callBack) {


        String url = HttpConstant.getSupplyInfoList;
        EbingoRequestParmater parmater = new EbingoRequestParmater(activity.getApplicationContext());
        parmater.put("lastid", lastId);
        parmater.put("pagesize", pageSize);       //每页显示20条。
        try {
            parmater.put("condition", URLEncoder.encode(appendcompanyId(company_id), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        final ProgressDialog dialog = DialogUtil.waitingDialog(activity);

        HttpUtil.post(url, parmater, new JsonHttpResponseHandler("UTF-8") {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                ArrayList<SearchSupplyBean> searchSupplyBeans = SearchSupplyBeanTools.getSearchSupplyBeans(response.toString());

                callBack.onSuccess(searchSupplyBeans);
//                if (lastId == 0) {           //如果第一次请求，即不是加载更多时。
//                    mSearchTypeBeans.clear();
//                }
//                mCurSearchType = SearchType.SUPPLY;
//                if (searchSupplyBeans == null || searchSupplyBeans.size() == 0) {
//
//                    noData(getString(R.string.no_search_data));
//                } else {
//                    mSearchTypeBeans.addAll(searchSupplyBeans);
//                    hasData(false);
//                }
//                mAdapter.notifyDataSetChanged(mSearchTypeBeans);
                dialog.dismiss();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
//                getDataFailed();
                callBack.onFaild(statusCode, "加载数据失败。");
                dialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
//                getDataFailed();
                callBack.onFaild(statusCode, "加载数据失败。");
                dialog.dismiss();
            }
        });

    }
}
