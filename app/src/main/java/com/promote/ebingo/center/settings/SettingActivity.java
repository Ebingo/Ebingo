package com.promote.ebingo.center.settings;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.webkit.MimeTypeMap;

import com.jch.lib.util.HttpUtil;
import com.jch.lib.util.ImageManager;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.promote.ebingo.BaseActivity;
import com.promote.ebingo.MainActivity;
import com.promote.ebingo.R;
import com.promote.ebingo.application.EbingoApp;
import com.promote.ebingo.application.HttpConstant;
import com.promote.ebingo.bean.Company;
import com.promote.ebingo.impl.EbingoHandler;
import com.promote.ebingo.impl.EbingoRequestParmater;
import com.promote.ebingo.publish.login.LoginActivity;
import com.promote.ebingo.util.ContextUtil;
import com.promote.ebingo.util.FileUtil;
import com.promote.ebingo.util.LogCat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
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
//                requestVersionCode(getApplicationContext());
                downloadFile("http://d3.dn.ptbus.com/soft/apk/ptbus_daotachuanqi_2.1.2.apk");
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
            case R.id.logout:
                Company.clearInstance();
                ContextUtil.cleanCurComany();
                FileUtil.deleteFile(this, FileUtil.FILE_COMPANY);
                toActivity(LoginActivity.class);
                finish();
                break;
            default:
                super.onClick(v);
        }
    }

    private void requestVersionCode(final Context context) {

        EbingoRequestParmater param = new EbingoRequestParmater(context);
        HttpUtil.post(HttpConstant.getNewestVersion, param, new EbingoHandler() {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                try {
                    if (response.getInt("version") > EbingoApp.localeVersion) {
                        builder.setMessage(response.getString("msg"));
                        builder.setPositiveButton(R.string.update_right_now, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                    } else {
                        builder.setMessage(R.string.already_new_version);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(int statusCode, String msg) {

            }

            @Override
            public void onFinish() {

            }
        });
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void downloadFile(String url) {
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

    public static class APKDownloadTask extends AsyncTask<String, Integer, Void> {
        private Context mContext;

        public APKDownloadTask(Context Context) {
            this.mContext = Context;
        }

        @Override
        protected Void doInBackground(String... params) {
            String url = params[0];
            String fileName = params[1];
            FileOutputStream fot = null;
            InputStream is = null;
            try {
                URLConnection cnn = new URL(url).openConnection();
                cnn.setDoInput(true);
                fot = new FileOutputStream(new File(Environment.getExternalStorageDirectory(), fileName));
                is = cnn.getInputStream();
                byte[] bytes = new byte[10 * 1024];

                int len;
                int contentLength=cnn.getContentLength();
                int downloadLength=0;
                while ((len = is.read(bytes)) != -1) {
                    fot.write(bytes, 0, len);
                    downloadLength+=len;
                    publishProgress(downloadLength,contentLength);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fot != null)
                    try {
                        fot.flush();
                        fot.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int download=values[0];
            int total=values[1];
            int rate= (int) ((download/(float)total)*100);
            NotificationCompat.Builder builder=new NotificationCompat.Builder(mContext);
            builder.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(),R.drawable.ic_launcher));
            builder.setContent(null);
            builder.setAutoCancel(false);
            builder.setProgress(total,download,true);
            builder.setContentIntent(PendingIntent.getActivity(mContext,1,new Intent(mContext, MainActivity.class),PendingIntent.FLAG_UPDATE_CURRENT));
            NotificationManager manager= (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(0,builder.build());
        }

    }

}
