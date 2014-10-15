package com.promote.ebingo.center.settings;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.TextView;

import com.jch.lib.util.DialogUtil;
import com.jch.lib.util.HttpUtil;
import com.jch.lib.util.ImageManager;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.promote.ebingo.BaseActivity;
import com.promote.ebingo.MainActivity;
import com.promote.ebingo.R;
import com.promote.ebingo.application.EbingoApp;
import com.promote.ebingo.application.HttpConstant;
import com.promote.ebingo.bean.Company;
import com.promote.ebingo.impl.EbingoHandler;
import com.promote.ebingo.impl.EbingoRequestParmater;
import com.promote.ebingo.publish.EbingoDialog;
import com.promote.ebingo.publish.login.LoginActivity;
import com.promote.ebingo.util.ContextUtil;
import com.promote.ebingo.util.FileUtil;
import com.promote.ebingo.util.LogCat;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by acer on 2014/9/16.
 */
public class SettingActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
        try {
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            TextView tv_version = (TextView) findViewById(R.id.tv_version_name);
            tv_version.setText(getString(R.string.current_version, versionName));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.company_setting:
                toActivity(EnterpriseSettingActivity.class);
                break;
            case R.id.about_us:
                toActivity(AboutUsActivity.class);
                break;
            case R.id.check_update: {
                VersionManager.requestVersionCode(SettingActivity.this, true);
                break;
            }
            case R.id.clear_cache: {
                FileUtil.clearCache(getApplicationContext());
                ImageLoader.getInstance().clearDiskCache();
                ImageLoader.getInstance().clearMemoryCache();
                ContextUtil.toast(R.string.clear_success);
                break;
            }
            case R.id.suggestion:
                toActivity(SuggestionActivity.class);
                break;
            case R.id.logout: {
                EbingoDialog dialog = new EbingoDialog(this);
                dialog.setTitle(R.string.warn);
                dialog.setMessage(R.string.confirm_logout);
                DialogInterface.OnClickListener l = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            Company.clearInstance();
                            ContextUtil.cleanCurComany();
                            FileUtil.deleteFile(SettingActivity.this, FileUtil.FILE_COMPANY);
                            toActivity(LoginActivity.class);
                            finish();
                        }
                    }
                };
                dialog.setPositiveButton(R.string.yes, l);
                dialog.setNegativeButton(R.string.cancel, l);
                dialog.show();
                break;
            }
            default:
                super.onClick(v);
        }
    }


}
