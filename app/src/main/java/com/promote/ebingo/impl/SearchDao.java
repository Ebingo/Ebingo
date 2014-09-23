package com.promote.ebingo.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.promote.ebingo.bean.SearchHistoryBean;

import java.util.ArrayList;

/**
 * Created by ACER on 2014/9/2.
 */
public class SearchDao {

    private static DbOpenHelper mHelper;
    private Context mContext;

    public SearchDao(Context context) {

        this.mContext = context;
        mHelper = new DbOpenHelper(context);
    }

    /**
     * 保存历史记录。
     *
     * @param history
     */
    public synchronized void addHistory(String history) {


        SQLiteDatabase db = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("history", history);
        db.insert(DbOpenHelper.SEARCH_HISTORY, null, values);
        db.close();
    }

    /**
     * 获取历史记录。
     *
     * @return ArrayList<SearchHistoryBean>
     */
    public synchronized ArrayList<SearchHistoryBean> getHistorys() {

        SQLiteDatabase db = mHelper.getWritableDatabase();
        Cursor cursor = db.query(true, DbOpenHelper.SEARCH_HISTORY, new String[]{"history"}, null, null, null, null, "id desc", String.valueOf(7));
        ArrayList<SearchHistoryBean> histories = new ArrayList<SearchHistoryBean>();

        while (cursor.moveToNext()) {
            SearchHistoryBean historyBean = new SearchHistoryBean();
            historyBean.setHistory(cursor.getString(cursor.getColumnIndex("history")));
            histories.add(historyBean);
        }

        cursor.close();
        db.close();
        return histories;
    }

    /**
     * 清空历史记录。
     */
    public synchronized void clearHistory() {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        String sqlStr = "delete from " + DbOpenHelper.SEARCH_HISTORY;
        db.execSQL(sqlStr);
        db.close();
    }

}
