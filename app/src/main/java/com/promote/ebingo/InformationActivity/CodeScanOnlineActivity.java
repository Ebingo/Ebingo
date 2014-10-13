package com.promote.ebingo.InformationActivity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import com.promote.ebingo.R;

public class CodeScanOnlineActivity extends ActionBarActivity {

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
        scanwb.loadUrl(urlStr);
    }
}
