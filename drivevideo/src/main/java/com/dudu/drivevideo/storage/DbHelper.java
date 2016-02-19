package com.dudu.drivevideo.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.dudu.commonlib.CommonLib;
import com.dudu.drivevideo.model.VideoEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper {

    private final static String TAG = "dbhelper";

    private Logger mLogger;

    private final static String DATABASE_NAME = "launcher.db";

    private final static int DB_VERSION = 1;

    public static final String VIDEO_TABLE_NAME = "video";
    public static final String VIDEO_COLUMN_ID = "_id";
    public static final String VIDEO_COLUMN_NAME = "name";
    public static final String VIDEO_COLUMN_STATUS = "status";// 0代表未锁定， 1代表已锁定
    public static final String VIDEO_COLUMN_CREATE_TIME = "create_time";
    public static final String VIDEO_COLUMN_PATH = "path";
    public final static String VIDEO_COLUMN_SIZE = "size";


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
        mLogger = LoggerFactory.getLogger(TAG);
    }

    public static DbHelper getDbHelper() {
        if (mDbHelper == null) {
            mDbHelper = new DbHelper(CommonLib.getInstance().getContext());
        }
        return mDbHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase sdb) {
        sdb.execSQL(CREATE_VIDEO_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS notes");
        onCreate(db);
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
        mLogger.debug("getAllVideos: ");
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
                    if (!TextUtils.isEmpty(name) /*&& FileUtils.isTFlashCardExists()*/) {
                        mLogger.debug("getAllVideos: "+name+" doesn't exist");
                        deleteVideo(name);
                    }
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

    public List<VideoEntity> getAllVideosList() {
        mLogger.debug("getAllVideos: ");
        db = getWritableDatabase();
        List<VideoEntity> videos = new ArrayList<>();
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
                    if (!TextUtils.isEmpty(name) /*&& FileUtils.isTFlashCardExists()*/) {
                        mLogger.debug("getAllVideos: "+name+" doesn't exist");
                        deleteVideo(name);
                    }
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
        mLogger.debug("deleteVideo: "+name);
    }

    public void deleteOldestVideo() {
        mLogger.debug("删除时间最久的视频...");
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
            mLogger.debug("deleteOldestVideo: "+file.getName());
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
        ContentValues videos = new ContentValues();
        videos.put(VIDEO_COLUMN_NAME, video.getName());
        videos.put(VIDEO_COLUMN_PATH, video.getPath());
        videos.put(VIDEO_COLUMN_STATUS, video.getStatus());
        videos.put(VIDEO_COLUMN_CREATE_TIME, video.getCreateTime());
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
