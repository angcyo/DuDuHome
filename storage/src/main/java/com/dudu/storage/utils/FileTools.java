package com.dudu.storage.utils;

import android.os.Environment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Created by dengjun on 2015/12/3.
 * Description :
 */
public class FileTools {
    private static String storagePath = "/dudu/message";

    private static Logger log = LoggerFactory.getLogger("storage");

    public static  String getExternalStorageDir(String filePath){
        return Environment.getExternalStorageDirectory()+ filePath;
    }

    public static String getStoragePath(){
        File file = new File(Environment.getExternalStorageDirectory(), storagePath);
        if (!file.exists()){
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }

    public static boolean deleteFile(String f) {
        if (f != null && f.length() > 0) {
            return deleteFile(new File(f));
        }
        return false;
    }

    public static boolean deleteFile(File f) {
        if (f != null && f.exists() && !f.isDirectory()) {
            return f.delete();
        }
        return false;
    }
}
