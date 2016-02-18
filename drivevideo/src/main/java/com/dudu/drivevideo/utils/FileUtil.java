package com.dudu.drivevideo.utils;

import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dengjun on 2016/2/15.
 * Description :
 */
public class FileUtil {
    private static String T_FLASH_PATH = "/storage/sdcard1";
    private static final int BUFF_SIZE = 1024 * 1024; // 1M Byte


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

    public static String fileByte2Mb(double size) {
        double mbSize = size / 1024 / 1024;
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(mbSize);
    }

    public static double getTFlashCardSpace() {
        File tfCard = new File(T_FLASH_PATH);
        return tfCard.getTotalSpace()*0.8;
    }


    public static double getTFlashCardFreeSpace() {
        File tfCard = new File(T_FLASH_PATH);
        return tfCard.getFreeSpace();
    }

    private static void delectAllFiles(File root) {
        File files[] = root.listFiles();
        if (files != null)
            for (File f : files) {
                if (f.isDirectory()) { // 判断是否为文件夹
                    delectAllFiles(f);
                } else {
                    if (f.exists()) { // 判断是否存在
                        try {
                            f.delete();
                        } catch (Exception e) {
                        }
                    }
                }
            }
    }

    public static void clearLostDirFolder() {
        if (isTFlashCardExists()) {
            File root = new File(T_FLASH_PATH, "LOST.DIR");
            if (root != null && root.exists()) {
                delectAllFiles(root);
            }
        }
    }
}
