package com.promote.ebingo.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.promote.ebingo.bean.Company;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by acer on 2014/9/25.
 */
public class FileUtil {

    public static final String FILE_CITY_LIST = "cityList";
    public static final String FILE_COMPANY = "company";
    public static final String FILE_PROVINCE_LIST = "provinceList";
    public static final String FILE_SUPPLY_LIST = "supplyList";
    public static final String FILE_DEMAND_LIST = "demandList";
    public static final String FILE_WISH_LIST = "wishList";
    public static final String HOEM_DATA_CACh = "home_data";
    public static final String CATEGORY_CACH = "category_data";
    public static final String FILE_CALL_RECORD = "call_record";

    /**
     * 读取缓存文件
     *
     * @param context
     * @param name
     * @return
     */
    public static Object readCache(Context context, String name) {
        return read(getCacheFile(context, name));
    }

    /**
     * @param context
     * @param name
     */
    public static void saveCache(Context context, String name, Object o) {
        save(getCacheFile(context, name), o);
    }

    /**
     * 读取一个非缓存文件
     *
     * @param context
     * @param name
     * @return
     */
    public Object readFile(Context context, String name) {
        return read(getFile(context, name));
    }

    /**
     * 讲一个对象保存到 一个非缓存文件中
     *
     * @param context
     * @param name
     * @param o
     */
    public static void saveFile(Context context, String name, Object o) {
        save(getFile(context, name), o);
    }

    /**
     * 将一个文件读出，这个文件必须为一个序列化的对象
     *
     * @param file
     * @return
     */
    public static Object read(File file) {
        Object obj = null;
        try {
            FileInputStream is = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(is);
            obj = ois.readObject();
        } catch (Exception e) {
            Log.w("FileUtil",e.getMessage());
        }
        LogCat.i("--->", "read:" + file + "===" + obj);
        return obj;
    }

    /**
     * 讲一个对象o写入到file中
     *
     * @param file
     * @param o
     */
    public static void save(File file, Object o) {
        LogCat.i("--->", "save:" + file + "--->" + o);
        try {
            FileOutputStream os = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(o);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除文件
     *
     * @param file
     */
    public static void delete(File file) {
        if (!file.isDirectory()) {
            file.delete();
        } else {
            for (File f : file.listFiles()){
                delete(f);
                LogCat.d("delete file ---->"+f.getName());
            }
        }
    }

    /**
     * 删除文件
     *
     * @param context
     * @param name
     */
    public static void deleteFile(Context context, String name) {
        delete(getFile(context, name));
    }

    /**
     * 缓存文件路径 cache/<company_id>/name
     *
     * @param context
     * @param name
     * @return
     */
    private static File getCacheFile(Context context, String name) {

        //创建缓存文件
        return new File(getCacheDir(context), name);
    }

    /**
     * 获取缓存目录
     *
     * @param context
     * @return
     */
    private static File getCacheDir(Context context) {
        File dir = new File(context.getCacheDir(), Company.getInstance().getCompanyId() + "");
        if (!dir.exists()) dir.mkdir();
        return dir;
    }

    private static File getFile(Context context, String name) {
        return new File(context.getFilesDir(), name);
    }

    public static void clearCache(Context context) {
        delete(getCacheDir(context));
    }
}
