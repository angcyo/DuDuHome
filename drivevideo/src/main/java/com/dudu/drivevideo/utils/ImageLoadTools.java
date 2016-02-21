package com.dudu.drivevideo.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dengjun on 2016/1/2.
 * Description :
 */
public class ImageLoadTools {
    public static List<Uri> initImageUriList(String filePath, String fileNamePrefix, String minNumFileNameSuffix, String fileType){
        List<Uri> imageUriList = null;
        File imageFileDir = new File(filePath);
        if (imageFileDir.isDirectory()){
            imageUriList = new ArrayList<Uri>();
            int imageFileNum = imageFileDir.list().length;
            int minNumFile = Integer.valueOf(minNumFileNameSuffix);
            int suffixLength = minNumFileNameSuffix.length();
            for (int i = minNumFile; i < (minNumFile +imageFileNum); i++){
                String suffix = "000000"+ String.valueOf(i);
                String curFileName = filePath+fileNamePrefix+suffix.substring(suffix.length() -suffixLength)+fileType;
                Uri uri = Uri.parse("file://"+curFileName);
                imageUriList.add(uri);
            }
        }
        return imageUriList;
    }

    public static List<String> initImagePathList(String filePath, String fileNamePrefix, String minNumFileNameSuffix, String fileType){
        List<String> imageUriList = null;
        File imageFileDir = new File(filePath);
        if (imageFileDir.isDirectory()){
            imageUriList = new ArrayList<String>();
            int imageFileNum = imageFileDir.list().length;
            int minNumFile = Integer.valueOf(minNumFileNameSuffix);
            int suffixLength = minNumFileNameSuffix.length();
            for (int i = minNumFile; i < (minNumFile +imageFileNum); i++){
                String suffix = "000000"+ String.valueOf(i);
                String curFileName = filePath+fileNamePrefix+suffix.substring(suffix.length() -suffixLength)+fileType;

                imageUriList.add(curFileName);
            }
        }
        return imageUriList;
    }

    public static List<Bitmap> getBitmapWithFilePath(List<String> filePathlist){
        List<Bitmap> bitmapList = new ArrayList<Bitmap>();
        for (String filePath: filePathlist){
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_4444;
                bitmapList.add(BitmapFactory.decodeFile(filePath, options));
            } catch (Exception e) {

            }
        }
        return bitmapList;
    }



    public static void sleep(int millisecond){
        try {
            Thread.sleep(millisecond);
        } catch (InterruptedException e) {
        }
    }



    public static List<Uri> getDirPhotoUriList(String dirPath){
        File dir = new File(dirPath);
        List<Uri> uriList = null;
        if (dir.exists()){
            File[] files = dir.listFiles();
            uriList = new ArrayList<Uri>();
            for (File file: files){
                if (file.getName().endsWith(".jpg")){
                    uriList.add(Uri.parse("file://"+file.getAbsolutePath()));
                }
            }
            if (uriList.size() == 0){
                uriList = null;
            }
        }
        return uriList;
    }
}
