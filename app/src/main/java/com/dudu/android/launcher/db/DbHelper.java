package com.dudu.android.launcher.db;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dudu.android.launcher.LauncherApplication;
import com.dudu.android.launcher.bean.VideoEntity;

public class DbHelper extends SQLiteOpenHelper {

    private final static String DATABASE_NAME = "launcher.db";

    private final static int DB_VERSION = 1;

    public static final String VIDEO_TABLE_NAME = "video";
    public static final String VIDEO_COLUMN_ID = "_id";
    public static final String VIDEO_COLUMN_NAME = "name";
    public static final String VIDEO_COLUMN_STATUS = "status";// 0代表未锁定， 1代表已锁定
    public static final String VIDEO_COLUMN_CREATE_TIME = "create_time";
    public static final String VIDEO_COLUMN_PATH = "path";
    public final static String VIDEO_COLUMN_SIZE = "size";

    public final static String FLOW_TABLE_NAME = "flow";
    public final static String FLOW_COLUMN_ID = "_id";
    public final static String FLOW_COLUMN_UPLOAD = "UpFlow";
    public final static String FLOW_COLUMN_DOWNLOAD = "DownFlow";
    public final static String FLOW_COLUMN_TYPE = "type";
    public final static String FLOW_COLUMN_TIME = "time";

    private final static String CREATE_FLOW_TABLE_SQL = "create table if not exists "
            + FLOW_TABLE_NAME
            + " ("
            + FLOW_COLUMN_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + FLOW_COLUMN_UPLOAD
            + " Float,"
            + FLOW_COLUMN_DOWNLOAD
            + " Float,"
            + FLOW_COLUMN_TYPE
            + " INTEGER," + FLOW_COLUMN_TIME + " DATETIME)";

    // 新建一个表
    private static final String CREATE_VIDEO_TABLE_SQL = "create table if not exists "
            + VIDEO_TABLE_NAME
            + " ("
            + VIDEO_COLUMN_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + VIDEO_COLUMN_NAME
            + " VARCHAR,"
            + VIDEO_COLUMN_STATUS
            + " INTEGER,"
            + VIDEO_COLUMN_CREATE_TIME
            + " VARCHAR, "
            + VIDEO_COLUMN_PATH
            + " VARCHAR, " + VIDEO_COLUMN_SIZE + " VARCHAR)";

    private SQLiteDatabase db;

    private static DbHelper mDbHelper;

