package com.promote.ebingoo.impl;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.promote.ebingoo.bean.Company;
import com.promote.ebingoo.publish.PreviewActivity;
import com.promote.ebingoo.util.ContextUtil;
import com.promote.ebingoo.util.ImageUtil;
import com.promote.ebingoo.util.LogCat;

import java.io.InputStream;
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
            else if (!TextUtils.isEmpty(path = params[0])) {
                URL url = new URL(path);
                LogCat.i("ImageDownloadTask--->", path);
                URLConnection urlConnection = url.openConnection();
                urlConnection.setDoInput(true);
                InputStream inputStream = urlConnection.getInputStream();
                LogCat.i("--->", inputStream + "");
                bitmap = BitmapFactory.decodeStream(inputStream);
                Company.getInstance().setImageUri(ImageUtil.saveBitmap(bitmap, PreviewActivity.PIC_NAME));
            }
        } catch (Exception e) {
           LogCat.e("ImageDownloadTask error!");
        }
        return bitmap;
    }
}
