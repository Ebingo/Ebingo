package com.promote.ebingoo.publish;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.jch.lib.util.DialogUtil;
import com.promote.ebingoo.R;
import com.promote.ebingoo.util.ImageUtil;
import com.promote.ebingoo.util.LogCat;

import java.io.IOException;

/**
 * Created by acer on 2014/9/9.
 */
public class PreviewActivity extends Activity implements View.OnClickListener {
    public static final String PIC_NAME = "ebingoo_preview.png";
    private static boolean isPreviewing = false;
    private Uri uri;
    private Uri savedUri;
    private ImageView picked_image;

    /**
     * @return if this activity is Previewing pictures
     */
    public static boolean isPreviewing() {
        return isPreviewing;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preview_activity);
        findViewById(R.id.common_back_btn).setOnClickListener(this);
        picked_image = (ImageView) findViewById(R.id.dial);
        uri = getIntent().getData();
        new LoadImageTask().execute(uri);
        isPreviewing = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isPreviewing = false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.common_back_btn:
                finish();
                break;
            case R.id.commit_title_done:
                Intent data = new Intent();
                data.setData(savedUri);
                setResult(RESULT_OK, data);
                finish();
                break;
        }
    }

    /**
     * 根据Uri来加载一个图片，并压缩
     */
    private class LoadImageTask extends AsyncTask<Uri, Void, Bitmap> {

        private final int max_size = 200 * 1024;//图片大小
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
            Bitmap result = null;
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(getContentResolver().openInputStream(params[0]), null, options);
                int size = options.outWidth * options.outHeight;
                LogCat.i("--->", "size=" + size + " size/max=" + (size / max_size));
                if (size > max_size) {
                    options.inSampleSize = size / max_size;
                    options.inJustDecodeBounds = false;
                    result = BitmapFactory.decodeStream(getContentResolver().openInputStream(params[0]), null, options);
                } else {
                    result = BitmapFactory.decodeStream(getContentResolver().openInputStream(params[0]));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            try {
                picked_image.setImageBitmap(result);
                picked_image.requestFocus();
                dialog.dismiss();
                savedUri = ImageUtil.saveBitmap(result, PIC_NAME);
//                if (!result.isRecycled()) result.recycle();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
