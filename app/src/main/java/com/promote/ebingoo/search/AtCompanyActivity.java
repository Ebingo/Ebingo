package com.promote.ebingoo.search;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.promote.ebingoo.R;

public class AtCompanyActivity extends Activity {

    public static final String KEY = "key";
    private WebView atcompanywv;
    private ImageView backBtn = null;
    private TextView titleTv = null;
    private ProgressBar pb = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_at_company);
        initialize();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (atcompanywv.canGoBack()) {      //按下返回键是webView回退.
                atcompanywv.goBack();
                return true;
            }
        }


        return super.onKeyDown(keyCode, event);
    }

    private void initialize() {
        String urlStr = getIntent().getStringExtra(KEY);
        backBtn = (ImageView) findViewById(R.id.common_back_btn);
        atcompanywv = (WebView) findViewById(R.id.at_company_wv);
        pb = (ProgressBar) findViewById(R.id.company_progressbar);
        titleTv = (TextView) findViewById(R.id.common_title_tv);
        atcompanywv.setWebViewClient(new MyWebViewClient());
        atcompanywv.getSettings().setJavaScriptEnabled(true);
        atcompanywv.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                pb.setProgress(newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (title != null || !title.equals(""))
                    titleTv.setText(title);
            }
        });
        if (urlStr != null && !urlStr.equals(""))
            atcompanywv.loadUrl(urlStr);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            pb.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            pb.setVisibility(View.GONE);
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }

    }


}
