package com.promote.ebingo.InformationActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.promote.ebingo.R;

public class CodeScanOnlineActivity extends Activity {

    private WebView scanwb;
    private String urlStr = null;
    public static final String URLSTR = "urlStr";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_scan);
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
        urlStr = getIntent().getStringExtra(URLSTR);
        scanwb.getSettings().setJavaScriptEnabled(true);
        scanwb.setWebViewClient(new MyWebViewClient());
        scanwb.loadUrl(urlStr);
    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            view.loadUrl(url);
            return true;
        }
    }
}
