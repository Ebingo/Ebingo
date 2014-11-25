package com.promote.ebingoo.impl;

import android.app.Activity;
import android.app.ProgressDialog;

import com.jch.lib.util.DialogUtil;
import com.jch.lib.util.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.promote.ebingoo.R;
import com.promote.ebingoo.application.HttpConstant;
import com.promote.ebingoo.bean.CategoryBeanTools;
import com.promote.ebingoo.bean.CategoryBeen;
import com.promote.ebingoo.bean.Company;
import com.promote.ebingoo.bean.GetIndexBeanTools;
import com.promote.ebingoo.bean.SearchDemandBean;
import com.promote.ebingoo.bean.SearchDemandBeanTools;
import com.promote.ebingoo.bean.SearchSupplyBean;
import com.promote.ebingoo.bean.SearchSupplyBeanTools;
import com.promote.ebingoo.util.ContextUtil;
import com.promote.ebingoo.util.FileUtil;
import com.promote.ebingoo.util.LogCat;

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
     * 获取首页数据.
     *
     * @param activity
     * @param callBack
     */
    public static void getHomedata(Activity activity, boolean showDialog, final RequestCallBack<GetIndexBeanTools.GetIndexBean> callBack) {

        final ProgressDialog dialog = DialogUtil.waitingDialog(activity, showDialog);
        EbingoRequestParmater parma = new EbingoRequestParmater(activity.getApplicationContext());
        Company company = Company.getInstance();
        if (company.getCompanyId() != null) {
            parma.put("company_id", company.getCompanyId());
        } else {
            parma.put("company_id", 0);
        }

        HttpUtil.post(HttpConstant.getIndex, parma, new JsonHttpResponseHandler("utf-8") {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                LogCat.d("home data -- : " + response.toString());
                GetIndexBeanTools.GetIndexBean indexBean = GetIndexBeanTools.getIndexBeanJson(response.toString());
                if (indexBean != null) {
                    LogCat.i("save data -- :" + indexBean.getHot_category().toString());
                    ContextUtil.saveCache(FileUtil.HOEM_DATA_CACh, indexBean);
                } else {
                    LogCat.i("get home data failed　－－　：" + indexBean);
                    indexBean = (GetIndexBeanTools.GetIndexBean) ContextUtil.read(FileUtil.HOEM_DATA_CACh);
                }
                callBack.onSuccess(indexBean);
                if (dialog.isShowing())
                    dialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                callBack.onFaild(statusCode, "数据解析错误。");
                if (dialog.isShowing())
                    dialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                callBack.onFaild(statusCode, "网络错误");

                if (dialog.isShowing())
                    dialog.dismiss();
            }
        });
    }

    /**
     * 获取发现分类列表。
     *
     * @param activity
     * @param callBack
     */
    public static void getCategoryList(final Activity activity, final RequestCallBack<ArrayList<CategoryBeen>> callBack) {

        String urlStr = HttpConstant.getCategories;
        final ProgressDialog dialog = DialogUtil.waitingDialog(activity);
        EbingoRequestParmater parmater = new EbingoRequestParmater(activity.getApplicationContext());
        parmater.put("scene", "find");
        HttpUtil.post(urlStr, parmater, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                ArrayList<CategoryBeen> categoryBeens = CategoryBeanTools.getCategories(response.toString());
                if (categoryBeens != null && categoryBeens.size() != 0) {
                    callBack.onSuccess(categoryBeens);
                    ContextUtil.saveCache(FileUtil.CATEGORY_CACH, categoryBeens);
                } else {
                    callBack.onFaild(400, "网络错误");
                }

                dialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

                callBack.onFaild(statusCode, activity.getResources().getString(R.string.no_network));

                dialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                callBack.onFaild(statusCode, "数据解析错误.");
                dialog.dismiss();
            }
        });

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
     * http://218.244.149.129/eb/index.php?s=/Home/Api/getSupplyInfoList?condition=%7B%22company_id%22%3A%22-1%22%7D&pagesize=20&lastid=0&os=android&secret=5cab4ffb3c00730b53eaad20a22bb201&uuid=000000000000000&time=1411200220872
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
                LogCat.d("--->" + response);
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
