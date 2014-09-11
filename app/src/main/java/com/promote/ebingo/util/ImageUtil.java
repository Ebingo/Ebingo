package com.promote.ebingo.util;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;

import com.jch.lib.util.DialogUtil;
import com.jch.lib.util.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.promote.ebingo.application.HttpConstant;
import com.promote.ebingo.impl.EbingoRequestParmater;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by acer on 2014/9/9.
 */
public class ImageUtil {

    private static String getImageFile() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
    }

    /**
     * 保存方法
     */
    public static Uri saveBitmap(Bitmap bm, String picName) {
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

    public static Bitmap readBitmap(String picName) {
        Bitmap bitmap = null;
        try {
            File f = new File(getImageFile(), picName);
            FileInputStream inputStream = new FileInputStream(f);
            bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static String base64Encode(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        byte[] bytes = bos.toByteArray();
        try {
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "data:image/png;base64," + Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    /**
     * 以一个bitmap的中心为圆点，以radius为半径，去截取
     * @param src
     * @param radius
     * @return
     */
    public static Bitmap roundBitmap(Bitmap src, int radius) {

        int src_w=src.getWidth();
        int src_h=src.getHeight();
        int result_length=radius*2;
        LogCat.i("--->","src_w="+src_w+" src_h="+src_h);
        float scale;
        if(src_w>src_h){
            scale=result_length/(float)src_h;
        }else{
            scale=result_length/(float)src_w;
        }
        src_w*=scale;
        src_h*=scale;
        LogCat.i("--->","src_w="+src_w+" src_h="+src_h+" scale="+scale+" radius="+radius);
        Bitmap resizeSrc=Bitmap.createScaledBitmap(src,src_w,src_h,false);//缩放后的Bitmap

        final Paint paint=new Paint();
        paint.setAntiAlias(true);
        Bitmap result=Bitmap.createBitmap(result_length,result_length, Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(result);
//        canvas.drawARGB(0,0,0,0);//背景透明效果
        canvas.drawCircle(radius,radius,radius,paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        float translate_x=(result.getWidth()-resizeSrc.getWidth())/2;
        float translate_y=(result.getHeight()-resizeSrc.getHeight())/2;

        canvas.save();
        canvas.translate(translate_x,translate_y);
        canvas.drawBitmap(resizeSrc,0,0,paint);
        canvas.restore();

        return result;
    }
}
