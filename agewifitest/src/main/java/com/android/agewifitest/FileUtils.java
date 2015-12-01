package com.android.agewifitest;

import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2015/11/24.
 */
public class FileUtils {
    /**
     * 获得SDCard的路径
     */
    public static String getExternalStorageDirectory() {
        String path = Environment.getExternalStorageDirectory().getPath();
        return path;
    }

    /**
     * 复制文件
     *
     * @param sdFile :目标文件
     * @params assetFile :被复制文件的输入流
     */
    public static Boolean copyFileToSd(InputStream assetFile, File sdFile) {
        boolean flags = false;
        try {
            FileOutputStream fos = new FileOutputStream(sdFile);
            byte[] buffer = new byte[1024];
            int count;
            while ((count = assetFile.read(buffer)) > 0) {
                fos.write(buffer, 0, count);
            }
            flags = true;
            fos.close();
            assetFile.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flags;
    }
}
