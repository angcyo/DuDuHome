package com.dudu.aios.ui.map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dudu.android.launcher.LauncherApplication;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Administrator on 2016/2/14.
 */
public class MapDbHelper extends SQLiteOpenHelper {

    private final static String DATABASE_NAME = "navigation.db";
    private final static int DB_VERSION = 1;

    public final static String NAVIGATION_TABLE_NAME = "navi_history";
    public final static String NAVIGATION_COLUMN_ID = "_id";
    public final static String NAVIGATION_COLUMN_ADDRESS = "navi_address";
    public final static String NAVIGATION_COLUMN_PLACENAME = "navi_place_name";
    public final static String NAVIGATION_COLUMN_DISTANCE = "distance";
    public final static String NAVIGATION_COLUMN_SEARCH_TIME = "search_time";

    private static final String CREATE_NAVIGATION_TABLE_SQL = "create table if not exists "
            + NAVIGATION_TABLE_NAME
            + " ("
            + NAVIGATION_COLUMN_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + NAVIGATION_COLUMN_PLACENAME
            + " VARCHAR,"
            + NAVIGATION_COLUMN_ADDRESS
            + " VARCHAR,"
            + NAVIGATION_COLUMN_DISTANCE
            + " VARCHAR)";
    private SQLiteDatabase db;

    private static MapDbHelper mDbHelper;

    private MapDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
    }

    public static MapDbHelper getDbHelper() {
        if (mDbHelper == null) {
            mDbHelper = new MapDbHelper(LauncherApplication.getContext());
        }
        return mDbHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase sdb) {
        sdb.execSQL(CREATE_NAVIGATION_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS notes");
        onCreate(db);
    }

    public ArrayList<MapListItemObservable> getHistory() {
        db = getWritableDatabase();
        ArrayList<MapListItemObservable> historyList = new ArrayList<>();
        Cursor c = db.query(NAVIGATION_TABLE_NAME, null, null, null, null, null,
                NAVIGATION_COLUMN_SEARCH_TIME + " desc");
        if (c != null) {
            while (c.moveToNext()) {
                MapListItemObservable histiory = new MapListItemObservable();
                String placeName = c.getString(c
                        .getColumnIndexOrThrow(NAVIGATION_COLUMN_PLACENAME));
                String address = c.getString(c
                        .getColumnIndexOrThrow(NAVIGATION_COLUMN_PLACENAME));
                String distance = c.getString(c
                        .getColumnIndexOrThrow(NAVIGATION_COLUMN_DISTANCE));
                histiory.address.set(address);
                histiory.addressName.set(placeName);
                histiory.distance.set(distance);
                historyList.add(histiory);
            }

            c.close();
        }

        return historyList;
    }


    public void saveHistory(MapListItemObservable mapListObservable){
        db = getWritableDatabase();
        db.insertOrThrow(NAVIGATION_TABLE_NAME, null, getHistoryValues(mapListObservable));
    }

    private ContentValues getHistoryValues(MapListItemObservable naviHistory) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.getDefault());
        String dateString = format.format(new Date());

        ContentValues history = new ContentValues();
        history.put(NAVIGATION_COLUMN_PLACENAME, naviHistory.addressName.get());
        history.put(NAVIGATION_COLUMN_ADDRESS, naviHistory.address.get());
        history.put(NAVIGATION_COLUMN_DISTANCE, naviHistory.distance.get());
        history.put(NAVIGATION_COLUMN_SEARCH_TIME,dateString);
        return history;
    }

    public void deleteHistory(){


    }

}
