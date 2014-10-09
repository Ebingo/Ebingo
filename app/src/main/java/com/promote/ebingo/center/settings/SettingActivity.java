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
    private APKDownloadTask mTask;
    private final String testUrl = "http://www.ebingoo.com/upload/ebingoo.apk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
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
                if (mTask == null) mTask = new APKDownloadTask(SettingActivity.this);
                requestVersionCode(SettingActivity.this);
//                downloadFile(testUrl);
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
                EbingoDialog dialog=new EbingoDialog(this);
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

    /**
     * 请求版本信息
     *
     * @param context
     */
    private void requestVersionCode(final Context context) {

        EbingoRequestParmater param = new EbingoRequestParmater(context);
        final Dialog dialog = DialogUtil.waitingDialog(this, "正在获取版本信息...");
        HttpUtil.post(HttpConstant.getNewestVersion, param, new JsonHttpResponseHandler("utf-8") {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                EbingoDialog versionDialog = new EbingoDialog(context);
                try {
                    LogCat.i("--->", response + "");
                    LogCat.i("--->", "localeVersion:"+ EbingoApp.localeVersion);

                    JSONObject result = response.getJSONObject("response");
                    if (result.getInt("version_code") > EbingoApp.localeVersion) {
                        final String url = result.getString("url");
//                        final String url = testUrl;
                        String fileName = url.substring(url.lastIndexOf("/") + 1);
                        versionDialog.setTitle(fileName);
                        versionDialog.setMessage(result.getString("msg"));
                        DialogInterface.OnClickListener l = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == DialogInterface.BUTTON_POSITIVE)
                                    //检查wifi状态，在非wifi状态下提醒用户，让用户确定下载
                                    checkWifiAndDownload(url);
                            }
                        };
                        versionDialog.setPositiveButton(R.string.update_right_now, l);
                        versionDialog.setNegativeButton(R.string.cancel, l);
                    } else {
                        versionDialog.setMessage(R.string.already_new_version);
                    }
                    versionDialog.show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onFinish() {
                dialog.dismiss();
            }
        });
    }

    /**
     * 检查wifi状态，在非wifi状态下提醒用户，让用户确定下载
     *
     * @param url
     */
    private void checkWifiAndDownload(final String url) {
        WifiManager manager = (WifiManager) getSystemService(WIFI_SERVICE);
        if (manager.getWifiState() != WifiManager.WIFI_STATE_ENABLED) {
            EbingoDialog dialog = new EbingoDialog(this);
            dialog.setTitle(R.string.warn);
            DialogInterface.OnClickListener l = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == DialogInterface.BUTTON_POSITIVE) mTask.execute(url);
                }
            };
            dialog.setPositiveButton(R.string.yes, l);
            dialog.setNegativeButton(R.string.cancel, l);
            dialog.setMessage("检测到当前不是wifi状态，");
            dialog.show();
            return;
        }
        mTask.execute(url);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void downloadFile(String url) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            mTask.execute(url);
            return;
        }
        DownloadManager manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        try {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
            request.setAllowedOverRoaming(false);
            MimeTypeMap typeMap = MimeTypeMap.getSingleton();

            request.setMimeType(typeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url)));
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
            request.setVisibleInDownloadsUi(true);

            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "gg.mp3");
            request.setTitle("download gg.mp3");
            manager.enqueue(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        registerReceiver(new DownLoadReceiver(), new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    /**
     * 下载apk，需要传入url
     */
    public static class APKDownloadTask extends AsyncTask<String, Integer, File> implements Dialog.OnDismissListener {
        private Context mContext;
        private ProgressDialog dialog;

        public APKDownloadTask(Context Context) {
            this.mContext = Context;
            dialog = new ProgressDialog(mContext);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setOnDismissListener(this);
            dialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            dialog.show();
        }

        @Override
        protected File doInBackground(String... params) {
            String url = params[0];
            String fileName = url.substring(url.lastIndexOf("/") + 1);
            File apkFile = new File(Environment.getExternalStorageDirectory(), fileName);
            FileOutputStream fot = null;
            InputStream is = null;
            try {
                HttpURLConnection cnn = (HttpURLConnection) new URL(url).openConnection();
                cnn.setDoInput(true);
                cnn.getURL().getFile();
                fot = new FileOutputStream(apkFile);
                is = cnn.getInputStream();
                byte[] bytes = new byte[10 * 1024];

                int len;
                int contentLength = cnn.getContentLength();
                int downloadLength = 0;
                while ((len = is.read(bytes)) != -1) {
                    fot.write(bytes, 0, len);
                    downloadLength += len;
                    publishProgress(downloadLength, contentLength);
                }
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
                ContextUtil.toast("下载失败！");
                return null;
            } finally {
                if (fot != null)
                    try {
                        fot.flush();
                        fot.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

            }

            return apkFile;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int download = values[0];
            int total = values[1];
            int rate = (int) ((download / (float) total) * 100);
            dialog.setMessage("已完成" + rate + "%");
            dialog.setProgress(rate);
        }

        @Override
        protected void onPostExecute(File file) {
            dialog.dismiss();
            final File f = file;
            EbingoDialog installDialog = new EbingoDialog(mContext);
            installDialog.setTitle(R.string.warn);
            installDialog.setMessage(file.getName()+"下载完成，请立即安装！");
            installDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setAction(android.content.Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(f),
                            "application/vnd.android.package-archive");
                    mContext.startActivity(intent);
                }
            });
            installDialog.show();
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
        }
    }

}