    private DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
    }

    public static DbHelper getDbHelper() {
        return getDbHelper(LauncherApplication.getContext());
    }

    public static DbHelper getDbHelper(Context context) {
        if (mDbHelper == null) {
            mDbHelper = new DbHelper(context);
        }
        return mDbHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase sdb) {
        sdb.execSQL(CREATE_FLOW_TABLE_SQL);
        sdb.execSQL(CREATE_VIDEO_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS notes");
        onCreate(db);
    }

    public void insertFlow(float UpFlow, float DownFlow, int WebType, Date date) {
        db = getWritableDatabase();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.getDefault());
        String dateString = format.format(date);
        String insertData = " INSERT INTO " + FLOW_TABLE_NAME + " ("
                + FLOW_COLUMN_UPLOAD + ", " + FLOW_COLUMN_DOWNLOAD + ","
                + FLOW_COLUMN_TYPE + "," + FLOW_COLUMN_TIME + " ) values("
                + UpFlow + ", " + DownFlow + "," + WebType + "," + "datetime('"
                + dateString + "'));";
        db.execSQL(insertData);
    }

    public float calculateForMonth(int year, int Month, int netType) {
        db = getWritableDatabase();
        Cursor c = fetchMonthFlow(year, Month, netType);
        float sum;
        float monthSum = 0;
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    int upColumn = c.getColumnIndex("monthUp");
                    int dwColumn = c.getColumnIndex("monthDw");
                    sum = c.getFloat(upColumn) + c.getFloat(dwColumn);
                    monthSum += sum;
                } while (c.moveToNext());
            }

            c.close();
        }

        return monthSum;
    }

    public Cursor fetchMonthFlow(int year, int Month, int netType) {
        db = getWritableDatabase();
        StringBuffer date = new StringBuffer();
        date.append(String.valueOf(year) + "-");
        if (Month < 10) {
            date.append("0" + String.valueOf(Month) + "-");
        } else {
            date.append(String.valueOf(Month) + "-");
        }

        Cursor c = db.query(FLOW_TABLE_NAME, new String[]{
                        "sum(" + FLOW_COLUMN_UPLOAD + ") AS monthUp",
                        "sum(" + FLOW_COLUMN_DOWNLOAD + ") as monthDw"},
                FLOW_COLUMN_TYPE + "=" + netType + " and " + FLOW_COLUMN_TIME
                        + " LIKE '" + date.toString() + "%'", null, null, null,
                null, null);
        return c;
    }

    public boolean checkRecord(int netType, Date date) {
        db = getWritableDatabase();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd",
                Locale.getDefault());
        String dateString = format.format(date);
        Cursor c = db.query(FLOW_TABLE_NAME, new String[]{
                FLOW_COLUMN_UPLOAD + " AS upPro",
                FLOW_COLUMN_DOWNLOAD + " AS dwPro"}, FLOW_COLUMN_TYPE + "="
                + netType + " and " + FLOW_COLUMN_TIME + " like '" + dateString
                + "%'", null, null, null, null, null);
        boolean hasRecord = c != null && c.moveToNext() ? true : false;
        c.close();
        return hasRecord;
    }

    public Cursor getRecord(int netType, Date date) {
        db = getWritableDatabase();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd",
                Locale.getDefault());
        String dateString = format.format(date);
        Cursor c = db.query(FLOW_TABLE_NAME, new String[]{
                FLOW_COLUMN_UPLOAD + " AS upPro",
                FLOW_COLUMN_DOWNLOAD + " AS dwPro"}, FLOW_COLUMN_TYPE + "="
                + netType + " and " + FLOW_COLUMN_TIME + " like '" + dateString
                + "%'", null, null, null, null, null);
        return c;
    }

    public float getProFlowUp(int netType, Date date) {
        db = getWritableDatabase();
        Cursor c = getRecord(netType, date);
        float up = 0;
        if (c.moveToNext()) {
            up = c.getFloat(c.getColumnIndex("upPro"));
        }

        c.close();
        return up;
    }

    public float getProFlowDw(int netType, Date date) {
        db = getWritableDatabase();
        Cursor c = getRecord(netType, date);
        float up = 0;
        if (c.moveToNext()) {
            up = c.getFloat(c.getColumnIndex("dwPro"));
        }

        c.close();
        return up;
    }

    public void updateFlow(float down, float up, int webType, Date date) {
        db = getWritableDatabase();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd",
                Locale.getDefault());
        String dataString = format.format(date);
        String updateData = "UPDATE " + FLOW_TABLE_NAME + " SET "
                + FLOW_COLUMN_UPLOAD + "=" + up + " , " + FLOW_COLUMN_DOWNLOAD
                + "=" + down + " WHERE " + webType + "=" + webType + " and "
                + FLOW_COLUMN_TIME + " like '" + dataString + "%'";
        db.execSQL(updateData);
    }

    public VideoEntity getVideo(int id) {
        db = getWritableDatabase();
        Cursor c = db.query(VIDEO_TABLE_NAME, null, VIDEO_COLUMN_ID + "=?",
                new String[]{id + ""}, null, null, null);
        if (c != null && c.moveToFirst()) {
            VideoEntity video = new VideoEntity();
            String name = c.getString(c
                    .getColumnIndexOrThrow(VIDEO_COLUMN_NAME));
            int status = c.getInt(c.getColumnIndexOrThrow(VIDEO_COLUMN_STATUS));
            String path = c.getString(c
                    .getColumnIndexOrThrow(VIDEO_COLUMN_PATH));
            String size = c.getString(c
                    .getColumnIndexOrThrow(VIDEO_COLUMN_SIZE));
            String createTime = c.getString(c
                    .getColumnIndexOrThrow(VIDEO_COLUMN_CREATE_TIME));
            c.close();

            video.setName(name);
            video.setStatus(status);
            File file = new File(path, name);
            if (file.exists()) {
                video.setFile(file);
            }

            video.setFile(file);
            video.setPath(path);
            video.setSize(size);
            video.setCreateTime(createTime);
            return video;
        }

        return null;
    }

    public LinkedList<VideoEntity> getAllVideos() {
        db = getWritableDatabase();
        LinkedList<VideoEntity> videos = new LinkedList<>();
        Cursor c = db.query(VIDEO_TABLE_NAME, null, null, null, null, null,
                VIDEO_COLUMN_CREATE_TIME + " desc");
        if (c != null) {
            while (c.moveToNext()) {
                VideoEntity video = new VideoEntity();
                String name = c.getString(c
                        .getColumnIndexOrThrow(VIDEO_COLUMN_NAME));
                int status = c.getInt(c
                        .getColumnIndexOrThrow(VIDEO_COLUMN_STATUS));
                String path = c.getString(c
                        .getColumnIndexOrThrow(VIDEO_COLUMN_PATH));
                String size = c.getString(c
                        .getColumnIndexOrThrow(VIDEO_COLUMN_SIZE));
                String createTime = c.getString(c
                        .getColumnIndexOrThrow(VIDEO_COLUMN_CREATE_TIME));

                video.setName(name);
                video.setStatus(status);
                File file = new File(path, name);
                if (file.exists()) {
                    video.setFile(file);
                } else {
                    continue;
                }

                video.setFile(file);
                video.setPath(path);
                video.setSize(size);
                video.setCreateTime(createTime);
                videos.add(video);
            }

            c.close();
        }

        return videos;
    }

    public List<VideoEntity> getVideos(int first, int max) {
        db = getWritableDatabase();
        List<VideoEntity> videos = new ArrayList<VideoEntity>();
        String sql = "select * from " + VIDEO_TABLE_NAME + " order by " + VIDEO_COLUMN_CREATE_TIME +
                " desc limit " + first + "," + max;
        Cursor c = db.rawQuery(sql, null);
        if (c != null) {
            while (c.moveToNext()) {
                VideoEntity video = new VideoEntity();
                String name = c.getString(c
                        .getColumnIndexOrThrow(VIDEO_COLUMN_NAME));
                int status = c.getInt(c
                        .getColumnIndexOrThrow(VIDEO_COLUMN_STATUS));
                String path = c.getString(c
                        .getColumnIndexOrThrow(VIDEO_COLUMN_PATH));
                String size = c.getString(c
                        .getColumnIndexOrThrow(VIDEO_COLUMN_SIZE));
                String createTime = c.getString(c
                        .getColumnIndexOrThrow(VIDEO_COLUMN_CREATE_TIME));

                video.setName(name);
                video.setStatus(status);
                File file = new File(path, name);
                if (file.exists()) {
                    video.setFile(file);
                } else {
                    continue;
                }

                video.setFile(file);
                video.setPath(path);
                video.setSize(size);
                video.setCreateTime(createTime);
                videos.add(video);
            }

            c.close();
        }

        return videos;
    }

    public void insertVideo(VideoEntity video) {
        db = getWritableDatabase();
        db.insertOrThrow(VIDEO_TABLE_NAME, null, getVideoValues(video));
    }

    public void deleteVideo(String name) {
        db = getWritableDatabase();
        db.delete(VIDEO_TABLE_NAME, VIDEO_COLUMN_NAME + "=?",
                new String[]{name});
    }

    public void deleteOldestVideo() {
        db = getWritableDatabase();
        Cursor c = db.rawQuery(
                "select _id, min(create_time) from video where status=0", null);
        if (c != null && c.moveToFirst()) {
            int id = c.getInt(c.getColumnIndexOrThrow(VIDEO_COLUMN_ID));
            VideoEntity video = getVideo(id);
            if (video == null) {
                return;
            }

            File file = video.getFile();
            if (file != null && file.exists()) {
                file.delete();
            }

            db.delete(VIDEO_TABLE_NAME, "_id=?", new String[]{id + ""});

            c.close();
        }
    }

    public float getTotalSize() {
        db = getWritableDatabase();
        Cursor c = db.rawQuery("select sum(" + VIDEO_COLUMN_SIZE + ") from "
                + VIDEO_TABLE_NAME, null);
        if (c != null && c.moveToFirst()) {
            float total = c.getFloat(0);
            c.close();
            return total;
        }

        return 0;
    }

    public boolean isAllVideoLocked() {
        db = getWritableDatabase();
        Cursor c = db.query(VIDEO_TABLE_NAME, null, null, null, null, null,
                null);
        if (c == null) {
            return false;
        }

        while (c.moveToNext()) {
            if (c.getInt(c.getColumnIndexOrThrow(VIDEO_COLUMN_STATUS)) == 0) {
                return false;
            }
        }

        if (c != null) {
            c.close();
        }

        return true;
    }

    public void updateVideoStatus(String name, int status) {
        db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(VIDEO_COLUMN_STATUS, status);
        db.update(VIDEO_TABLE_NAME, values, VIDEO_COLUMN_NAME + "=?",
                new String[]{name + ""});
    }

    private ContentValues getVideoValues(VideoEntity video) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.getDefault());
        String dateString = format.format(new Date());
        ContentValues videos = new ContentValues();
        videos.put(VIDEO_COLUMN_NAME, video.getName());
        videos.put(VIDEO_COLUMN_PATH, video.getPath());
        videos.put(VIDEO_COLUMN_STATUS, video.getStatus());
        videos.put(VIDEO_COLUMN_CREATE_TIME, dateString);
        videos.put(VIDEO_COLUMN_SIZE, video.getSize());
        return videos;
    }

    public int getVideoTotalCount() {
        db = getWritableDatabase();
        int totalCount = 0;
        String sql = "select count(*) from " + VIDEO_TABLE_NAME;
        Cursor c = db.rawQuery(sql, null);
        if (c != null && c.moveToFirst()) {
            totalCount = c.getInt(0);
            c.close();
        }

        return totalCount;
    }

}
