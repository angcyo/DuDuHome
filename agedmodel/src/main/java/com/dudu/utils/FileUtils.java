package com.dudu.utils;

import android.os.Environment;

/**
 * Created by Administrator on 2015/11/24.
 */
public class FileUtils {
    /**
     * 获得SDCard的路径
     * */
    public static String getExternalStorageDirectory(){
        String path= Environment.getExternalStorageDirectory().getPath();
        return path;
    }
}
