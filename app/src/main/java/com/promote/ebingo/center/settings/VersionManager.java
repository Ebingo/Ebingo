package com.promote.ebingo.center.settings;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.WindowManager;

import com.jch.lib.util.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.promote.ebingo.R;
import com.promote.ebingo.application.HttpConstant;
import com.promote.ebingo.impl.EbingoRequestParmater;
import com.promote.ebingo.publish.EbingoDialog;
import com.promote.ebingo.util.LogCat;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by acer on 2014/10/11.
 */
public class VersionManager {

    /**
     * 请求版本信息
     *
     * @param context
     */
    public static void requestVersionCode(Context context, final boolean showDialogIfNewest) {
        final Context mContext = context.getApplicationContext();

        EbingoRequestParmater param = new EbingoRequestParmater(context);
        HttpUtil.post(HttpConstant.getNewestVersion, param, new JsonHttpResponseHandler("utf-8") {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                LogCat.i("--->", response + "");
                try {
                    JSONObject result = response.getJSONObject("response");
                    if (result.getInt("version_code") > getLocaleVersion(mContext)) {
                        final String url = result.getString("url");
                        File apkFile = getDownloadFile(mContext, url.substring(url.lastIndexOf("/") + 1));
                        DialogInterface.OnClickListener installListener = new DownloadListener(mContext, url);
                        //检查是否已经下载
                        if (apkFile.exists()) {
                            showInstallDialog(mContext, apkFile);
                        } else {//已经下载，用户可以直接安装
                            EbingoDialog versionDialog = new EbingoDialog(mContext);
                            versionDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                            versionDialog.setTitle(mContext.getString(R.string.find_new_version, apkFile.getName()));
                            versionDialog.setMessage(result.getString("msg"));
                            versionDialog.setPositiveButton(R.string.update_right_now, installListener);
                            versionDialog.setNegativeButton(R.string.cancel, versionDialog.DEFAULT_LISTENER);
                            versionDialog.show();
                        }
                    }else if(showDialogIfNewest){
                        EbingoDialog versionDialog = new EbingoDialog(mContext);
                        versionDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                        versionDialog.setMessage("已经是最新版本了！");
                        versionDialog.setTitle(R.string.warn);
                        versionDialog.setPositiveButton(R.string.i_know,versionDialog.DEFAULT_LISTENER);
                        versionDialog.show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }
        });
    }

    private static int getLocaleVersion(Context context) {
        int localeVersion = 0;
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            localeVersion = info.versionCode;
            LogCat.i("--->", "versionCode=" + info.versionCode);
            LogCat.i("--->", "versionName=" + info.versionName);
            LogCat.i("--->", "packageName=" + info.packageName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localeVersion;
    }

    /**
     * 下载并安装
     */
    static class DownloadListener implements DialogInterface.OnClickListener {

        private Context mContext;
        private String url;

        DownloadListener(Context context, String url) {
            this.mContext = context;
            this.url = url;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {

            final APKDownloadTask mTask = new APKDownloadTask(mContext);
            DialogInterface.OnClickListener l = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE: {
                            mTask.execute(url);
                            break;
                        }
                        case DialogInterface.BUTTON_NEGATIVE: {
                            dialog.dismiss();
                            break;
                        }
                    }
                }
            };
            //检查wifi状态，在非wifi状态下提醒用户，让用户确定下载
            if (!isWifi()) {
                EbingoDialog wifiDialog = new EbingoDialog(mContext);
                wifiDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                wifiDialog.setTitle(R.string.warn);
                wifiDialog.setMessage("检测到当前不是wifi状态，仍然继续下载吗？");
                wifiDialog.setPositiveButton(R.string.continue_, l);
                wifiDialog.setNegativeButton(R.string.cancel, l);
                wifiDialog.show();
                return;
            } else {
                mTask.execute(url);
            }

        }

        /**
         * 检测wifi是否可用
         *
         * @return
         */
        private boolean isWifi() {
            WifiManager manager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
            return manager.getWifiState() == WifiManager.WIFI_STATE_ENABLED;
        }

    }

    /**
     * 检查文件是否已经下载
     *
     * @param context
     * @return
     */
    private static File getDownloadFile(Context context, String fileName) {
        File apkFile;
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            apkFile = new File(Environment.getExternalStorageDirectory(), fileName);
        } else {
            apkFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName);
        }
        LogCat.i("--->", apkFile.getAbsolutePath());
        return apkFile;
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
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
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
            File apkFile;
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                apkFile = new File(Environment.getExternalStorageDirectory(), fileName);
            } else {
                apkFile = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName);
            }

            FileOutputStream fot = null;
            InputStream is = null;
            try {
                HttpURLConnection cnn = (HttpURLConnection) new URL(url).openConnection();
                cnn.setDoInput(true);
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
            showInstallDialog(mContext, file);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
        }
    }

    /**
     * 安装
     *
     * @param context
     * @param file
     */
    private static void showInstallDialog(Context context, File file) {
        final File f = file;
        final Context mContext = context.getApplicationContext();
        EbingoDialog installDialog = new EbingoDialog(context);
        installDialog.setTitle(R.string.warn);
        installDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        installDialog.setMessage(file.getName() + "已经下载完毕，请立即安装！");
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

}
