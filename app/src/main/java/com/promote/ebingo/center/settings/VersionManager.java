package com.promote.ebingo.center.settings;

import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.WindowManager;

import com.jch.lib.util.DialogUtil;
import com.jch.lib.util.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.promote.ebingo.MainActivity;
import com.promote.ebingo.R;
import com.promote.ebingo.application.EbingoApp;
import com.promote.ebingo.application.HttpConstant;
import com.promote.ebingo.impl.EbingoRequestParmater;
import com.promote.ebingo.publish.EbingoDialog;
import com.promote.ebingo.util.ContextUtil;
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
    public static void requestVersionCode(final Context context, final boolean showDialogIfNewest) {
        final Context mContext = context.getApplicationContext();

        EbingoRequestParmater param = new EbingoRequestParmater(context);
        HttpUtil.post(HttpConstant.getNewestVersion, param, new JsonHttpResponseHandler("utf-8") {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                LogCat.i("--->", response + "");
                try {
                    JSONObject result = response.getJSONObject("response");
                    int version_code = result.getInt("version_code");
                    if (version_code > getLocaleVersion(mContext)) {
                        String url = result.getString("url");
                        String versionName = result.getString("version");
                        File apkFile = getDownloadFile(mContext, versionName);
                        //检查是否已经下载
                        if (apkFile.exists()) {
                            showInstallDialog(mContext, apkFile);
                        } else {//已经下载，用户可以直接安装
                            EbingoDialog versionDialog = new EbingoDialog(mContext);
                            versionDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                            versionDialog.setTitle(mContext.getString(R.string.find_new_version, apkFile.getName()));
                            versionDialog.setMessage(result.getString("msg"));
                            versionDialog.setPositiveButton(R.string.update_right_now, new DownloadListener(mContext, url, versionName));
                            versionDialog.setNegativeButton(R.string.cancel, versionDialog.DEFAULT_LISTENER);
                            versionDialog.show();
                        }
                    } else if (showDialogIfNewest) {
                        EbingoDialog versionDialog = EbingoDialog.newInstance(context, EbingoDialog.DialogStyle.STYLE_I_KNOW);
                        versionDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                        versionDialog.setMessage("已经是最新版本了！");
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
        private String fileName;

        DownloadListener(Context context, String url, String fileName) {
            this.mContext = context;
            this.url = url;
            this.fileName = fileName;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {

            final APKDownloadTask mTask = new APKDownloadTask(mContext);
            DialogInterface.OnClickListener l = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE: {
                            mTask.execute(url, fileName);
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
                mTask.execute(url, fileName);
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
     * 根据版本名创建文件
     *
     * @param context
     * @return
     */
    public static File getDownloadFile(Context context, String version) {
        File dir;
        if ( Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            dir = new File(Environment.getExternalStorageDirectory(), "ebingoo");
        } else {
            dir = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "ebingoo");
        }

        if (!dir.exists()) {
            dir.mkdirs();
        }
        String fileName = "Ebingoo" + version + ".apk";
        return new File(dir, fileName);
    }

    /**
     * 下载apk，需要传入url
     */
    public static class APKDownloadTask extends AsyncTask<String, Integer, File> implements Dialog.OnDismissListener {
        private Context mContext;
        private ApkProgressDialog dialog;
        private NotificationCompat.Builder builder;

        public APKDownloadTask(Context context) {
            this.mContext = context.getApplicationContext();
            dialog = new ApkProgressDialog(mContext);
            dialog.setOnDismissListener(this);
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }

        @Override
        protected void onPreExecute() {
            dialog.show();
        }

        @Override
        protected File doInBackground(String... params) {
            String url = params[0];
//            String fileName = url.substring(url.lastIndexOf("/") + 1);
            String fileName = params[1];
            File apkFile;

            apkFile = getDownloadFile(mContext, fileName);
            LogCat.i("install apk name--:" + apkFile.getAbsolutePath() + "---------apk name:" + apkFile.getName());
            FileOutputStream fot = null;
            InputStream is = null;
            try {
                HttpURLConnection cnn = (HttpURLConnection) new URL(url).openConnection();
                cnn.setDoInput(true);
                if (!apkFile.exists()) {
                    apkFile.createNewFile();
                }
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
                if (apkFile!=null){
                    apkFile.delete();
                }
                apkFile=null;
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
            dialog.setProgress(rate);
            showNotification(rate);
        }

        private void showNotification(int rate) {
            NotificationManager manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder build = getNotification();
            build.setContentTitle(String.format("已经下载%d%%", rate));
            build.setProgress(100, rate, false);
            //发出通知
            manager.notify(0, build.build());
        }

        private NotificationCompat.Builder getNotification() {

            if (builder == null) {
                builder = new NotificationCompat.Builder(mContext);
                Intent updateIntent = new Intent(mContext, MainActivity.class);
                PendingIntent updatePendingIntent = PendingIntent.getActivity(mContext, 0, updateIntent, 0);
                builder.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.app_icon));
                builder.setSmallIcon(R.drawable.app_icon);
                builder.setTicker("正在下载");
                builder.setAutoCancel(false);
                builder.setWhen(0);
                builder.setSound(null);
                builder.setContentIntent(updatePendingIntent);
                //设置通知栏显示内容
            }
            return builder;
        }

        @Override
        protected void onPostExecute(File file) {
            dialog.dismiss();
            if (file == null) {
                ContextUtil.toast(R.string.download_fialed);
            } else {
                NotificationCompat.Builder build = getNotification();
                build.setContentTitle(mContext.getString(R.string.click_install,file.getName()));
                Intent intent = getInstallIntent(file);
                build.setContentIntent(PendingIntent.getActivity(mContext, 0, intent, 0));
                build.setAutoCancel(true);
                build.setProgress(100, 100, false);
                NotificationManager manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                manager.notify(0, build.build());
                showInstallDialog(mContext, file);
            }
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
        }
    }

    private static Intent getInstallIntent(File file) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        return intent;
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
        installDialog.setMessage(context.getString(R.string.click_install,file.getName()));
        installDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = getInstallIntent(f);
                mContext.startActivity(intent);
                dialog.dismiss();
            }
        });
        installDialog.show();
    }

}
