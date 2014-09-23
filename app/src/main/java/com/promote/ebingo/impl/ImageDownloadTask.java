package com.promote.ebingo.impl;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.jch.lib.util.DialogUtil;
import com.promote.ebingo.bean.Company;
import com.promote.ebingo.publish.PreviewActivity;
import com.promote.ebingo.util.ContextUtil;
import com.promote.ebingo.util.ImageUtil;
import com.promote.ebingo.util.LogCat;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by acer on 2014/9/23.
 */
public class ImageDownloadTask extends AsyncTask<String, Void, Bitmap> {
    private Uri uri;

    public ImageDownloadTask() {

    }

    public void loadBy(String url) {
        execute(url);
    }

    public void loadBY(Uri uri) {
        this.uri = uri;
        execute();
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        Bitmap bitmap = null;
        String path = null;
        try {
            if (uri != null)
                bitmap = MediaStore.Images.Media.getBitmap(ContextUtil.getContext().getContentResolver(), uri);
            else if (!TextUtils.isEmpty(path=params[0])) {
                URL url = new URL(path);
                LogCat.i("ImageDownloadTask--->", path);
                URLConnection urlConnection = url.openConnection();
                urlConnection.setDoInput(true);
                bitmap = BitmapFactory.decodeStream(urlConnection.getInputStream());
                Company.getInstance().setImageUri(ImageUtil.saveBitmap(bitmap, PreviewActivity.PIC_NAME));
            }
        } catch (IOException e) {

        }
        return bitmap;
    }
}
