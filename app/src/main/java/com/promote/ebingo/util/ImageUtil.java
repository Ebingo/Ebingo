package com.promote.ebingo.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by acer on 2014/9/9.
 */
public class ImageUtil {

    private String getImageFile(){
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
    }

    /** 保存方法 */
    public Uri saveBitmap(Bitmap bm,String picName) {
        File f = new File(getImageFile(), picName);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return Uri.fromFile(f);
    }
    public Bitmap readBitmap(String picName){
        Bitmap bitmap=null;
        try {
            File f = new File(getImageFile(),picName);
            FileInputStream inputStream = new FileInputStream(f);
            bitmap= BitmapFactory.decodeFile(f.getAbsolutePath());
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
