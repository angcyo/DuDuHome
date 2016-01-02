package com.dudu.android.ext.media.domain.services;

import android.os.Environment;

import java.io.File;

/**
 * Created by Administrator on 2015/12/9.
 */
public class VideoUtils {

    private static String T_FLASH_PATH = "/storage/sdcard1";

    public static String getMainDirName() {
        return "/dudu";
    }

    /**
     * 获取录像存储目录
     */
    public static File getVideoStorageDir() {
        File dir = new File(getStorageDir(), "/video");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        makeNoMediaFile(dir);
        return dir;
    }

    public static void makeNoMediaFile(File dir) {
        try {
            File f = new File(dir, ".nomedia");
            if (!f.exists()) {
                f.createNewFile();
            }
        } catch (Exception e) {
        }
    }

    /**
     * 获取临时目录
     */
    public static File getStorageDir() {
        File dir;
        if (isTFlashCardExists()) {
            dir = new File(T_FLASH_PATH, getMainDirName());
        } else {
            dir = new File(Environment.getExternalStorageDirectory(), getMainDirName());
        }

        if (!dir.exists()) {
            dir.mkdirs();
        }

        return dir;
    }

    public static boolean isTFlashCardExists() {
        return new File(T_FLASH_PATH, "Android").exists();
    }

    public static double getTFlashCardSpace() {
        File dir;
        if (isTFlashCardExists()) {
            dir = new File(T_FLASH_PATH);
            return dir.getTotalSpace() * 0.8;
        }

        return 0;
    }

    public static double getTFlashCardFreeSpace() {
        File dir;
        if (isTFlashCardExists()) {
            dir = new File(T_FLASH_PATH);
            return dir.getFreeSpace();
        }

        return 0;
    }

}
