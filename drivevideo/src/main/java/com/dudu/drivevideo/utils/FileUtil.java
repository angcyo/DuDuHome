package com.dudu.drivevideo.utils;

import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dengjun on 2016/2/15.
 * Description :
 */
public class FileUtil {
    private static String T_FLASH_PATH = "/storage/sdcard1";
    private static final int BUFF_SIZE = 1024 * 1024; // 1M Byte


    public static double getTFlashCardSpace() {
        File dir;
        if (isTFlashCardExists()) {
            dir = new File(T_FLASH_PATH);
            return dir.getTotalSpace() * 0.8;
        }

        return 0;
    }

    public static File getTFlashCardDirFile(String parentDirName, String dirName){
        File dirFile = new File(getStorageDir(parentDirName), dirName);
        if (!dirFile.exists()){
            dirFile.mkdirs();
        }
        return dirFile;
    }

    public static File getStorageDir(String dirString) {
        File dir  = new File(T_FLASH_PATH, dirString);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }


    public static boolean isTFlashCardExists(){
        return testNewTfFile(T_FLASH_PATH);
    }

    public static boolean testNewTfFile(String filePath) {
        File testFile = new File(filePath, "testNewFile");
        boolean returnFlag = false;
        if (!testFile.exists()) {
            try {
                if (testFile.createNewFile()) {
                    returnFlag = true;
                    testFile.delete();
                }
            } catch (IOException e) {
                returnFlag = false;
            }
        } else {
            testFile.delete();
            returnFlag = true;
        }
        return returnFlag;
    }

    public static List<String> getDirFileNameList(String dir, String startString){
        File dirFile = new File(dir);
        List<String> fileNameList = new ArrayList<String>();
        if (dirFile.isDirectory()){
            File[] fileArray = dirFile.listFiles();
            for (File file:fileArray){
                if(file.getName().startsWith(startString) && !file.getName().equals("")){
                    fileNameList.add(file.getName());
                }
            }
        }
        return  fileNameList;
    }
}
