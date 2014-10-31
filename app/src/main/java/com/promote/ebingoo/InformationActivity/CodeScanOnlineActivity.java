package com.promote.ebingoo.InformationActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jch.lib.util.VaildUtil;
import com.promote.ebingoo.R;
import com.promote.ebingoo.publish.EbingoDialog;
import com.promote.ebingoo.util.ContextUtil;
import com.promote.ebingoo.util.LogCat;

public class CodeScanOnlineActivity extends Activity implements View.OnClickListener {

    private WebView scanwb;
    private String urlStr = null;
    public static final String URLSTR = "urlStr";

    /**
     * 回退按钮. *
     */
    private ImageView mBackIv = null;
    private TextView titleTv;
    private ProgressBar bar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_scan);
        titleTv = (TextView) findViewById(R.id.title_tv);
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
        bar = (ProgressBar) findViewById(R.id.progressBar);
        mBackIv.setOnClickListener(this);
        urlStr = getIntent().getStringExtra(URLSTR);
        LogCat.d("CodeScan", "url=" + urlStr);
        WebSettings settings=scanwb.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setUserAgentString(settings.getUserAgentString()+" ebingoo");
        scanwb.addJavascriptInterface(new JavaInterface(this), "ebingoo");
        scanwb.setWebViewClient(new MyWebViewClient());
        scanwb.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                titleTv.setText(title);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                bar.setProgress(newProgress);
            }

        });
        scanwb.setDownloadListener(new MyDownLoadListener());
        scanwb.loadUrl(urlStr);
    }

    private class MyDownLoadListener implements DownloadListener {

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
            ContextUtil.toast(R.string.is_download_url);
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

        @Override
        public void onPageFinished(WebView view, String url) {
            bar.setVisibility(View.GONE);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            bar.setVisibility(View.VISIBLE);
        }
    }

    private class JavaInterface {
        private Activity activity;
        private Handler handler;


        private JavaInterface(Activity activity) {
            this.activity = activity;
            handler=new Handler(activity.getMainLooper());
        }

        /**
         * 推送Intent到消息队列的接口
         */
        private class JavaInterfaceRunnable implements Runnable{
            private  Intent postIntent;

            private JavaInterfaceRunnable(Intent postIntent) {
                this.postIntent = postIntent;
            }

            @Override
            public void run() {
                activity.startActivity(postIntent);
            }
        }

        @JavascriptInterface()
        public void jumpToSupply(int id) {
            LogCat.i("---------jumpToSupply");
            Intent intent=new Intent(activity, ProductInfoActivity.class);
            intent.putExtra(ProductInfoActivity.ARG_ID,id);
            handler.post(new JavaInterfaceRunnable(intent));

        }

        @JavascriptInterface()
        public void jumpToDemand(int id) {
            LogCat.i("---------jumpToDemand");
            Intent intent = new Intent(activity, BuyInfoActivity.class);
            intent.putExtra(BuyInfoActivity.DEMAND_ID, id);
            handler.post(new JavaInterfaceRunnable(intent));
        }

        @JavascriptInterface()
        public void jumpToCompany(int id) {
            LogCat.i("---------jumpToCompany");
            Intent intent = new Intent(activity, InterpriseInfoActivity.class);
            intent.putExtra(InterpriseInfoActivity.ARG_ID, id);
            handler.post(new JavaInterfaceRunnable(intent));
        }

        @JavascriptInterface
        public String callPhone(final String number, final String contacts) {
            if (TextUtils.isEmpty(number) || number.equals(VaildUtil.validPhone(number))) return "fail";

            handler.post(new Runnable() {
                @Override
                public void run() {
                    EbingoDialog dialog = EbingoDialog.newInstance(activity, EbingoDialog.DialogStyle.STYLE_CALL_PHONE);
                    dialog.setTitle(contacts);
                    dialog.setMessage(activity.getString(R.string.dial_number_notice, number));
                    dialog.setPositiveButton(R.string.make_call, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            LogCat.i("--->", "dial:" + number);
                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
                            activity.startActivity(intent);
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            });
            return "OK";
        }

        @JavascriptInterface
        public String share() {

            handler.post(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(Intent.ACTION_SEND); // 启动分享发送的属性
                    intent.setType("text/plain"); // 分享发送的数据类型
                    intent.putExtra(Intent.EXTRA_SUBJECT, scanwb.getTitle()); // 分享的主题
                    intent.putExtra(Intent.EXTRA_TEXT, scanwb.getTitle() + scanwb.getUrl()); // 分享的内容
                    activity.startActivity(Intent.createChooser(intent, "分享到"));
                }
            });
            return "OK";
        }
    }
}
