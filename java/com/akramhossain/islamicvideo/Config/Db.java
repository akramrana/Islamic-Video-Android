package com.akramhossain.islamicvideo.Config;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Lenovo on 8/26/2018.
 */

public class Db extends SQLiteOpenHelper {

    private static String DB_NAME = "islamic_video.db";
    private SQLiteDatabase db;

    private static final String TABLE_WATCH_HISTORY_CREATE = "CREATE TABLE 'watch_history' (" +
            "'watch_history_id' INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE , " +
            "'video_id' INTEGER, " +
            "'category_id' INTEGER, " +
            "'category_name' CHAR, " +
            "'title' CHAR,"+
            "'source' CHAR,"+
            "'url' TEXT,"+
            "'image' TEXT," +
            "'youtube_video_id' CHAR"+
            ")";

    private static final String TABLE_FAVORITE_CREATE = "CREATE TABLE 'favorite' (" +
            "'favorite_id' INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE , " +
            "'video_id' INTEGER, " +
            "'category_id' INTEGER, " +
            "'category_name' CHAR, " +
            "'title' CHAR,"+
            "'source' CHAR,"+
            "'url' TEXT,"+
            "'image' TEXT," +
            "'youtube_video_id' CHAR"+
            ")";

    public Db(Context context) {
        super(context, DB_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(TABLE_WATCH_HISTORY_CREATE);
        db.execSQL(TABLE_FAVORITE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS watch_history");
        db.execSQL("DROP TABLE IF EXISTS favorite");
        onCreate(db);
    }

    public void insertWatchHistory(String id, String category_id, String category_name, String title, String source, String url, String image, String youtubeVideoId) {
        ContentValues values = new ContentValues();
        values.put("video_id",id);
        values.put("category_id",category_id);
        values.put("category_name",category_name);
        values.put("title",title);
        values.put("source",source);
        values.put("url",url);
        values.put("image",image);
        values.put("youtube_video_id",youtubeVideoId);
        this.getWritableDatabase().insertOrThrow("watch_history", "", values);
    }

    public void addToFavorite(String id, String category_id, String category_name, String title, String source, String url, String image, String youtubeVideoId) {
        ContentValues values = new ContentValues();
        values.put("video_id",id);
        values.put("category_id",category_id);
        values.put("category_name",category_name);
        values.put("title",title);
        values.put("source",source);
        values.put("url",url);
        values.put("image",image);
        values.put("youtube_video_id",youtubeVideoId);
        this.getWritableDatabase().insertOrThrow("favorite", "", values);
    }
}
