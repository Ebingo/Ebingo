package com.promote.ebingo.InformationActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.jch.lib.util.DialogUtil;
import com.jch.lib.util.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.promote.ebingo.R;
import com.promote.ebingo.application.HttpConstant;
import com.promote.ebingo.bean.NewsBeanTools;
import com.promote.ebingo.impl.EbingoRequestParmater;

import org.apache.http.Header;
import org.json.JSONObject;

public class InterpriseNewActivity extends Activity {

    public final static String ARG_ID = "news_id";
    private ImageView commonbackbtn;
    private TextView commontitletv;
    private WebView iprisenewsdiswv;
    private int newsId = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interprise_new);
        initialize();
        getNewsWapUrl();
    }


    private void initialize() {

        newsId = getIntent().getIntExtra(ARG_ID, -1);

        commonbackbtn = (ImageView) findViewById(R.id.common_back_btn);
        commontitletv = (TextView) findViewById(R.id.common_title_tv);
        iprisenewsdiswv = (WebView) findViewById(R.id.iprise_news_dis_wv);
        // 设置支持JavaScript等
        WebSettings mWebSettings = iprisenewsdiswv.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setBuiltInZoomControls(true);
        mWebSettings.setLightTouchEnabled(true);
        mWebSettings.setSupportZoom(true);
        iprisenewsdiswv.setHapticFeedbackEnabled(false);

    }


    /**
     * 獲得詳情。
     */
    private void getNewsWapUrl() {

        String url = HttpConstant.getNewsWapUrl;
        EbingoRequestParmater parmater = new EbingoRequestParmater(getApplicationContext());
        parmater.put("newsid", newsId);
        final ProgressDialog dialog = DialogUtil.waitingDialog(InterpriseNewActivity.this);


        HttpUtil.post(url, parmater, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                if (response != null) {
                    NewsBeanTools.NewsBean newsBean = NewsBeanTools.getNews(response.toString());
                    final String mimeType = "text/html";
                    final String encoding = "utf-8";

//                    iprisenewsdiswv.loadDataWithBaseURL(newsBean.getUrl(), newsBean.getUrl(), mimeType,
//                            encoding, "");
                    iprisenewsdiswv.loadUrl(newsBean.getUrl());
                }
                super.onSuccess(statusCode, headers, response);
                dialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                dialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                dialog.dismiss();
            }
        });

    }

}
