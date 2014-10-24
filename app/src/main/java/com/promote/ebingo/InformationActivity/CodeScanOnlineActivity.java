package com.promote.ebingo.InformationActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.promote.ebingo.R;
import com.promote.ebingo.util.ContextUtil;
import com.promote.ebingo.util.LogCat;

import org.w3c.dom.Text;

public class CodeScanOnlineActivity extends Activity implements View.OnClickListener {

    private WebView scanwb;
    private String urlStr = null;
    public static final String URLSTR = "urlStr";
    /**
     * 回退按钮. *
     */
    private ImageView mBackIv = null;
    private TextView titleTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_scan);
        titleTv = (TextView) findViewById(R.id.common_title_tv);
        initialize();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.code_scan, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initialize() {

        scanwb = (WebView) findViewById(R.id.scan_wb);
        mBackIv = (ImageView) findViewById(R.id.common_back_btn);
        mBackIv.setOnClickListener(this);
        urlStr = getIntent().getStringExtra(URLSTR);
        LogCat.d("CodeScan", "url=" + urlStr);
        scanwb.getSettings().setJavaScriptEnabled(true);
        scanwb.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        scanwb.addJavascriptInterface(new JavaInterface(), "ebingoo");
        scanwb.setWebViewClient(new MyWebViewClient());
        scanwb.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                titleTv.setText(title);
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                LogCat.i("--------------url"+url);
                return false;
            }

        });
        scanwb.setDownloadListener(new MyDownLoadListener());
        scanwb.loadUrl(urlStr);
    }

    private class MyDownLoadListener implements DownloadListener {

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
            ContextUtil.toast("这是一个下载链接！");
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.common_back_btn: {
                onBackPressed();
                this.finish();
                break;
            }
            default: {

            }
        }

    }


    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            view.loadUrl(url);
            return true;
        }
    }

    private class JavaInterface {
        @JavascriptInterface()
        public String jumpToSupply(int id) {
            LogCat.i("---------jumpToSupply");
            Intent intent=new Intent(CodeScanOnlineActivity.this,ProductInfoActivity.class);
            intent.putExtra(ProductInfoActivity.ARG_ID,id);
            startActivity(intent);
            return "test";
        }

        @JavascriptInterface()
        public void jumpToDemand(int id) {
            LogCat.i("---------jumpToSupply");
            Intent intent=new Intent(CodeScanOnlineActivity.this,BuyInfoActivity.class);
            intent.putExtra(BuyInfoActivity.DEMAND_ID,id);
            startActivity(intent);
        }

        @JavascriptInterface()
        public void jumpToCompany(int id) {
            LogCat.i("---------jumpToSupply");
            Intent intent=new Intent(CodeScanOnlineActivity.this,InterpriseInfoActivity.class);
            intent.putExtra(InterpriseInfoActivity.ARG_ID,id);
            startActivity(intent);
        }
    }
}
