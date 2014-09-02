package com.promote.ebingo.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.promote.ebingo.application.Constant;
import com.promote.ebingo.util.LogCat;

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

        db.execSQL("CREATE TABLE if not exists " + SEARCH_HISTORY + "(" + "'id' INTEGER PRIMARY KEY, 'history' TEXT)" );

//		db.execSQL("CREATE TABLE if not exists "
//				+ DOWNLOADMUSIC_TBL
//				+ "("
//				+ "'music_id' INTEGER PRIMARY KEY, 'music_name' TEXT, 'singer' TEXT, 'lyric_url' TEXT, 'music_icon_url' TEXT, 'price' INTEGER, 'music_file_demo_url' TEXT, 'music_file_url' TEXT, 'file_size' TEXT, 'isBuy' INTEGER, 'downloadstate' INTEGER, 'total' INTEGER NOT NULL DEFAULT 0, 'progress' INTEGER DEFAULT 0, 'file_path' TEXT, 'temp_file_path' TEXT)");
//
//		db.execSQL("CREATE TABLE if not exists " + USER_TBL + "("
//				+ "'id' INTEGER PRIMARY KEY AUTOINCREMENT, 'afid' TEXT NOT NULL, 'pwd' TEXT)");
//		db.execSQL("CREATE TABLE if not exists "
//				+ MUSICCATEGORY_TBL
//				+ "("
//				+ "'category_id' INTEGER NOT NULL UNIQUE, 'category_name' TEXT, 'cat_ent_num' INTEGER, 'cat_ent_time' INTEGER, 'cat_music_download' INTEGER)");
//		db.execSQL("CREATE TABLE if not exists " + MUSICADV_TBL + "("
//				+ "'sub_id' INTEGER NOT NULL, 'pic_url' TEXT, 'type' TEXT, 'click_url' TEXT)");
//		db.execSQL("CREATE TABLE if not exists "
//				+ NET_MUSIC
//				+ "("
//				+ "'music_id' INTEGER PRIMARY KEY, 'music_name' TEXT, 'singer' TEXT, 'lyric_url' TEXT, 'music_icon_url' TEXT, 'price' INTEGER, 'music_file_demo_url' TEXT, 'music_file_url' TEXT, 'file_size' TEXT, 'isBuy' INTEGER, 'music_backgd_pic_url' TEXT)");
//
//		db.execSQL("CREATE TABLE if not exists " + MUSIC_COLLECTION_DATA + "("
//				+ "'music_id' INTEGER, 'music_name' TEXT, 'singer' TEXT,'play_num' INTEGER, 'play_length' INTEGER)");
//
//		db.execSQL("CREATE TABLE if not exists " + MSG_TBL + "("
//				+ "'msg_id' INTEGER,'msg_title' TEXT, 'msg_pic' TEXT,'msg_content' TEXT, 'msg_date' TEXT,'msg_stat' INTEGER)");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
          db.execSQL("drop table if exists" + SEARCH_HISTORY);
//        db.execSQL("drop table if exists " + DOWNLOADMUSIC_TBL);
//        db.execSQL("drop table if exists " + USER_TBL);
//        db.execSQL("drop table if exists " + MUSICCATEGORY_TBL);
//        db.execSQL("drop table if exists " + MUSICADV_TBL);
//        db.execSQL("drop table if exists " + NET_MUSIC);
//        db.execSQL("drop table if exists " + BG_TBL);
//        db.execSQL("drop table if exists " + MUSIC_COLLECTION_DATA);
//        db.execSQL("drop table if exists " + MSG_TBL);
		onCreate(db);
	}

}
