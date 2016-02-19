package com.dudu.drivevideo.utils;

import android.net.Uri;

import android.util.Log;

import com.dudu.drivevideo.config.DriveVideoContants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.realm.internal.IOException;

/**
 * Created by dengjun on 2016/2/19.
 * Description :
 */
public class TakePictureTools {
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private static Logger log = LoggerFactory.getLogger("video.drivevideo");

    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    public static File getOutputMediaFile(int type){


        File mediaStorageDir = FileUtil.getTFlashCardDirFile(DriveVideoContants.REAR_VIDEO_STORAGE_PARENT_PATH,
                DriveVideoContants.FRONT_PICTURE_STORAGE_PATH);

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            log.info("failed to create directory");
            return null;
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    public static void savePictureData(byte[] data, File pictureFile){
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(data);
            fos.close();
        } catch (FileNotFoundException e) {
            log.error("异常", e);
        } catch (IOException e) {
            log.error("异常", e);
        } catch (Exception e){
            log.error("异常", e);
        }

    }
}
