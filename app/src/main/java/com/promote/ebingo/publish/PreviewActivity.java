package com.promote.ebingo.publish;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.jch.lib.util.DialogUtil;
import com.jch.lib.util.ImageManager;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.utils.ImageSizeUtils;
import com.promote.ebingo.R;
import com.promote.ebingo.util.ImageUtil;
import com.promote.ebingo.util.LogCat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by acer on 2014/9/9.
 */
public class PreviewActivity extends Activity implements View.OnClickListener{
    private Uri uri;
    private Uri savedUri;
    private ImageView picked_image;
    public static  final String PIC_NAME="ebingoo_preview.png";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preview_activity);
        findViewById(R.id.common_back_btn).setOnClickListener(this);
        picked_image= (ImageView) findViewById(R.id.imageView);
        uri=getIntent().getData();
        new LoadImageTask().execute(uri);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.common_back_btn:
                finish();
                break;
            case R.id.btn_done:
                Intent data=new Intent();
                data.setData(savedUri);
                setResult(RESULT_OK,data);
                finish();
                break;
        }
    }
    /**
     * 根据Uri来加载一个图片，并压缩
     */
    private class LoadImageTask extends AsyncTask<Uri, Void, Bitmap> {

        private final int max_size = 300 * 1024;
        private ProgressDialog dialog;

        private LoadImageTask() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            picked_image.setImageResource(R.drawable.img_loading_01);
            dialog = DialogUtil.waitingDialog(PreviewActivity.this, "正在处理图片...");
        }

        @Override
        protected Bitmap doInBackground(Uri... params) {
            ContentResolver resolver = getContentResolver();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(resolver, params[0]);
                ByteArrayOutputStream bao = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bao);
                int size = bao.toByteArray().length;
                if (size > max_size) {
                    float scale = size / (float) max_size;
                    bitmap = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() / scale), (int) (bitmap.getHeight() / scale), false);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            try {
                picked_image.setImageBitmap(result);
                picked_image.requestFocus();
                dialog.dismiss();
                savedUri= new ImageUtil().saveBitmap(result, PIC_NAME);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
