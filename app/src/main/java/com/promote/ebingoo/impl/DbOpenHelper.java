package com.promote.ebingoo.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.promote.ebingoo.application.Constant;
import com.promote.ebingoo.util.LogCat;

public class DbOpenHelper extends SQLiteOpenHelper {

    public static final String DBNAME = "Ebingo.db";
    public static final String SEARCH_HISTORY = "search_history";
    public static final String DOWNLOADMUSIC_TBL = "downloadmusic";
    public static final String USER_TBL = "music_user";
    public static final String MUSICITEM_TBL = "musicitem";
    public static final String MUSICCATEGORY_TBL = "music_category";
    public static final String MUSICADV_TBL = "music_adv";
    public static final String NET_MUSIC = "net_music";
    public static final String BG_TBL = "bg_tbl";
    public static final String MUSIC_COLLECTION_DATA = "msuic_collection_data";
    public static final String MSG_TBL = "my_message";

    public DbOpenHelper(Context context) {
        super(context, DBNAME, null, Constant.DB_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        LogCat.v("test create table------");
        db.execSQL("CREATE TABLE if not exists " + SEARCH_HISTORY + "(" + "'id' INTEGER PRIMARY KEY, 'history' TEXT, 'search_type' INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + SEARCH_HISTORY);
        onCreate(db);
    }

}
