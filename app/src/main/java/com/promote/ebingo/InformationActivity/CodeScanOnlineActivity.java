package com.promote.ebingo.InformationActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
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

import com.jch.lib.util.VaildUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.promote.ebingo.R;
import com.promote.ebingo.center.CallRecordActivity;
import com.promote.ebingo.publish.EbingoDialog;
import com.promote.ebingo.util.ContextUtil;
import com.promote.ebingo.util.LogCat;

import org.w3c.dom.Text;

public class CodeScanOnlineActivity extends Activity implements View.OnClickListener {

    private WebView scanwb;
    private String urlStr = null;
    private String curUrl=null;//当前页面url
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
                LogCat.i("--------------url" + url);
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
            curUrl=url;
            view.loadUrl(url);
            return true;
        }
    }

    private class JavaInterface {
        @JavascriptInterface()
        public void jumpToSupply(int id) {
            LogCat.i("---------jumpToSupply");
            Intent intent = new Intent(CodeScanOnlineActivity.this, ProductInfoActivity.class);
            intent.putExtra(ProductInfoActivity.ARG_ID, id);
            startActivity(intent);
        }

        @JavascriptInterface()
        public void jumpToDemand(int id) {
            LogCat.i("---------jumpToDemand");
            Intent intent = new Intent(CodeScanOnlineActivity.this, BuyInfoActivity.class);
            intent.putExtra(BuyInfoActivity.DEMAND_ID, id);
            startActivity(intent);
        }

        @JavascriptInterface()
        public void jumpToCompany(int id) {
            LogCat.i("---------jumpToCompany");
            Intent intent = new Intent(CodeScanOnlineActivity.this, InterpriseInfoActivity.class);
            intent.putExtra(InterpriseInfoActivity.ARG_ID, id);
            startActivity(intent);
        }

        @JavascriptInterface
        public void callPhone(final String number, String contacts) {
            final Context context = CodeScanOnlineActivity.this;
            if (TextUtils.isEmpty(number) || number.equals(VaildUtil.validPhone(number))) return;

            EbingoDialog dialog = EbingoDialog.newInstance(context, EbingoDialog.DialogStyle.STYLE_CALL_PHONE);
            dialog.setTitle(contacts);
            dialog.setMessage(context.getString(R.string.dial_number_notice, number));
            dialog.setPositiveButton(R.string.make_call, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    LogCat.i("--->", "dial:" + number);
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
                    startActivity(intent);
                    dialog.dismiss();
                }
            });
            dialog.show();
        }

        @JavascriptInterface
        public void share() {
            Intent intent = new Intent(Intent.ACTION_SEND); // 启动分享发送的属性
            intent.setType("text/plain"); // 分享发送的数据类型
            intent.putExtra(Intent.EXTRA_SUBJECT, scanwb.getTitle()); // 分享的主题
            intent.putExtra(Intent.EXTRA_TEXT, scanwb.getTitle()+scanwb.getUrl()); // 分享的内容
            startActivity(Intent.createChooser(intent, "分享到"));
        }
    }
}
