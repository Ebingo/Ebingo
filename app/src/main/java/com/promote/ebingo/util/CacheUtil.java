package com.promote.ebingo.util;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by acer on 2014/9/25.
 */
public class CacheUtil {

    public static final String FILE_PROVINCE_LIST ="province_list";

    public void save(Context context,String name, Object o) {
        FileOutputStream os = null;
        try {
            os = context.openFileOutput(name, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(o);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Object read(Context context,String name) {
        Object obj = null;
        try {
            FileInputStream is = context.openFileInput(name);
            ObjectInputStream ois = new ObjectInputStream(is);
            obj = ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return obj;
    }
}
